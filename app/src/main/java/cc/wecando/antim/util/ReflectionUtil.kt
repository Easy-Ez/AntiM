package cc.wecando.antim.util

import android.util.Log
import cc.wecando.antim.base.Classes
import cc.wecando.antim.parser.ApkFile
import cc.wecando.antim.parser.ClassTrie
import cc.wecando.antim.SecurityGlobal
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookMethod
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap

/**
 * 封装了一批关于 Reflection 的方法, 用来辅助 [Classes] 进行自动适配
 */
object ReflectionUtil {
    /**
     * 利用 Reflection 对指定对象进行浅拷贝
     */
    @JvmStatic
    fun shadowCopy(obj: Any, copy: Any, clazz: Class<*>? = obj::class.java) {
        if (clazz == null) {
            return
        }
        shadowCopy(obj, copy, clazz.superclass)
        clazz.declaredFields.forEach {
            it.isAccessible = true
            it.set(copy, it.get(obj))
        }
    }

    /**
     * 用于缓存已经完成的[findClassesFromPackage]的搜索结果
     */
    private val classCache: MutableMap<String, Classes> = ConcurrentHashMap()

    @JvmStatic
    fun clearClassCache() {
        classCache.clear()
    }

    /**
     * 用于缓存已经完成的[findDeclaredMethodExact]的搜索结果
     */
    private val declaredMethodCache: MutableMap<String, Method?> = ConcurrentHashMap()

    /**
     * 用于缓存已经完成的[findMethodExact]的搜索结果
     */
    private val methodCache: MutableMap<String, Method?> = ConcurrentHashMap()

    /**
     * 用于缓存已经完成的[findConstructorExact]的搜索结果
     */
    private val constructorCache: MutableMap<String, Constructor<*>?> = ConcurrentHashMap()

    @JvmStatic
    fun clearMethodCache() {
        declaredMethodCache.clear()
        methodCache.clear()
    }

    /**
     * 查找一个确定的类, 如果不存在返回 null
     */
    @JvmStatic
    fun findClassIfExists(className: String, classLoader: ClassLoader): Class<*>? {
        try {
            return Class.forName(className, false, classLoader)
        } catch (throwable: Throwable) {
            if (SecurityGlobal.wxUnitTestMode) {
                throw throwable
            }
            Log.e("anti-dev", "findClassIfExists", throwable)
        }
        return null
    }

    /**
     * 查找指定包里指定深度的所有类
     *
     * 出于性能方面的考虑, 只有深度相等的类才会被返回, 比如搜索深度为0的时候, 就只返回这个包自己拥有的类, 不包括它
     * 里面其他包拥有的类.
     *
     * @param loader 用于取出 [Class] 对象的加载器
     * @param trie 整个 APK 的包结构, 由于 Java 的 [ClassLoader] 对象不支持读取所有类名, 我们必须先通过其他手段
     * 解析 APK 结构, 然后才能检索某个包内的所有类, 详情请参见 [ApkFile] 和 [SecurityGlobal]
     * @param packageName 包名
     * @param depth 深度
     */
    @JvmStatic
    fun findClassesFromPackage(
        loader: ClassLoader,
        trie: ClassTrie,
        packageName: String,
        depth: Int = 0
    ): Classes {
        val key = "$depth-$packageName"
        val cached = classCache[key]
        if (cached != null) {
            return cached
        }
        val classes = Classes(trie.search(packageName, depth).mapNotNull { name ->
            findClassIfExists(name, loader)
        })
        return classes.also { classCache[key] = classes }
    }

    @JvmStatic
    fun findDeclaredClasses(
        clazz: Class<*>,
    ): Classes {
        return Classes(clazz.declaredClasses.toList())
    }


    /**
     * 查找一个确定的构造方法, 如果不存在返回 null
     */
    @JvmStatic
    fun findConstructorIfExists(clazz: Class<*>, vararg parameterTypes: Class<*>): Constructor<*>? =
        try {
            findConstructorExact(clazz, *parameterTypes)
        } catch (_: Throwable) {
            null
        }

    /**
     * 根据 JVM Specification 生成一个参数签名
     */
    @JvmStatic
    private fun getParametersString(vararg clazzes: Class<*>): String =
        "(" + clazzes.joinToString(",") { it.canonicalName ?: "" } + ")"


    @JvmStatic
    fun findConstructorExact(clazz: Class<*>, vararg parameterTypes: Class<*>): Constructor<*> {
        val fullMethodName =
            "${clazz.name}#${getParametersString(*parameterTypes)}#constructor"
        if (fullMethodName in constructorCache) {
            return constructorCache[fullMethodName] ?: throw NoSuchMethodError(fullMethodName)
        }
        try {
            val constructor = clazz.getDeclaredConstructor(*parameterTypes).apply {
                isAccessible = true
            }
            return constructor.also { constructorCache[fullMethodName] = constructor }
        } catch (e: NoSuchMethodException) {
            declaredMethodCache[fullMethodName] = null
            throw NoSuchMethodError(fullMethodName)
        }
    }


    /**
     * 查找一个确定的方法, 如果不存在, 抛出 [NoSuchMethodException] 异常
     *
     * @param clazz 该方法所属的类
     * @param methodName 该方法的名称
     * @param parameterTypes 该方法的参数类型
     */
    @JvmStatic
    fun findDeclaredMethodExact(
        clazz: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method {
        val fullMethodName =
            "${clazz.name}#$methodName${getParametersString(*parameterTypes)}#exact"
        if (fullMethodName in declaredMethodCache) {
            return declaredMethodCache[fullMethodName] ?: throw NoSuchMethodError(fullMethodName)
        }
        try {
            val method = clazz.getDeclaredMethod(methodName, *parameterTypes).apply {
                isAccessible = true
            }
            return method.also { declaredMethodCache[fullMethodName] = method }
        } catch (e: NoSuchMethodException) {
            declaredMethodCache[fullMethodName] = null
            throw NoSuchMethodError(fullMethodName)
        }
    }

    /**
     * 查找一个确定的方法, 如果不存在返回 null
     */
    @JvmStatic
    fun findDeclaredMethodExactIfExists(
        clazz: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method? =
        try {
            findDeclaredMethodExact(clazz, methodName, *parameterTypes)
        } catch (_: Throwable) {
            null
        }

    /**
     * 查找一个确定的方法, 如果不存在, 抛出 [NoSuchMethodException] 异常
     *
     * @param clazz 该方法所属的类
     * @param methodName 该方法的名称
     * @param parameterTypes 该方法的参数类型
     */
    @JvmStatic
    fun findMethodExact(
        clazz: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method {
        val fullMethodName =
            "${clazz.name}#$methodName${getParametersString(*parameterTypes)}#exact"
        if (fullMethodName in methodCache) {
            return methodCache[fullMethodName] ?: throw NoSuchMethodError(fullMethodName)
        }
        try {
            val method = clazz.getMethod(methodName, *parameterTypes).apply {
                isAccessible = true
            }
            return method.also { methodCache[fullMethodName] = method }
        } catch (e: NoSuchMethodException) {
            methodCache[fullMethodName] = null
            throw NoSuchMethodError(fullMethodName)
        }
    }

    /**
     * 查找一个确定的方法, 如果不存在返回 null
     */
    @JvmStatic
    fun findMethodExactIfExists(
        clazz: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method? =
        try {
            findMethodExact(clazz, methodName, *parameterTypes)
        } catch (_: Throwable) {
            null
        }

    /**
     * 查找所有满足要求的方法
     *
     * @param clazz 该方法所属的类
     * @param returnType 该方法的返回类型
     * @param parameterTypes 该方法的参数类型
     */
    @JvmStatic
    fun findDeclaredMethodsByExactParameters(
        clazz: Class<*>,
        returnType: Class<*>?,
        vararg parameterTypes: Class<*>
    ): List<Method> {
        try {
            return clazz.declaredMethods.filter { method ->
                if (returnType != null && returnType != method.returnType) {
                    return@filter false
                }

                val methodParameterTypes = method.parameterTypes
                if (parameterTypes.size != methodParameterTypes.size) {
                    return@filter false
                }
                for (i in parameterTypes.indices) {
                    if (parameterTypes[i] != methodParameterTypes[i]) {
                        return@filter false
                    }
                }

                method.isAccessible = true
                return@filter true
            }
        } catch (e: Throwable) {
            return emptyList<Method>()
        }

    }

    /**
     * 查找一个确定的成员变量, 如果不存在返回 null
     */
    @JvmStatic
    fun findFieldIfExists(clazz: Class<*>, fieldName: String): Field? =
        try {
            clazz.getField(fieldName)
        } catch (_: Throwable) {
            null
        }

    /**
     * 查找指定类中所有特定类型的成员变量
     */
    @JvmStatic
    fun findDeclaredFieldsWithType(clazz: Class<*>, typeName: String): List<Field> {
        return clazz.declaredFields.filter {
            it.type.name == typeName
        }
    }

    @JvmStatic
    fun findDeclaredFieldsWithType(clazz: Class<*>, filedTypeClass: Class<*>): List<Field> {
        return clazz.declaredFields.filter {
            it.type == filedTypeClass
        }
    }

    /**
     * 查找指定类以及父类中所有特定类型的成员变量
     */
    @JvmStatic
    fun findFieldsWithType(clazz: Class<*>, filedTypeClass: Class<*>): List<Field> {
        return clazz.fields.filter {
            it.type == filedTypeClass
        }
    }

    /**
     * 查找指定类中所有特定类型的静态成员变量
     */
    @JvmStatic
    fun findStaticDeclaredFieldsWithType(clazz: Class<*>, typeName: String): List<Field> {
        return clazz.declaredFields.filter {
            Modifier.isStatic(it.modifiers) && it.type.name == typeName
        }
    }

    /**
     * 查找指定类中所有特定泛型的成员变量
     */
    @JvmStatic
    fun findFieldsWithGenericType(clazz: Class<*>, genericTypeName: String): List<Field> {
        return clazz.declaredFields.filter {
            it.genericType.toString() == genericTypeName
        }
    }

    /**
     *
     */
    @JvmStatic
    fun findSupper(clazz: Class<*>, superClass: Class<*>?, depth: Int): Class<*>? {
        var currentDepth = depth
        var foundSuperClass: Class<*>? = clazz.superclass
        var ret = foundSuperClass == superClass
        // 如果没有找到,并且还需要继续向上(寻找, 并且还存在父类)
        while (!ret && currentDepth > 0 && foundSuperClass != null) {
            currentDepth = depth - 1
            foundSuperClass = findSupper(foundSuperClass, superClass, currentDepth)
            ret = foundSuperClass == superClass

        }
        return foundSuperClass
    }

    /**
     * 钩住一个类中所有的方法, 一般只用于测试
     */
    @JvmStatic
    fun hookAllMethodsInClass(clazz: Class<*>, callback: XC_MethodHook) {
        clazz.declaredMethods.forEach { method -> hookMethod(method, callback) }
    }
}
