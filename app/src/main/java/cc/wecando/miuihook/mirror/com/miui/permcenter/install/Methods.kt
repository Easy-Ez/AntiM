package cc.wecando.miuihook.mirror.com.miui.permcenter.install

import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.com.miui.antivirus.activity.Methods.BaseActivity_init
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Classes.AdbInstallActivity
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Method

object Methods {


    val AdbInstallActivity_init: Method by SecurityGlobal.wxLazy("AdbInstallActivity_init") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInstallActivity,
            BaseActivity_init.name,
            *BaseActivity_init.parameterTypes
        )
    }

    val AdbInstallActivity_onDestroy: Method by SecurityGlobal.wxLazy("AdbInstallActivity_onDestroy") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInstallActivity,
            "onDestroy"
        )
    }
    val AdbInstallActivity_finish: Method by SecurityGlobal.wxLazy("AdbInstallActivity_finish") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInstallActivity,
            "finish"
        )
    }

}