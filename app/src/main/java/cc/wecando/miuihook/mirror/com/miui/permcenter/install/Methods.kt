package cc.wecando.miuihook.mirror.com.miui.permcenter.install

import android.os.Handler
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Classes.AdbInstallActivity
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Method

object Methods {

    val AdbInstallActivity_countdownView: Method by SecurityGlobal.wxLazy("AdbInstallActivity_countdownView") {
        ReflectionUtil.findMethodsByExactParameters(
            AdbInstallActivity,
            Int::class.java,
            AdbInstallActivity
        ).firstOrNull()
    }
    val AdbInstallActivity_onDestroy: Method by SecurityGlobal.wxLazy("AdbInstallActivity_onDestroy") {
        ReflectionUtil.findMethodExact(
            AdbInstallActivity,
            "onDestroy"
        )
    }
}