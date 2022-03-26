package cc.wecando.miuihook

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import cc.wecando.miuihook.base.Version
import cc.wecando.miuihook.parser.ApkFile
import cc.wecando.miuihook.util.FileUtil
import cc.wecando.miuihook.util.MirrorUtil
import cc.wecando.miuihook.util.ReflectionUtil
import cc.wecando.miuihook.mirror.MirrorClasses
import cc.wecando.miuihook.mirror.MirrorFields
import cc.wecando.miuihook.mirror.MirrorMethods
import dalvik.system.PathClassLoader
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.lang.ClassLoader.getSystemClassLoader
import kotlin.system.measureTimeMillis

/**
 * 自动化的微信版本适配测试
 */
@ExperimentalUnsignedTypes
class MirrorUnitTest {
    companion object {
        private const val DOMESTIC_DIR = "apks/domestic"
        private const val PLAY_STORE_DIR = "apks/play-store"
    }

    private var context: Context? = null

    @Before
    fun initialize() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun verifyPackage(apkPath: String) {
        // 解析 APK 版本
        val regex = Regex("v(.*)\\.apk")
        val match = regex.find(apkPath) ?: throw Exception("Unexpected path format")
        val version = match.groupValues[1]
        // 将 APK 文件保存至 Cache 目录
        val cacheDir = context!!.cacheDir
        val apkFile = File(cacheDir, apkPath)
        try {
            javaClass.classLoader!!.getResourceAsStream(apkPath).use {
                FileUtil.writeInputStreamToDisk(apkFile.absolutePath, it)
            }
        } catch (t: Throwable) {
            Log.w("MirrorUnitTest", t)
            return // ignore if the apk isn't accessible
        }

        // 确保 APK 文件存在 并开始自动化适配测试
        assertTrue(apkFile.exists())
        ApkFile(apkFile).use {
            // 测试 APK Parser 的解析速度
            val timeParseDex = measureTimeMillis { it.classTypes }
            Log.d("MirrorUnitTest", "Benchmark: Parsing APK takes $timeParseDex ms.")

            // 初始化 WechatGlobal
            SecurityGlobal.wxUnitTestMode = true
            SecurityGlobal.versivon = Version(version)
            SecurityGlobal.packageName = "com.miui.permcenter"
            SecurityGlobal.loader = PathClassLoader(apkFile.absolutePath, getSystemClassLoader())
            SecurityGlobal.classes = it.classTypes

            // 清理上次测试留下的缓存
            val objects = MirrorClasses + MirrorMethods + MirrorFields
            ReflectionUtil.clearClassCache()
            ReflectionUtil.clearMethodCache()
            objects.forEach { instance ->
                MirrorUtil.clearUnitTestLazyFields(instance)
            }

            // 进行适配测试并生成结果
            var result: List<Pair<String, String>>?
            val timeSearch = measureTimeMillis {
                result = MirrorUtil.generateReportWithForceEval(objects)
            }
            Log.d("MirrorUnitTest", "Benchmark: Searching over classes takes $timeSearch ms.")
            result?.forEach { entry ->
                Log.d("MirrorUnitTest", "Verified: ${entry.first} -> ${entry.second}")
            }
        }

        apkFile.delete()
    }

    @Test
    fun verifyDomesticPackage6_0_0() {
        verifyPackage("$DOMESTIC_DIR/v6.0.0.apk")
    }

    @Test
    fun verifyDomesticPackage5_6_0() {
        verifyPackage("$DOMESTIC_DIR/v5.6.0.apk")
    }


}