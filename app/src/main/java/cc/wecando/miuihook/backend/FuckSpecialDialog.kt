package cc.wecando.miuihook.backend

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import cc.wecando.miuihook.base.Hooker
import cc.wecando.miuihook.mirror.android.os.Methods.Handler_removeCallbacksAndMessages
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Fields.SpecialBaseFrag_CountDownHandler
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Fields.SpecialWithCheckboxFrag_CheckBox
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Methods.SpecialWithCheckboxFrag__setCountDownValue
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Methods.SpecialWithCheckboxFrag_getArguments
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Methods.SpecialWithCheckboxFrag_initView
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Methods.SpecialWithoutCheckboxFrag_getArguments
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Methods.SpecialWithoutCheckboxFrag_initView
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Methods.SpecialWithoutCheckboxFrag_setCountDownValue
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

/**
 * 特殊权限弹框
 * SpecialPermissionInterceptActivity 会根据 intent 中的 permName 初始化相应的 fragment
 * fragment 有两类, 两类都有倒计时, 不过其中一类要需要勾选 checkbox.
 * fragment 中的确认 Button 默认 enable = false ,只有倒计时结束后会重置(并且 checkbox 打钩) enable 为 true
 * 因此 hook  中的 CheckBox 类型的 i 并设置为 true
 * hook fragment 父类中的 handler 类型, 取消倒计时并且回调方法传递参数0
 * hook checkbox 手动设置为 checked
 * 完工
 * 👇🏻👇🏻👇🏻👇🏻👇🏻👇🏻👇🏻👇🏻 需要勾选 checkbox
 * "perm_notification" -> 设备和应用通知
 * "perm_install_unknown" -> 安装未知应用
 * "perm_app_statistics" -> 使用情况访问权限
 * "perm_device_manager" -> 设备管理应用
 * "miui_open_debug" -> 开启调试模式
 * "miui_barrier_free" -> 无障碍
 * 👆🏻👆🏻👆🏻👆🏻👆🏻👆🏻👆🏻👆🏻 需要勾选 checkbox
 * "miui_close_optimization" -> MIUI 优化
 * "oaid_close -> 虚拟 ID
 * @author: sadhu
 * @email: c.yao@aftership.com
 * @date: 2021/9/2
 */
val FuckSpecialDialog = Hooker {
    XposedBridge.hookMethod(SpecialWithCheckboxFrag_initView, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            Log.d(
                "anti-dev",
                "permName:${
                    (SpecialWithCheckboxFrag_getArguments.invoke(param.thisObject) as Bundle?)?.getString(
                        "permName"
                    )
                }"
            )
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            super.afterHookedMethod(param)
            // ignore perm_device_manager type
            if ("perm_device_manager" !== (SpecialWithCheckboxFrag_getArguments.invoke(param.thisObject) as Bundle?)?.getString(
                    "permName"
                )
            ) {
                // remove countdown
                Handler_removeCallbacksAndMessages.invoke(
                    SpecialBaseFrag_CountDownHandler.get(param.thisObject),
                    null
                )
                // set checked
                (SpecialWithCheckboxFrag_CheckBox.get(param.thisObject) as CheckBox).isChecked =
                    true
                // invoke setCountDownValue with 0
                SpecialWithCheckboxFrag__setCountDownValue.invoke(param.thisObject, 0)
            }
        }
    })
    XposedBridge.hookMethod(SpecialWithoutCheckboxFrag_initView, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            Log.d(
                "anti-dev",
                "permName:${
                    (SpecialWithoutCheckboxFrag_getArguments.invoke(param.thisObject) as Bundle?)?.getString(
                        "permName"
                    )
                }"
            )
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            super.afterHookedMethod(param)
            Handler_removeCallbacksAndMessages.invoke(
                SpecialBaseFrag_CountDownHandler.get(param.thisObject),
                null
            )
            // invoke setCountDownValue with 0
            SpecialWithoutCheckboxFrag_setCountDownValue.invoke(param.thisObject, 0)

        }
    })
}