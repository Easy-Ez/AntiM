package cc.wecando.antim.mirror.com.miui.permcenter.install

import android.os.Bundle
import cc.wecando.antim.SecurityGlobal
import cc.wecando.antim.mirror.com.miui.antivirus.activity.Methods.BaseActivity_init
import cc.wecando.antim.mirror.com.miui.permcenter.install.Classes.AdbInputApplyActivity
import cc.wecando.antim.mirror.com.miui.permcenter.install.Classes.AdbInstallActivity
import cc.wecando.antim.util.ReflectionUtil
import java.lang.reflect.Method

object Methods {


    val AdbInstallActivity_init: Method by SecurityGlobal.lazy("AdbInstallActivity_init") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInstallActivity,
            BaseActivity_init.name,
            *BaseActivity_init.parameterTypes
        )
    }

    val AdbInstallActivity_onDestroy: Method by SecurityGlobal.lazy("AdbInstallActivity_onDestroy") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInstallActivity,
            "onDestroy"
        )
    }
    val AdbInstallActivity_finish: Method by SecurityGlobal.lazy("AdbInstallActivity_finish") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInstallActivity,
            "finish"
        )
    }
    val AdbInputApplyActivity_onCreate: Method by SecurityGlobal.lazy("AdbInputApplyActivity_onCreate") {
        ReflectionUtil.findDeclaredMethodExact(
            AdbInputApplyActivity,
            "onCreate",
            Bundle::class.java
        )
    }


}