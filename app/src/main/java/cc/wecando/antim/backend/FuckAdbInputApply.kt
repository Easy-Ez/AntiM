package cc.wecando.antim.backend

import android.os.Handler
import android.util.Log
import android.widget.Button
import cc.wecando.antim.base.Hooker
import cc.wecando.antim.mirror.com.miui.permcenter.install.Classes.AdbInputApplyActivity
import cc.wecando.antim.mirror.com.miui.permcenter.install.Fields.AdbInputApplyActivity_Handler
import cc.wecando.antim.mirror.com.miui.permcenter.install.Methods.AdbInputApplyActivity_onCreate
import cc.wecando.antim.util.ReflectionUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

/**
 * 1. 在 onCreate 之前将 step 改为 3
 * 2. 在 onCreate 之后将 button 设置为 enable
 * 3. 在 onCreate 之后 removeMessage 100
 */
val FuckAdbInputApply = Hooker {
    XposedBridge.hookMethod(AdbInputApplyActivity_onCreate, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            Log.d("anti-dev", "beforeHookedMethod")
            // 在 onCreate 之前将 step 改为 3
            val stepFiled = ReflectionUtil
                .findDeclaredFieldsWithType(
                    AdbInputApplyActivity,
                    Int::class.java
                ).first {
                    it.isAccessible = true
                    it.getInt(param.thisObject) <= 3
                }
            stepFiled.setInt(param.thisObject, 3)
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            // 在 onCreate 之后将 button 设置为 enable
            val acceptBtn = ReflectionUtil
                .findDeclaredFieldsWithType(
                    AdbInputApplyActivity,
                    Button::class.java
                ).first {
                    it.isAccessible = true
                    !(it.get(param.thisObject) as Button).isEnabled
                }.get(param.thisObject) as Button?
            acceptBtn?.let {
                it.isEnabled = true
                it.text = "确定"
            }
            // onCreate 之后 removeMessage 100
            (AdbInputApplyActivity_Handler.get(param.thisObject) as Handler).removeMessages(100)
        }
    })
}
