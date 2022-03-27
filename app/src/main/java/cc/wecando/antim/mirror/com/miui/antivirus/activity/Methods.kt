package cc.wecando.antim.mirror.com.miui.antivirus.activity

import android.os.Bundle
import android.widget.Button
import cc.wecando.antim.SecurityGlobal
import cc.wecando.antim.mirror.com.miui.antivirus.activity.Classes.BaseActivity
import cc.wecando.antim.util.ReflectionUtil
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object Methods {

    val BaseActivity_init: Method by SecurityGlobal.lazy("BaseActivity_init") {
        BaseActivity.declaredMethods.firstOrNull {
            it.parameterCount == 1 && it.parameterTypes.first() != Bundle::class.java && Modifier.isProtected(
                it.modifiers
            ) && !Modifier.isAbstract(it.modifiers)
        }
    }

    val MIUIDialog_getButton: Method by SecurityGlobal.lazy("MIUIDialog_getButton") {
        ReflectionUtil.findDeclaredMethodsByExactParameters(
            BaseActivity_init.parameterTypes.first(),
            Button::class.java,
            Int::class.java
        ).first()
    }

}