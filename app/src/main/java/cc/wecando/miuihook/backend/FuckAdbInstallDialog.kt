package cc.wecando.miuihook.backend

import android.os.Handler
import android.util.Log
import android.widget.Button
import cc.wecando.miuihook.base.Hooker
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Classes.AdbInstallActivity
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Fields.AdbInstallActivity_Button
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Fields.AdbInstallActivity_Handler
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Methods
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Methods.AdbInstallActivity_finish
import cc.wecando.miuihook.util.ReflectionUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge


/**
 * com.miui.permcenter.install.AdbInstallActivity
 * adb install apk 时, miui 有烦人的倒计时提醒,啥 jb 玩意.
 * destroy 的时候会判断是否允许, flag 字段允许后会设置为-1
 * protected void onDestroy() {
 *     super.onDestroy();
 *     this.o.removeMessages(10);
 *     try {
 *         if (this.e != null) {
 *             Message message = new Message();
 *             message.what = this.c;
 *             if (this.d != null) {
 *                 Bundle bundle = new Bundle();
 *                 bundle.putString("msg", this.d);
 *                 message.setData(bundle);
 *             }
 *             this.e.send(message);
 *         }
 *     } catch (RemoteException unused) {
 *     }
 * }
 **/
val FuckAdbInstallDialog = Hooker {
    // anti auto reject
    XposedBridge.hookMethod(Methods.AdbInstallActivity_init, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // 停止倒计时
            (AdbInstallActivity_Handler.get(param.thisObject) as Handler).removeMessages(10)
            // 修改 button 文案
            (AdbInstallActivity_Button.get(param.thisObject) as Button).text = "拒绝"
            // auto install
            // 方案一, 模拟点击 "继续安装" 按钮, 页面会闪一下
            //(MIUIDialog_getButton.invoke(param.args.first(), -2) as Button).performClick()
            // 方案二 直接 finish, hook onDestroy ,修改 message what 为 -1 , 用户无感知
            AdbInstallActivity_finish.invoke(param.thisObject)
        }
    })

    // auto install
    XposedBridge.hookMethod(Methods.AdbInstallActivity_onDestroy, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            Log.d("anti-dev", "beforeHookedMethod onDestroy")
            ReflectionUtil
                .findDeclaredFieldsWithType(
                    AdbInstallActivity,
                    Int::class.java
                ).first {
                    it.isAccessible = true
                    it.getInt(param.thisObject) <= 0
                }.setInt(param.thisObject, -1)
        }
    })

}