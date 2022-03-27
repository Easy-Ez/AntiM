package cc.wecando.antim.mirror.android.os

import android.os.Handler
import cc.wecando.antim.SecurityGlobal
import cc.wecando.antim.util.ReflectionUtil
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