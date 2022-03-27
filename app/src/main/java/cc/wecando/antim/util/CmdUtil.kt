package cc.wecando.antim.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object CmdUtil {
    suspend fun runRootCommand(
        command: String,
    ): Pair<Int, List<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
                val br = BufferedReader(InputStreamReader(process.inputStream))
                val retList = br.lineSequence().toList()
                val code = process.waitFor()
                process.inputStream.close()
                process.errorStream.close()
                process.inputStream.close()
                Pair(code, retList)
            } catch (e: Exception) {
                Log.e("anti-dev", "runRootCommand error", e)
                Pair(-1, emptyList())
            }

        }

    }

}