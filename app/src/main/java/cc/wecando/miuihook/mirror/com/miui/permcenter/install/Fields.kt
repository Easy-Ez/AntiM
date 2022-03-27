package cc.wecando.miuihook.mirror.com.miui.permcenter.install

import android.os.Handler
import android.widget.Button
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Classes.AdbInputApplyActivity
import cc.wecando.miuihook.mirror.com.miui.permcenter.install.Classes.AdbInstallActivity
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Field

object Fields {
    val AdbInstallActivity_Handler: Field by SecurityGlobal.lazy("AdbInstallActivity_Handler") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                AdbInstallActivity,
                Handler::class.java
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }

    val AdbInputApplyActivity_Handler: Field by SecurityGlobal.lazy("AdbInputApplyActivity_Handler") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                AdbInputApplyActivity,
                Handler::class.java
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }

    val AdbInstallActivity_Button: Field by SecurityGlobal.lazy("AdbInstallActivity_Button") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                AdbInstallActivity,
                Button::class.java
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }

}