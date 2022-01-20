package cc.wecando.miuihook.util

import android.util.Log
import kotlin.concurrent.thread

/**
 * 封装了一批很便利的常用操作
 */
object BasicUtil {
    /**
     * 执行回调函数, 无视它抛出的任何异常
     */
    @JvmStatic
    inline fun <T : Any> trySilently(func: () -> T?): T? {
        return try {
            func()
        } catch (t: Throwable) {
            null
        }
    }

    /**
     * 执行回调函数, 将它抛出的异常记录到 Xposed 的日志里
     */
    @JvmStatic
    inline fun <T : Any> tryVerbosely(func: () -> T?): T? {
        return try {
            func()
        } catch (t: Throwable) {
            Log.e("Xposed", Log.getStackTraceString(t)); null
        }
    }

    /**
     * 异步执行回调函数, 将它抛出的记录到 Xposed 的日志里
     *
     * WARN: 别忘了任何 UI 操作都必须使用 runOnUiThread
     */
    @JvmStatic
    inline fun tryAsynchronously(crossinline func: () -> Unit): Thread {
        return thread(start = true) { func() }.apply {
            setUncaughtExceptionHandler { _, t ->
                Log.e("Xposed", Log.getStackTraceString(t))
            }
        }
    }

    /**
     * 手动抛一个异常, 打印当前的调用栈
     */
    @JvmStatic
    fun printStackTrace(): String {
        return try {
            throw Throwable()
        } catch (t: Throwable) {
            Log.getStackTraceString(t)
        }

    }
}