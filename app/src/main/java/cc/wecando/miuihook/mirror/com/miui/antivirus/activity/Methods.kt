package cc.wecando.miuihook.mirror.com.miui.antivirus.activity

import android.os.Bundle
import android.widget.Button
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.com.miui.antivirus.activity.Classes.BaseActivity
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object Methods {

    val BaseActivity_init: Method by SecurityGlobal.wxLazy("BaseActivity_init") {
        BaseActivity.declaredMethods.firstOrNull {
            it.parameterCount == 1 && it.parameterTypes.first() != Bundle::class.java && Modifier.isProtected(
                it.modifiers
            ) && !Modifier.isAbstract(it.modifiers)
        }
    }

    val MIUIDialog_getButton: Method by SecurityGlobal.wxLazy("MIUIDialog_getButton") {
        ReflectionUtil.findDeclaredMethodsByExactParameters(
            BaseActivity_init.parameterTypes.first(),
            Button::class.java,
            Int::class.java
        ).first()
    }

}