package com.example.envmonitor

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var statusText: TextView
    private val logBuffer = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusText = TextView(this).apply {
            textSize = 14f
            setPadding(32, 32, 32, 32)
        }
        setContentView(statusText)
        appendLog("onCreate: 应用启动")
        lifecycleScope.launch {
            while (true) {
                delay(5000)
                appendLog("心跳检查: 任务仍在运行")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appendLog("onStart: Activity 可见")
    }

    override fun onResume() {
        super.onResume()
        appendLog("onResume: Activity 获得焦点，可交互")
    }

    override fun onPause() {
        super.onPause()
        appendLog("⚠️ onPause: Activity 失去焦点！")
    }

    override fun onStop() {
        super.onStop()
        appendLog("onStop: Activity 完全不可见")
    }

    override fun onDestroy() {
        super.onDestroy()
        appendLog("💀 onDestroy: Activity 被销毁")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            appendLog("🔴 onWindowFocusChanged: 失去窗口焦点（可能被小窗/分屏/其他应用覆盖）")
        } else {
            appendLog("🟢 onWindowFocusChanged: 重新获得窗口焦点")
        }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        appendLog("onMultiWindowModeChanged: 多窗口模式 = $isInMultiWindowMode")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appendLog("onConfigurationChanged: 屏幕尺寸/方向/密度变化 (不会重建Activity)")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        appendLog("onSaveInstanceState: 系统给了保存状态的机会")
    }

    private fun appendLog(msg: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        logBuffer.append("[$time] $msg\n")
        Log.d("EnvMonitor", msg)
        runOnUiThread { statusText.text = logBuffer.toString() }
    }
}
