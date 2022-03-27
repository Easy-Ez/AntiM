package cc.wecando.antim.mirror.com.miui.permcenter.privacymanager

import android.widget.CheckBox
import cc.wecando.antim.SecurityGlobal
import cc.wecando.antim.mirror.com.miui.permcenter.privacymanager.Classes.CountDownHandler
import cc.wecando.antim.mirror.com.miui.permcenter.privacymanager.Classes.SpecialBaseFrag
import cc.wecando.antim.mirror.com.miui.permcenter.privacymanager.Classes.SpecialWithCheckboxFrag
import cc.wecando.antim.util.ReflectionUtil
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