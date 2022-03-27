package cc.wecando.miuihook.mirror.android.os

import android.os.Handler
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Method

object Methods {

    val Handler_removeCallbacksAndMessages: Method by SecurityGlobal.lazy("Handler_removeCallbacksAndMessages") {
        ReflectionUtil.findDeclaredMethodExact(
            Handler::class.java,
            "removeCallbacksAndMessages",
            Any::class.java
        )
    }
}