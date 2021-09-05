package cc.wecando.miuihook.backend

import android.content.Context
import android.content.pm.PackageInfo
import de.robv.android.xposed.callbacks.XC_LoadPackage

interface IFuckTask {
    fun fuck(lpparam: XC_LoadPackage.LoadPackageParam, context: Context, packageInfo: PackageInfo)
}