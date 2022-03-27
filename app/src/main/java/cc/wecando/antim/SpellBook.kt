package cc.wecando.antim

import android.content.Context
import cc.wecando.antim.base.Version
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object SpellBook {
    /**
     * 利用 Reflection 获取当前的系统 Context
     */
    private fun getSystemContext(): Context {
        val activityThreadClass = XposedHelpers.findClass("android.app.ActivityThread", null)
        val activityThread =
            XposedHelpers.callStaticMethod(activityThreadClass, "currentActivityThread")
        val context = XposedHelpers.callMethod(activityThread, "getSystemContext") as Context?
        return context ?: throw Error("Failed to get system context.")
    }

    /**
     * 获取指定应用的 APK 路径
     */
    fun getApplicationApkPath(packageName: String): String {
        val pm = getSystemContext().packageManager
        val apkPath = pm.getApplicationInfo(packageName, 0).publicSourceDir
        return apkPath ?: throw Error("Failed to get the APK path of $packageName")
    }

    /**
     * 获取指定应用的版本号
     */
    fun getApplicationVersion(packageName: String): Version {
        val pm = getSystemContext().packageManager
        val versionName = pm.getPackageInfo(packageName, 0)?.versionName
        return Version(
            versionName
                ?: throw Error("Failed to get the version of $packageName")
        )
    }

    fun isImportantProcess(lpparam: XC_LoadPackage.LoadPackageParam): Boolean {
        return ("com.miui.securitycenter" == lpparam.packageName && lpparam.isFirstApplication)
    }
}