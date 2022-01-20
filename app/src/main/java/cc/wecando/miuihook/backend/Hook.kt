package cc.wecando.miuihook.backend

import android.app.Application
import android.content.Context
import android.util.Log
import cc.wecando.miuihook.Plugins
import cc.wecando.miuihook.SpellBook.isImportantProcess
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.XposedUtil
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {


    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (isImportantProcess(lpparam)) {
            hookAttachBaseContext(lpparam.classLoader) {
                handleLoadWechat(lpparam, it)
            }
        }
    }

    private inline fun hookAttachBaseContext(
        loader: ClassLoader,
        crossinline callback: (Context) -> Unit
    ) {
        XposedHelpers.findAndHookMethod(
            "android.content.ContextWrapper", loader, "attachBaseContext",
            Context::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    callback(param.thisObject as? Application ?: return)
                }
            })
    }

    private fun handleLoadWechat(lpparam: XC_LoadPackage.LoadPackageParam, context: Context) {
        Log.d("Xposed", "Security SpellBook: ${Plugins.size} plugins.")
        SecurityGlobal.init(lpparam)
        Plugins.forEach {
            if (!it.hasHooked) {
                XposedUtil.postHooker(it)
            }
        }
    }
}




