package cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager

import android.os.Bundle
import android.os.Handler
import android.view.View
import cc.wecando.miuihook.SecurityGlobal
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Classes.SpecialBaseFrag
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Classes.SpecialWithCheckboxFrag
import cc.wecando.miuihook.mirror.com.miui.permcenter.privacymanager.Classes.SpecialWithoutCheckboxFrag
import cc.wecando.miuihook.util.ReflectionUtil
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object Methods {

    private val SpecialBaseFrag_initView: Method by SecurityGlobal.lazy("SpecialBaseFrag_initView") {
        ReflectionUtil.findDeclaredMethodsByExactParameters(
            SpecialBaseFrag,
            null,
            View::class.java
        ).first { Modifier.isAbstract(it.modifiers) }
    }

    /**
     * 基类 handler 每次接受到 msg 后调用该方法, 设置当前倒计时值
     */
    private val SpecialBaseFrag_setCountDownValue: Method by SecurityGlobal.lazy("SpecialBaseFrag_setCountDownValue") {
        ReflectionUtil.findDeclaredMethodsByExactParameters(
            SpecialBaseFrag,
            null,
            Int::class.java
        ).first { Modifier.isAbstract(it.modifiers) }
    }


    val SpecialWithCheckboxFrag_initView: Method by SecurityGlobal.lazy("SpecialBaseFrag_initView") {
        ReflectionUtil.findDeclaredMethodExactIfExists(
            SpecialWithCheckboxFrag,
            SpecialBaseFrag_initView.name,
            *SpecialBaseFrag_initView.parameterTypes
        )
    }

    val SpecialWithCheckboxFrag_getArguments: Method by SecurityGlobal.lazy("SpecialWithCheckboxFrag_getArguments") {
        ReflectionUtil.findMethodExactIfExists(
            SpecialWithCheckboxFrag,
            "getArguments",
        )
    }

    val SpecialWithCheckboxFrag_removeCallbacksAndMessages: Method by SecurityGlobal.lazy("SpecialWithCheckboxFrag_removeCallbacksAndMessages") {
        ReflectionUtil.findMethodExactIfExists(
            SpecialWithCheckboxFrag,
            "removeCallbacksAndMessages",
            Any::class.java
        )
    }
    val SpecialWithCheckboxFrag__setCountDownValue: Method by SecurityGlobal.lazy("SpecialWithCheckboxFrag__setCountDownValue") {
        ReflectionUtil.findDeclaredMethodExactIfExists(
            SpecialWithCheckboxFrag,
            SpecialBaseFrag_setCountDownValue.name,
            *SpecialBaseFrag_setCountDownValue.parameterTypes
        )
    }
    val SpecialWithoutCheckboxFrag_initView: Method by SecurityGlobal.lazy("SpecialBaseFrag_initView") {
        ReflectionUtil.findDeclaredMethodExactIfExists(
            SpecialWithoutCheckboxFrag,
            SpecialBaseFrag_initView.name,
            *SpecialBaseFrag_initView.parameterTypes
        )
    }
    val SpecialWithoutCheckboxFrag_getArguments: Method by SecurityGlobal.lazy("SpecialWithoutCheckboxFrag_getArguments") {
        ReflectionUtil.findMethodExactIfExists(
            SpecialWithCheckboxFrag,
            "getArguments",
        )
    }
    val SpecialWithoutCheckboxFrag_removeCallbacksAndMessages: Method by SecurityGlobal.lazy("SpecialWithoutCheckboxFrag_removeCallbacksAndMessages") {
        ReflectionUtil.findMethodExactIfExists(
            SpecialWithCheckboxFrag,
            "removeCallbacksAndMessages",
            Any::class.java
        )
    }


    val SpecialWithoutCheckboxFrag_setCountDownValue: Method by SecurityGlobal.lazy("SpecialWithoutCheckboxFrag_setCountDownValue") {
        ReflectionUtil.findDeclaredMethodExactIfExists(
            SpecialWithoutCheckboxFrag,
            SpecialBaseFrag_setCountDownValue.name,
            *SpecialBaseFrag_setCountDownValue.parameterTypes
        )
    }


}