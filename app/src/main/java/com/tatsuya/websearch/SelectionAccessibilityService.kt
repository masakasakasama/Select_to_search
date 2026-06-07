package com.tatsuya.websearch

import android.accessibilityservice.AccessibilityService
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button

class SelectionAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private var windowManager: WindowManager? = null
    private var overlayButton: Button? = null
    private var selectedText: String? = null

    private val hideOverlayRunnable = Runnable { hideOverlay() }

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            return
        }

        val selection = extractSelectedText(event)
        if (selection.isNullOrBlank()) {
            hideOverlay()
            return
        }

        showOverlay(selection)
    }

    override fun onInterrupt() {
        hideOverlay()
    }

    override fun onDestroy() {
        hideOverlay()
        super.onDestroy()
    }

    private fun extractSelectedText(event: AccessibilityEvent): String? {
        val source = event.source
        val sourceText = source?.text?.toString()
        val sourceStart = source?.textSelectionStart ?: -1
        val sourceEnd = source?.textSelectionEnd ?: -1

        val fromSource = substringOrNull(sourceText, sourceStart, sourceEnd)
        if (!fromSource.isNullOrBlank()) {
            return fromSource.trim()
        }

        val eventText = event.text.firstOrNull()?.toString()
        val fromEvent = substringOrNull(eventText, event.fromIndex, event.toIndex)
        if (!fromEvent.isNullOrBlank()) {
            return fromEvent.trim()
        }

        return null
    }

    private fun substringOrNull(text: String?, start: Int, end: Int): String? {
        if (text.isNullOrEmpty() || start < 0 || end < 0 || start == end) {
            return null
        }

        val safeStart = minOf(start, end)
        val safeEnd = maxOf(start, end)
        if (safeStart > text.length || safeEnd > text.length) {
            return null
        }

        return text.substring(safeStart, safeEnd)
    }

    private fun showOverlay(selection: String) {
        selectedText = selection

        handler.removeCallbacks(hideOverlayRunnable)
        val existingButton = overlayButton
        if (existingButton != null) {
            handler.postDelayed(hideOverlayRunnable, OVERLAY_TIMEOUT_MS)
            return
        }

        val button = Button(this).apply {
            text = getString(R.string.process_text_label)
            setOnClickListener {
                selectedText?.let { query ->
                    if (query.isNotBlank()) {
                        openGoogleSearch(query)
                    }
                }
                hideOverlay()
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 24
            y = 180
        }

        try {
            windowManager?.addView(button, params)
            overlayButton = button
            handler.postDelayed(hideOverlayRunnable, OVERLAY_TIMEOUT_MS)
        } catch (_: RuntimeException) {
            overlayButton = null
        }
    }

    private fun hideOverlay() {
        handler.removeCallbacks(hideOverlayRunnable)
        val button = overlayButton ?: return
        try {
            windowManager?.removeView(button)
        } catch (_: RuntimeException) {
            // Overlay was already removed by the system.
        } finally {
            overlayButton = null
            selectedText = null
        }
    }

    private fun openGoogleSearch(query: String) {
        val searchUri = Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
        val browserIntent = Intent(Intent.ACTION_VIEW, searchUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(browserIntent)
        } catch (_: ActivityNotFoundException) {
            // No browser is available. Exit silently.
        }
    }

    private companion object {
        const val OVERLAY_TIMEOUT_MS = 10_000L
    }
}
