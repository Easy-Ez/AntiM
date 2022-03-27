package cc.wecando.miuihook

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cc.wecando.miuihook.util.CmdUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var tvOutput: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvOutput = findViewById(R.id.tv_output)
        findViewById<Button>(R.id.btn_kill).setOnClickListener {
            killSecurityCenter()
        }
    }

    private fun killSecurityCenter() {
        lifecycleScope.launchWhenCreated {
            val command = "pidof -s com.miui.securitycenter"
            withContext(Dispatchers.Main) {
                tvOutput.append("获取 手机管家 进程ing")
                tvOutput.append("\n")
            }
            val (pidofCode, retList) = CmdUtil.runRootCommand(command)
            withContext(Dispatchers.Main) {
                tvOutput.append("获取${if (pidofCode == 0) "成功,pid:${retList.first()}" else "失败"}")
                tvOutput.append("\n")

            }
            if (pidofCode != 0) {
                if (pidofCode == 13) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "请开启 root 权限", Toast.LENGTH_LONG).show()
                    }
                }
                return@launchWhenCreated
            }
            val (killCode) = CmdUtil.runRootCommand("kill -9 ${retList.first()}")
            withContext(Dispatchers.Main) {
                tvOutput.append("关闭进程${if (killCode == 0) "成功" else "失败"}")
                tvOutput.append("\n")
            }
            if (killCode != 0) {
                return@launchWhenCreated
            }
            val restartRet =
                CmdUtil.runRootCommand("am start -n com.miui.securitycenter/com.miui.securityscan.MainActivity")
            withContext(Dispatchers.Main) {
                tvOutput.append("重启进程${if (restartRet.first == 0) "成功" else "失败"}")
                tvOutput.append("\n")
            }
        }
    }

}