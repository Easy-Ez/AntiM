package cc.wecando.miuihook.mirror.android.os

import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.ReflectionUtil

object Classes {

    val IMessenger: Class<*> by SecurityGlobal.wxLazy("IMessenger") {
        ReflectionUtil.findClassIfExists("android.os.IMessenger", SecurityGlobal.wxLoader!!)
    }
}