package cc.wecando.miuihook

import android.util.Log
import cc.wecando.miuihook.SpellBook.getApplicationVersion
import cc.wecando.miuihook.base.Version
import cc.wecando.miuihook.base.WaitChannel
import cc.wecando.miuihook.parser.ApkFile
import cc.wecando.miuihook.parser.ClassTrie
import cc.wecando.miuihook.util.BasicUtil.tryAsynchronously
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 用于记录所有关于 Wechat 的关键全局变量
 */
object SecurityGlobal {

    /**
     * 若初始化操作耗费2秒以上, 视作初始化失败, 直接让微信开始正常运行
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val INIT_TIMEOUT = 5000L // ms

    /**
     * 用于防止其他线程在初始化完成之前访问 WechatGlobal的变量
     */
    private val initChannel = WaitChannel()

    /**
     * 微信版本
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile
    var versivon: Version? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 微信包名（用于处理多开的情况）
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile
    var packageName: String = ""
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 微信 APK 所使用的 ClassLoader, 用于加载 Class 对象
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile
    var loader: ClassLoader? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 微信 APK 所包含的全部类名, 依据 package 结构组织在一起, 用于动态适配不同的微信版本
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile
    var classes: ClassTrie? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 单元测试模式的开关, 只应该在单元测试中打开
     */
    @Volatile
    var wxUnitTestMode: Boolean = false


    /**
     * 创建一个惰性求值对象, 只有被用到的时候才会自动求值
     *
     * 当单元测试模式开启的时候, 会使用不同的 Lazy Implementation 辅助测试
     *
     * @param name 对象名称, 打印错误日志的时候会用到
     * @param initialVersion 大于等于指定版本才需要初始化
     * @param initializer 用来求值的回调函数
     */
    inline fun <reified T> wxLazy(
        name: String,
        initialVersion: Version? = null,
        crossinline initializer: () -> T?
    ): Lazy<T> {
        val clazz = T::class.java
        return if (wxUnitTestMode) {
            UnitTestLazyImpl {
                if (initialVersion == null || versivon!! >= initialVersion) {
                    initializer() ?: throw Error("Failed to evaluate $name")
                } else {
                    createDefaultValueForUnusedVersion(clazz)
                }
            }
        } else {

            lazy(LazyThreadSafetyMode.PUBLICATION) {
                when (null) {
                    versivon -> throw Error("Invalid wxVersion")
                    packageName -> throw Error("Invalid wxPackageName")
                    loader -> throw Error("Invalid wxLoader")
                    classes -> throw Error("Invalid wxClasses")
                }
                if (initialVersion == null || versivon!! >= initialVersion) {
                    initializer() ?: throw Error("Failed to evaluate $name")
                } else {
                    createDefaultValueForUnusedVersion(clazz)
                }

            }
        }
    }

    val unUsedMethod: Method = Any::class.java.methods[0]
    val unUsedClazz = Any::class.java
    val unUsedField: Field = StringBuffer::class.java.declaredFields[0]

    /**
     * 某些版本不需要初始化, 这里返回一个默认值
     */
    inline fun <reified T> createDefaultValueForUnusedVersion(clazz: Class<T>): T {
        return (if (clazz == Method::class.java) unUsedMethod else if (clazz == Field::class.java) unUsedField else unUsedClazz) as T
    }


    /**
     * 用来帮助单元测试的一个 Lazy Implementation, 允许开发者多次初始化一个惰性求值对象
     */
    class UnitTestLazyImpl<out T>(private val initializer: () -> T) : Lazy<T>,
        java.io.Serializable {
        @Volatile
        private var lazyValue: Lazy<T> = lazy(initializer)

        fun refresh() {
            lazyValue = lazy(initializer)
        }

        override val value: T
            get() = lazyValue.value

        override fun toString(): String = lazyValue.toString()

        override fun isInitialized(): Boolean = lazyValue.isInitialized()
    }

    /**
     * 初始化当前的 [SecurityGlobal]
     *
     * @param lpparam 通过重载 [IXposedHookLoadPackage.handleLoadPackage] 方法拿到的
     * [XC_LoadPackage.LoadPackageParam] 对象
     */
    @JvmStatic
    fun init(lpparam: XC_LoadPackage.LoadPackageParam) {
        tryAsynchronously {
            if (initChannel.isDone()) {
                return@tryAsynchronously
            }

            try {
                versivon = getApplicationVersion(lpparam.packageName)
                packageName = lpparam.packageName
                loader = lpparam.classLoader

                ApkFile(lpparam.appInfo.sourceDir).use {
                    classes = it.classTypes
                }

                Log.e(
                    "anti-dev",
                    "wxVersion:${versivon},wxPackageName${packageName},wxClasses:${classes}"
                )
            } catch (e: Exception) {
                Log.e("anti-dev", "SpellBook init error", e)
            } finally {
                initChannel.done()
            }
        }
    }
}