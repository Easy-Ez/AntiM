package cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager

import android.widget.CheckBox
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Classes.CountDownHandler
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Classes.SpecialBaseFrag
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Classes.SpecialWithCheckboxFrag
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Field

object Fields {

    val SpecialBaseFrag_CountDownHandler: Field by SecurityGlobal.lazy("SpecialBaseFrag_CountDownHandler") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                SpecialBaseFrag,
                CountDownHandler
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }

    val SpecialWithCheckboxFrag_CheckBox: Field by SecurityGlobal.lazy("SpecialWithCheckboxFrag_CheckBox") {
        ReflectionUtil
            .findDeclaredFieldsWithType(
                SpecialWithCheckboxFrag,
                CheckBox::class.java
            )
            .firstOrNull()
            ?.apply { isAccessible = true }
    }
}