package cc.wecando.miuihook.backend

import android.app.Application
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {
    val fuckList = mutableListOf<IFuckTask>(FuckAdbInstallDialog, FuckSpecialDialog)

    companion object {
        const val packageName = "com.miui.securitycenter"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        lpparam?.let {
            if (packageName == it.packageName && it.isFirstApplication) {

                XposedHelpers.findAndHookMethod(
                    Application::class.java,
                    "attach",
                    Context::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val context = param.args[0]
                            if (context is Context) {

                                val packageInfo =
                                    context.packageManager.getPackageInfo(lpparam.packageName, 0)
                                fuckList.forEach { item ->
                                    item.fuck(
                                        it,
                                        context,
                                        packageInfo
                                    )
                                }
                            }
                        }
                    })
            }
        }
    }


}