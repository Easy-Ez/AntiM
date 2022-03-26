package cc.wecando.miuihook.mirror.com.miui.permcenter.install

import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.ReflectionUtil

object Classes {


    val AdbInstallActivity: Class<*> by SecurityGlobal.wxLazy("AdbInstallActivity") {
        ReflectionUtil.findClassIfExists(
            "com.miui.permcenter.install.AdbInstallActivity",
            SecurityGlobal.loader!!,
        )
    }


}