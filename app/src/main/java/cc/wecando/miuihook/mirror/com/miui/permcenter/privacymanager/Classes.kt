package cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager

import android.os.Handler
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.util.ReflectionUtil

object Classes {
    private const val pkgName = "com.miui.permcenter.privacymanager"

    val SpecialBaseFrag: Class<*> by SecurityGlobal.lazy("SpecialWithCheckboxFrag") {
        ReflectionUtil.findClassesFromPackage(
            SecurityGlobal.loader!!,
            SecurityGlobal.classes!!,
            pkgName
        )
            .filterIsAbsClass()
            .filterByDeclaredMethod(null, "onDetach")
            .firstOrNull()
    }
    val CountDownHandler: Class<*> by SecurityGlobal.lazy("CountDownHandler") {
        ReflectionUtil.findDeclaredClasses(
            SpecialBaseFrag
        )
            .filterBySuper(Handler::class.java)
            .firstOrNull()
    }

    val SpecialPermissionInterceptActivity: Class<*> by SecurityGlobal.lazy("SpecialPermissionInterceptActivity") {
        ReflectionUtil.findClassIfExists(
            "${pkgName}.SpecialPermissionInterceptActivity",
            SecurityGlobal.loader!!
        )
    }

    val SpecialWithCheckboxFrag: Class<Fragment> by SecurityGlobal.lazy("SpecialWithCheckboxFrag") {
        ReflectionUtil.findClassesFromPackage(
            SecurityGlobal.loader!!,
            SecurityGlobal.classes!!,
            pkgName
        )
            .filterBySuper(SpecialBaseFrag)
            .filterByFieldType(CheckBox::class.java)
            .firstOrNullWithGeneric()
    }

    val SpecialWithoutCheckboxFrag: Class<Fragment> by SecurityGlobal.lazy("SpecialWithoutCheckboxFrag") {
        ReflectionUtil.findClassesFromPackage(
            SecurityGlobal.loader!!,
            SecurityGlobal.classes!!,
            pkgName
        )
            .filterBySuper(SpecialBaseFrag)
            .filterByFieldType(LinearLayout::class.java)
            .firstOrNullWithGeneric()
    }
}