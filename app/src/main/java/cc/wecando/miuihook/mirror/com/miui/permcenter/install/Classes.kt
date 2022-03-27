package cc.wecando.miuihook.mirror.com.miui.permcenter.install

import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.ReflectionUtil

object Classes {


    val AdbInstallActivity: Class<*> by SecurityGlobal.lazy("AdbInstallActivity") {
        ReflectionUtil.findClassIfExists(
            "com.miui.permcenter.install.AdbInstallActivity",
            SecurityGlobal.loader!!,
        )
    }
    val AdbInputApplyActivity: Class<*> by SecurityGlobal.lazy("AdbInputApplyActivity") {
        ReflectionUtil.findClassIfExists(
            "com.miui.permcenter.install.AdbInputApplyActivity",
            SecurityGlobal.loader!!,
        )
    }


}