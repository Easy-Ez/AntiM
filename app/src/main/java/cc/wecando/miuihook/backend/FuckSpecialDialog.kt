package cc.wecando.miuihook.backend

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Handler
import android.view.View
import android.widget.CheckBox
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Method

/**
 *
 * SpecialPermissionInterceptActivity 会根据 intent 中的 permName 初始化相应的 fragment
 * fragment 中确认按钮默认 enable = false ,只有checkbox 打钩 并且 倒计时结束后会重置 enable 为 true
 * 因此 hook  com.miui.permcenter.privacymanager.h.a 中的 CheckBox 类型的 i 并设置为 true
 * hook com.miui.permcenter.privacymanager.h.a 父类中的 handler 类型的 a, 取消倒计时并且回调方法传递参数0
 * com.miui.permcenter.privacymanager.h.a 的父类是抽象方法, 由子类实现. hook g
 * 完工
 *
 * 特殊权限弹框
 * @author: sadhu
 * @email: c.yao@aftership.com
 * @date: 2021/9/2
 */
object FuckSpecialDialog : IFuckTask {
    override fun fuck(
        lpparam: XC_LoadPackage.LoadPackageParam,
        context: Context,
        packageInfo: PackageInfo
    ) {
        val classloader = context.classLoader
        XposedHelpers.findAndHookMethod(
            "com.miui.permcenter.privacymanager.SpecialPermissionInterceptActivity",
            classloader,
            "r",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    XposedHelpers.findAndHookMethod(
                        "com.miui.permcenter.privacymanager.o.d",
                        classloader,
                        "c",
                        Int::class.java,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.let {
                                    XposedBridge.log("permName:${it.args[0]}")
                                }
                            }
                        }
                    )
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            "com.miui.permcenter.privacymanager.h",
            classloader,
            "a",
            View::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val aObject = param.thisObject
                    findCheckBox(aObject).isChecked = true
                    findHandler(aObject).removeCallbacksAndMessages(null)
                    findCallbackForHandler(aObject).invoke(aObject, 0)
                }
            }
        )

    }

    /**
     * 获取 CheckBox 类型的字段,并返回
     *
     */
    private fun findCheckBox(aObject: Any): CheckBox {
        val aJavaClass = aObject.javaClass
        val checkBoxField = aJavaClass.getDeclaredField("i")
        checkBoxField.isAccessible = true
        return checkBoxField.get(aObject) as CheckBox
    }

    /**
     * 获取 Handler 类型的字段,并返回
     *
     */
    private fun findHandler(aObject: Any): Handler {
        val aJavaClass = aObject.javaClass
        val handlerField = aJavaClass.superclass.getDeclaredField("a")
        handlerField.isAccessible = true
        return handlerField.get(aObject) as Handler
    }

    /**
     * 获取 handler 每次倒计时的方法
     * 签名为 g(int i)
     */
    private fun findCallbackForHandler(aObject: Any): Method {
        val aJavaClass = aObject.javaClass
        return aJavaClass.getDeclaredMethod("g", Int::class.java)
    }
}