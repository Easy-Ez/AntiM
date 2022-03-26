package cc.wecando.miuihook.mirror.com.miui.antivirus.activity

import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.ReflectionUtil

object Classes {
    private val DangerousAlertActivity: Class<*> by SecurityGlobal.wxLazy("DangerousAlertActivity") {
        ReflectionUtil.findClassIfExists(
            "com.miui.antivirus.activity.DangerousAlertActivity",
            SecurityGlobal.loader!!,
        )
    }

    val BaseActivity: Class<*> by SecurityGlobal.wxLazy("BaseActivity") {
        DangerousAlertActivity.superclass
    }


}