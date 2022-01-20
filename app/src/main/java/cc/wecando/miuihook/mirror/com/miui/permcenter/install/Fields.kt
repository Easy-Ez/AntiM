package cc.wecando.miuihook.mirror.com.miui.permcenter.install

import android.os.Handler
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.android.os.Classes.IMessenger
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Classes.AdbInstallActivity
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Field

object Fields {
    val AdbInstallActivity_Handler: Field by SecurityGlobal.wxLazy("AdbInstallActivity_Handler") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                AdbInstallActivity,
                Handler::class.java
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }


    val AdbInstallActivity_IMessenger: Field by SecurityGlobal.wxLazy("AdbInstallActivity_IMessenger") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                AdbInstallActivity,
                IMessenger
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }


}