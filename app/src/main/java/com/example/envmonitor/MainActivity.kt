package com.example.envmonitor

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var statusText: TextView
    private val logBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var heartbeatCount = 0
    private var isTaskRunning = false

    // 模拟一个非常脆弱的业务任务（比如上传、计时）
    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            heartbeatCount++
            appendLog("💓 任务执行中... 计数: $heartbeatCount")
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusText = TextView(this).apply {
            textSize = 16f
            setPadding(40, 40, 40, 40)
        }
        setContentView(statusText)
        appendLog("🟢 App 启动。")
        startTask()
    }

    override fun onPause() {
        super.onPause()
        // 模拟应用一失焦就报错的脆弱逻辑
        appendLog("⚠️ 【警告】失去焦点（可能切了后台或弹出了其他窗口），任务强制中断！")
        stopTask()
    }

    override fun onResume() {
        super.onResume()
        appendLog("✅ 重新回到前台。")
        if (!isTaskRunning) {
            appendLog("🔄 重新启动任务...")
            startTask()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            // 这里就是你说的：哪怕全屏，B小窗一出来，焦点丢了，立刻提示！
            appendLog("🔴 【焦点丢失】当前窗口被覆盖！请回到应用继续。")
            stopTask()
        } else {
            appendLog("🟢 【焦点恢复】重新获得控制权。")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 这里拦截屏幕变化（分屏、键盘弹出等），不崩溃，只弹提示
        appendLog("📐 【屏幕变化】检测到窗口尺寸变化（如分屏/键盘弹出），布局可能已错乱！")
    }

    private fun startTask() {
        if (!isTaskRunning) {
            isTaskRunning = true
            handler.post(heartbeatRunnable)
        }
    }

    private fun stopTask() {
        if (isTaskRunning) {
            isTaskRunning = false
            handler.removeCallbacks(heartbeatRunnable)
            appendLog("🛑 任务已停止。")
        }
    }

    private fun appendLog(msg: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        logBuffer.append("[$time] $msg\n")
        // 为了防止日志太长，只保留最新的 50 条
        val lines = logBuffer.lines()
        if (lines.size > 50) {
            logBuffer.clear()
            logBuffer.append(lines.drop(lines.size - 50).joinToString("\n"))
        }
        runOnUiThread { statusText.text = logBuffer.toString() }
    }
}
