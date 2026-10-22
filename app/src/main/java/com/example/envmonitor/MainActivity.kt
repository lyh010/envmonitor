package com.example.envmonitor

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var statusText: TextView
    private lateinit var scrollView: ScrollView
    private val logBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var heartbeatCount = 0
    private var isTaskRunning = false

    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            heartbeatCount++
            appendLog("💓 任务执行中... 计数: $heartbeatCount")
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用 ScrollView 包裹 TextView，解决日志出屏无法滑动的问题
        scrollView = ScrollView(this)
        statusText = TextView(this).apply {
            textSize = 16f
            setPadding(40, 40, 40, 40)
        }
        scrollView.addView(statusText)
        setContentView(scrollView)
        
        appendLog("🟢 App 启动。")
        startTask()
    }

    override fun onPause() {
        super.onPause()
        appendLog("⚠️ 【警告】失去焦点（可能切了后台或弹出了其他窗口），任务强制中断！")
        stopTask()
    }

    override fun onResume() {
        super.onResume()
        appendLog("✅ 重新回到前台。")
        startTask()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            appendLog("🔴 【焦点丢失】当前窗口被覆盖！请回到应用继续。")
            stopTask()
        } else {
            appendLog("🟢 【焦点恢复】重新获得控制权。")
            startTask() // 修复：焦点回来时，也尝试恢复任务
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
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
        
        // 只保留最新的 50 条
        val lines = logBuffer.lines()
        if (lines.size > 50) {
            logBuffer.clear()
            logBuffer.append(lines.drop(lines.size - 50).joinToString("\n"))
        }
        
        runOnUiThread { 
            statusText.text = logBuffer.toString()
            // 自动滚动到最底部
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }
}
