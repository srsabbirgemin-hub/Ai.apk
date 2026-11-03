package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AetherAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null
    var isAutoScrolling = false
        private set

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        stopAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
        if (instance == this) {
            instance = null
        }
    }

    fun performBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)

    fun performHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)

    fun performRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)

    fun performNotifications(): Boolean = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)

    fun performQuickSettings(): Boolean = performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)

    fun performLock(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    } else false

    fun performScreenshot(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
    } else false

    fun performSplitScreen(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    } else false

    fun performPowerDialog(): Boolean = performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)

    // ------------------------------------------------------------------------
    // Reels / Shorts / TikTok Swipe & Gestures
    // ------------------------------------------------------------------------

    fun swipeNextReel(onComplete: ((Boolean) -> Unit)? = null): Boolean {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()

        val startX = width * 0.5f
        val startY = height * 0.78f
        val endX = width * 0.5f
        val endY = height * 0.22f

        return dispatchSwipeGesture(startX, startY, endX, endY, 240, onComplete)
    }

    fun swipePreviousReel(onComplete: ((Boolean) -> Unit)? = null): Boolean {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()

        val startX = width * 0.5f
        val startY = height * 0.22f
        val endX = width * 0.5f
        val endY = height * 0.78f

        return dispatchSwipeGesture(startX, startY, endX, endY, 240, onComplete)
    }

    fun doubleTapLike(onComplete: ((Boolean) -> Unit)? = null): Boolean {
        val metrics = resources.displayMetrics
        val centerX = metrics.widthPixels * 0.5f
        val centerY = metrics.heightPixels * 0.5f

        val path1 = Path().apply { moveTo(centerX, centerY) }
        val stroke1 = GestureDescription.StrokeDescription(path1, 0, 50)
        val stroke2 = GestureDescription.StrokeDescription(path1, 120, 50)

        val gesture = GestureDescription.Builder()
            .addStroke(stroke1)
            .addStroke(stroke2)
            .build()

        return dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                onComplete?.invoke(true)
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                onComplete?.invoke(false)
            }
        }, null)
    }

    private fun dispatchSwipeGesture(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        durationMs: Long,
        onComplete: ((Boolean) -> Unit)? = null
    ): Boolean {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        return dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                onComplete?.invoke(true)
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                onComplete?.invoke(false)
            }
        }, null)
    }

    fun startAutoScroll(intervalSeconds: Int = 15, onTick: ((Int) -> Unit)? = null) {
        stopAutoScroll()
        isAutoScrolling = true
        val intervalMs = (intervalSeconds.coerceAtLeast(5)) * 1000L

        autoScrollRunnable = object : Runnable {
            override fun run() {
                if (isAutoScrolling) {
                    swipeNextReel()
                    onTick?.invoke(intervalSeconds)
                    handler.postDelayed(this, intervalMs)
                }
            }
        }
        handler.postDelayed(autoScrollRunnable!!, intervalMs)
    }

    fun stopAutoScroll() {
        isAutoScrolling = false
        autoScrollRunnable?.let { handler.removeCallbacks(it) }
        autoScrollRunnable = null
    }

    // ------------------------------------------------------------------------
    // Comment Tray & Comment Typing Interaction
    // ------------------------------------------------------------------------

    fun openCommentsAndType(commentText: String?): Boolean {
        val root = rootInActiveWindow ?: return false
        var commentButtonNode: AccessibilityNodeInfo? = null

        // 1. Search for comment button/icon
        commentButtonNode = findNodeMatching(root) { node ->
            val desc = node.contentDescription?.toString()?.lowercase() ?: ""
            val text = node.text?.toString()?.lowercase() ?: ""
            val viewId = node.viewIdResourceName?.lowercase() ?: ""

            (desc.contains("comment") || desc.contains("মন্তব্য") || desc.contains("কমেন্ট") ||
                    text.contains("comment") || text.contains("মন্তব্য") || text.contains("কমেন্ট") ||
                    viewId.contains("comment")) && node.isClickable
        }

        // If clickable parent needed
        if (commentButtonNode != null) {
            val clicked = commentButtonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (clicked && !commentText.isNullOrBlank()) {
                // Wait briefly for comment sheet/box to open, then type
                handler.postDelayed({
                    typeIntoCommentField(commentText)
                }, 600)
            }
            return clicked
        } else {
            // If already open, try typing directly
            if (!commentText.isNullOrBlank()) {
                return typeIntoCommentField(commentText)
            }
        }

        return false
    }

    private fun typeIntoCommentField(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val editField = findNodeMatching(root) { node ->
            node.isEditable || node.className?.toString()?.contains("EditText", ignoreCase = true) == true
        }

        if (editField != null) {
            editField.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            val arguments = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            }
            return editField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }
        return false
    }

    private fun findNodeMatching(
        node: AccessibilityNodeInfo?,
        predicate: (AccessibilityNodeInfo) -> Boolean
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        if (predicate(node)) return node

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val match = findNodeMatching(child, predicate)
            if (match != null) return match
        }
        return null
    }

    fun extractScreenText(): String {
        val root = rootInActiveWindow ?: return "Unable to read screen: no active window accessible."
        val builder = StringBuilder()
        traverseNode(root, builder)
        return if (builder.isEmpty()) {
            "Screen appears to have no readable text or accessibility data."
        } else {
            builder.toString().take(1500)
        }
    }

    private fun traverseNode(node: AccessibilityNodeInfo?, builder: StringBuilder) {
        if (node == null) return
        val text = node.text?.toString()?.trim()
        val desc = node.contentDescription?.toString()?.trim()

        if (!text.isNullOrEmpty()) {
            builder.append(text).append(" | ")
        } else if (!desc.isNullOrEmpty()) {
            builder.append(desc).append(" | ")
        }

        for (i in 0 until node.childCount) {
            traverseNode(node.getChild(i), builder)
        }
    }

    companion object {
        @Volatile
        var instance: AetherAccessibilityService? = null
            private set

        fun isServiceRunning(): Boolean = instance != null
    }
}
