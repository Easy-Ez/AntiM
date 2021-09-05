package cc.wecando.miuihook.backend

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageInfo
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * com.miui.permcenter.install.AdbInstallActivity
 * adb install apk 时, miui 有烦人的倒计时提醒,啥 jb 玩意.
 * destroy 的时候会判断是否允许, 字段 c 允许后会设置为-1
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
object FuckAdbInstallDialog : IFuckTask {

    const val versionName = "5.5.0-21077.0.2"

    override fun fuck(
        lpparam: XC_LoadPackage.LoadPackageParam,
        context: Context,
        packageInfo: PackageInfo
    ) {
        XposedBridge.log("FuckAdbInstallDialog version: ${packageInfo.versionName}")
        hookAdbInstallCreate(context.classLoader) {
            setAllowFlag(it.thisObject)
            finish(it.thisObject)
        }

    }


    /**
     * hook usb 安装提醒弹出页面
     */
    private fun hookAdbInstallCreate(
        classLoader: ClassLoader,
        callback: (param: XC_MethodHook.MethodHookParam) -> Unit
    ) {
        XposedHelpers.findAndHookMethod(
            "com.miui.permcenter.install.AdbInstallActivity",
            classLoader,
            "a",
            XposedHelpers.findClass("miuix.appcompat.app.i", classLoader),
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    param?.let {
                        callback(it)
                    }
                }
            })
    }

    /**
     * 修改 flag
     */
    private fun setAllowFlag(adbInstallActivity: Any) {
        val declaredField = adbInstallActivity.javaClass.getDeclaredField("c")
        declaredField.isAccessible = true
        declaredField.set(adbInstallActivity, -1)
    }


    /**
     * 修改 flag 后直接退出
     * 原始逻辑会监听dialog 的 dismiss 事件
     */
    private fun finish(adbInstallActivity: Any) {
        val method = adbInstallActivity.javaClass.getDeclaredMethod("finish")
        method.invoke(adbInstallActivity)
    }


}