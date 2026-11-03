package com.example.agent

import android.app.ActivityManager
import android.app.DownloadManager
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.service.AetherAccessibilityService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

sealed class ExecutionResult {
    data class Success(val message: String) : ExecutionResult()
    data class Failure(val reason: String) : ExecutionResult()
    data class ScreenText(val text: String) : ExecutionResult()
}

class ActionExecutor(private val context: Context) {

    private var isTorchOn = false

    fun execute(
        actionType: String,
        target: String?,
        extra: String?
    ): ExecutionResult {
        return try {
            when (actionType.lowercase().trim()) {
                // Apps & Communications
                "open_app" -> openApp(target ?: extra ?: "")
                "call" -> callContact(target ?: extra ?: "")
                "sms" -> sendSms(target ?: "", extra ?: "")
                "whatsapp" -> sendWhatsApp(target, extra ?: "")
                "alarm" -> setAlarm(target ?: "", extra ?: "Aether Alarm")
                "timer" -> setTimer(target ?: "300", extra ?: "Aether Timer")
                "note" -> saveNote(target ?: "Note", extra ?: "")
                "search" -> searchWeb(target ?: extra ?: "")
                "navigate" -> navigateTo(target ?: extra ?: "")
                "youtube" -> playYouTube(target ?: extra ?: "")

                // Social Video & Reels Controls (YouTube, FB Reels, TikTok)
                "developer_facebook", "facebook_id", "creator_profile" -> openFacebookProfile(target ?: extra ?: "https://www.facebook.com/Siddik9980")
                "facebook_page", "fb_page" -> openFacebookPage(target ?: extra ?: "https://www.facebook.com/profile.php?id=61557614412925")
                "telegram_channel", "telegram", "tg_channel" -> openTelegramChannel(target ?: extra ?: "https://t.me/SRSabbir")
                "youtube_channel", "yt_channel" -> openYouTubeChannel(target ?: extra ?: "https://www.youtube.com/@SRSabbir9980")
                "youtube_shorts", "shorts" -> openYouTubeShorts()
                "facebook_reels", "fb_reels" -> openFacebookReels()
                "tiktok" -> openTikTok()
                "next_reel", "next_video", "scroll_down" -> nextReel()
                "prev_reel", "prev_video", "scroll_up" -> previousReel()
                "like_reel", "like_video" -> likeReel()
                "comment_reel", "comment_video", "comment" -> commentOnReel(target ?: extra ?: "")
                "auto_scroll" -> startAutoScroll(target?.toIntOrNull() ?: 15)
                "stop_auto_scroll" -> stopAutoScroll()

                // Hardware & Device Controls
                "flashlight", "torch" -> toggleFlashlight(target)
                "battery", "battery_status" -> getBatteryInfo()
                "device_info", "system_info" -> getDeviceInfo()
                "volume_up" -> adjustVolume(AudioManager.ADJUST_RAISE)
                "volume_down" -> adjustVolume(AudioManager.ADJUST_LOWER)
                "volume_mute" -> muteVolume()
                "volume_max" -> maxVolume()
                "ringer_silent" -> setRingerMode(AudioManager.RINGER_MODE_SILENT)
                "ringer_vibrate" -> setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
                "ringer_normal" -> setRingerMode(AudioManager.RINGER_MODE_NORMAL)

                // System Settings Shortcuts
                "wifi_settings" -> openSystemSetting(Settings.ACTION_WIFI_SETTINGS, "Wi-Fi Settings")
                "bluetooth_settings" -> openSystemSetting(Settings.ACTION_BLUETOOTH_SETTINGS, "Bluetooth Settings")
                "display_settings" -> openSystemSetting(Settings.ACTION_DISPLAY_SETTINGS, "Display Settings")
                "sound_settings" -> openSystemSetting(Settings.ACTION_SOUND_SETTINGS, "Sound Settings")
                "date_settings" -> openSystemSetting(Settings.ACTION_DATE_SETTINGS, "Date & Time Settings")
                "hotspot_settings" -> openSystemSetting(Settings.ACTION_WIRELESS_SETTINGS, "Hotspot Settings")
                "location_settings", "gps_settings" -> openSystemSetting(Settings.ACTION_LOCATION_SOURCE_SETTINGS, "Location / GPS Settings")
                "airplane_settings" -> openSystemSetting(Settings.ACTION_AIRPLANE_MODE_SETTINGS, "Airplane Mode Settings")
                "accessibility_settings" -> openSystemSetting(Settings.ACTION_ACCESSIBILITY_SETTINGS, "Accessibility Settings")
                "app_settings", "manage_apps" -> openSystemSetting(Settings.ACTION_APPLICATION_SETTINGS, "App Management Settings")
                "storage_settings" -> openSystemSetting(Settings.ACTION_INTERNAL_STORAGE_SETTINGS, "Storage Settings")

                // Device Apps & Media
                "camera", "take_photo" -> openCamera()
                "gallery", "photos" -> openGallery()
                "voice_recorder", "recorder", "record_audio" -> openVoiceRecorder()
                "calendar", "calendar_event", "add_event" -> addCalendarEvent(target ?: "Aether Reminder", extra)
                "email", "send_email", "mail" -> sendEmail(target ?: "", extra, null)
                "files", "downloads", "file_manager" -> openFiles()
                "clock", "alarms", "stopwatch" -> openClock()

                // Accessibility System Gestures
                "read_screen" -> readScreen()
                "go_back" -> goBack()
                "go_home" -> goHome()
                "screenshot" -> takeScreenshot()
                "lock_screen" -> lockScreen()
                "notifications" -> openNotifications()
                "quick_settings" -> openQuickSettings()
                "recents" -> openRecents()
                "split_screen" -> toggleSplitScreen()

                // Smart Utilities & Math
                "math", "calculate" -> evaluateMath(target ?: extra ?: "")
                "unit_convert" -> convertUnit(target ?: "", extra ?: "")
                "coin_flip" -> flipCoin()
                "dice_roll" -> rollDice()
                "random_number" -> generateRandomNumber(target)
                "generate_password" -> generatePassword(target?.toIntOrNull() ?: 12)
                "copy_clipboard" -> copyToClipboard(target ?: extra ?: "")
                "read_clipboard" -> readClipboard()
                "clear_clipboard" -> clearClipboard()
                "daily_briefing" -> getDailyBriefing()
                "translate", "translate_text" -> quickTranslate(target ?: extra ?: "", extra)
                "nearby", "nearby_search" -> searchNearby(target ?: extra ?: "restaurants")
                "speed_test" -> speedTest()

                else -> ExecutionResult.Failure("Unknown action: $actionType")
            }
        } catch (e: Exception) {
            ExecutionResult.Failure("Action failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    // ------------------------------------------------------------------------
    // Hardware & Utilities
    // ------------------------------------------------------------------------
    fun toggleFlashlight(desiredState: String? = null): ExecutionResult {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return ExecutionResult.Failure("No camera flash found.")
            val newState = when (desiredState?.lowercase()?.trim()) {
                "on", "true", "enable" -> true
                "off", "false", "disable" -> false
                else -> !isTorchOn
            }
            cameraManager.setTorchMode(cameraId, newState)
            isTorchOn = newState
            ExecutionResult.Success(if (isTorchOn) "Flashlight turned ON" else "Flashlight turned OFF")
        } catch (e: Exception) {
            ExecutionResult.Failure("Unable to control flashlight: ${e.localizedMessage}")
        }
    }

    fun getBatteryInfo(): ExecutionResult {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryPct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = bm.isCharging
        val status = if (isCharging) "Charging" else "Discharging"
        return ExecutionResult.Success("Battery is at $batteryPct% ($status)")
    }

    fun getDeviceInfo(): ExecutionResult {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val availRamMb = memInfo.availMem / (1024 * 1024)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)

        val stat = StatFs(Environment.getDataDirectory().path)
        val availStorageGb = (stat.availableBlocksLong * stat.blockSizeLong) / (1024 * 1024 * 1024)

        val info = "Model: ${Build.MANUFACTURER} ${Build.MODEL} | Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) | RAM: ${availRamMb}MB free of ${totalRamMb}MB | Storage: ${availStorageGb}GB free"
        return ExecutionResult.Success(info)
    }

    private fun adjustVolume(direction: Int): ExecutionResult {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
        return ExecutionResult.Success(if (direction == AudioManager.ADJUST_RAISE) "Volume increased" else "Volume decreased")
    }

    private fun muteVolume(): ExecutionResult {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
        return ExecutionResult.Success("Media volume muted")
    }

    private fun maxVolume(): ExecutionResult {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, AudioManager.FLAG_SHOW_UI)
        return ExecutionResult.Success("Media volume set to maximum")
    }

    private fun setRingerMode(mode: Int): ExecutionResult {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return try {
            audioManager.ringerMode = mode
            val modeName = when (mode) {
                AudioManager.RINGER_MODE_SILENT -> "Silent"
                AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
                else -> "Normal"
            }
            ExecutionResult.Success("Phone set to $modeName mode")
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
            ExecutionResult.Success("Opened Sound Settings")
        }
    }

    private fun openSystemSetting(action: String, name: String): ExecutionResult {
        val intent = Intent(action).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        context.startActivity(intent)
        return ExecutionResult.Success("Opened $name")
    }

    // ------------------------------------------------------------------------
    // Social Reels, Shorts & TikTok Navigation & Gestures
    // ------------------------------------------------------------------------
    private fun openYouTubeShorts(): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/shorts")).apply {
            setPackage("com.google.android.youtube")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Opening YouTube Shorts")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/shorts")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening YouTube Shorts Web")
        }
    }

    private fun openFacebookReels(): ExecutionResult {
        val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/reel/")).apply {
            setPackage("com.facebook.katana")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(fbIntent)
            ExecutionResult.Success("Opening Facebook Reels")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/reel/")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening Facebook Reels Web")
        }
    }

    fun openFacebookProfile(url: String = "https://www.facebook.com/Siddik9980"): ExecutionResult {
        val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=$url")).apply {
            setPackage("com.facebook.katana")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(fbIntent)
            ExecutionResult.Success("Opening Facebook Profile: $url")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening Facebook Profile: $url")
        }
    }

    fun openFacebookPage(url: String = "https://www.facebook.com/profile.php?id=61557614412925"): ExecutionResult {
        val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=$url")).apply {
            setPackage("com.facebook.katana")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(fbIntent)
            ExecutionResult.Success("Opening Facebook Page: $url")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening Facebook Page: $url")
        }
    }

    fun openTelegramChannel(url: String = "https://t.me/SRSabbir"): ExecutionResult {
        val tgIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("org.telegram.messenger")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(tgIntent)
            ExecutionResult.Success("Opening Telegram Channel: $url")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening Telegram Channel: $url")
        }
    }

    fun openYouTubeChannel(url: String = "https://www.youtube.com/@SRSabbir9980"): ExecutionResult {
        val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.google.android.youtube")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(ytIntent)
            ExecutionResult.Success("Opening YouTube Channel: $url")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening YouTube Channel: $url")
        }
    }

    private fun openTikTok(): ExecutionResult {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage("com.zhiliaoapp.musically")
            ?: pm.getLaunchIntentForPackage("com.zhiliaoapp.musically.go")
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return ExecutionResult.Success("Opening TikTok")
        }
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
        return ExecutionResult.Success("Opening TikTok Web")
    }

    private fun nextReel(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.swipeNextReel()) {
            ExecutionResult.Success("Swiped to next video / reel")
        } else {
            ExecutionResult.Failure("Accessibility Service needed to scroll reels.")
        }
    }

    private fun previousReel(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.swipePreviousReel()) {
            ExecutionResult.Success("Swiped to previous video / reel")
        } else {
            ExecutionResult.Failure("Accessibility Service needed to scroll reels.")
        }
    }

    private fun likeReel(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.doubleTapLike()) {
            ExecutionResult.Success("Liked video / double tapped")
        } else {
            ExecutionResult.Failure("Accessibility Service needed.")
        }
    }

    private fun commentOnReel(commentText: String): ExecutionResult {
        val service = AetherAccessibilityService.instance
        if (service == null) {
            return ExecutionResult.Failure("Accessibility Service needed to open comments and type.")
        }
        val opened = service.openCommentsAndType(commentText.ifBlank { null })
        return if (opened) {
            ExecutionResult.Success(if (commentText.isNotBlank()) "Commented: \"$commentText\"" else "Opened comments tray")
        } else {
            copyToClipboard(commentText)
            ExecutionResult.Success("Comment copied to clipboard: \"$commentText\". Tap comment box to paste.")
        }
    }

    private fun startAutoScroll(seconds: Int): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null) {
            service.startAutoScroll(seconds)
            ExecutionResult.Success("Hands-free auto-scrolling started (swiping every $seconds seconds)")
        } else {
            ExecutionResult.Failure("Accessibility Service needed for hands-free auto-scrolling.")
        }
    }

    private fun stopAutoScroll(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null) {
            service.stopAutoScroll()
            ExecutionResult.Success("Auto-scrolling stopped.")
        } else {
            ExecutionResult.Success("Auto-scrolling is off.")
        }
    }

    // ------------------------------------------------------------------------
    // Accessibility Commands
    // ------------------------------------------------------------------------
    private fun takeScreenshot(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performScreenshot()) {
            ExecutionResult.Success("Captured Screenshot")
        } else {
            ExecutionResult.Failure("Accessibility Service needed to capture screenshot.")
        }
    }

    private fun lockScreen(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performLock()) {
            ExecutionResult.Success("Device locked")
        } else {
            ExecutionResult.Failure("Accessibility Service needed to lock screen.")
        }
    }

    private fun openNotifications(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performNotifications()) {
            ExecutionResult.Success("Pulled down notifications")
        } else {
            ExecutionResult.Failure("Accessibility Service required.")
        }
    }

    private fun openQuickSettings(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performQuickSettings()) {
            ExecutionResult.Success("Opened Quick Settings")
        } else {
            ExecutionResult.Failure("Accessibility Service required.")
        }
    }

    private fun openRecents(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performRecents()) {
            ExecutionResult.Success("Opened Recent Apps")
        } else {
            ExecutionResult.Failure("Accessibility Service required.")
        }
    }

    private fun toggleSplitScreen(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performSplitScreen()) {
            ExecutionResult.Success("Toggled Split Screen")
        } else {
            ExecutionResult.Failure("Accessibility Service required.")
        }
    }

    // ------------------------------------------------------------------------
    // Smart Calculator & Text Tools
    // ------------------------------------------------------------------------
    private fun evaluateMath(expression: String): ExecutionResult {
        val clean = expression.replace("x", "*").replace("÷", "/").replace(" ", "").trim()
        val result = try {
            calculateSimple(clean)
        } catch (_: Exception) {
            "Unable to compute '$expression'"
        }
        return ExecutionResult.Success("$expression = $result")
    }

    private fun calculateSimple(expr: String): String {
        return when {
            expr.contains("+") -> {
                val parts = expr.split("+")
                (parts[0].toDouble() + parts[1].toDouble()).toString()
            }
            expr.contains("-") -> {
                val parts = expr.split("-")
                (parts[0].toDouble() - parts[1].toDouble()).toString()
            }
            expr.contains("*") -> {
                val parts = expr.split("*")
                (parts[0].toDouble() * parts[1].toDouble()).toString()
            }
            expr.contains("/") -> {
                val parts = expr.split("/")
                (parts[0].toDouble() / parts[1].toDouble()).toString()
            }
            expr.contains("%") -> {
                val num = expr.replace("%", "").toDouble()
                (num / 100.0).toString()
            }
            else -> expr
        }
    }

    private fun convertUnit(from: String, valueStr: String): ExecutionResult {
        val v = valueStr.toDoubleOrNull() ?: 1.0
        val res = when (from.lowercase().trim()) {
            "c_to_f" -> "${v}°C = ${String.format("%.1f", (v * 9 / 5) + 32)}°F"
            "f_to_c" -> "${v}°F = ${String.format("%.1f", (v - 32) * 5 / 9)}°C"
            "km_to_mi" -> "$v km = ${String.format("%.2f", v * 0.621371)} miles"
            "mi_to_km" -> "$v miles = ${String.format("%.2f", v * 1.60934)} km"
            "kg_to_lbs" -> "$v kg = ${String.format("%.2f", v * 2.20462)} lbs"
            "lbs_to_kg" -> "$v lbs = ${String.format("%.2f", v * 0.453592)} kg"
            else -> "Converted $v $from"
        }
        return ExecutionResult.Success(res)
    }

    private fun flipCoin(): ExecutionResult {
        val outcome = if (Random.nextBoolean()) "HEADS" else "TAILS"
        return ExecutionResult.Success("Coin Flip Result: $outcome")
    }

    private fun rollDice(): ExecutionResult {
        val roll = Random.nextInt(1, 7)
        return ExecutionResult.Success("Rolled a die: $roll")
    }

    private fun generateRandomNumber(range: String?): ExecutionResult {
        val max = range?.toIntOrNull() ?: 100
        val num = Random.nextInt(1, max + 1)
        return ExecutionResult.Success("Random Number (1 to $max): $num")
    }

    private fun generatePassword(length: Int): ExecutionResult {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        val pass = (1..length).map { chars.random() }.joinToString("")
        copyToClipboard(pass)
        return ExecutionResult.Success("Generated Password: $pass (Copied to clipboard)")
    }

    private fun copyToClipboard(text: String): ExecutionResult {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Aether AI", text)
        clipboard.setPrimaryClip(clip)
        return ExecutionResult.Success("Copied to clipboard: \"$text\"")
    }

    private fun getDailyBriefing(): ExecutionResult {
        val dateStr = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault()).format(Date())
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryPct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        val briefing = "$greeting! Today is $dateStr. Your battery is at $batteryPct%. Aether floating assistant is active and ready to help you multitask."
        return ExecutionResult.Success(briefing)
    }

    // ------------------------------------------------------------------------
    // Standard Apps & Intents
    // ------------------------------------------------------------------------
    private fun openApp(appName: String): ExecutionResult {
        if (appName.isBlank()) return ExecutionResult.Failure("No app name provided.")
        val pm = context.packageManager
        val knownPackages = mapOf(
            "youtube" to "com.google.android.youtube",
            "spotify" to "com.spotify.music",
            "whatsapp" to "com.whatsapp",
            "camera" to "com.google.android.GoogleCamera",
            "chrome" to "com.android.chrome",
            "settings" to "com.android.settings",
            "maps" to "com.google.android.apps.maps",
            "calculator" to "com.google.android.calculator",
            "clock" to "com.google.android.deskclock",
            "play store" to "com.android.vending",
            "gmail" to "com.google.android.gm"
        )
        val clean = appName.lowercase().trim()
        val direct = knownPackages[clean]
        if (direct != null) {
            val launch = pm.getLaunchIntentForPackage(direct)
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launch)
                return ExecutionResult.Success("Launched $appName")
            }
        }
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        for (resolveInfo in resolveInfos) {
            val label = resolveInfo.loadLabel(pm).toString().lowercase()
            if (label.contains(clean) || clean.contains(label)) {
                val intent = pm.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return ExecutionResult.Success("Opened ${resolveInfo.loadLabel(pm)}")
                }
            }
        }
        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=$appName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(marketIntent)
            ExecutionResult.Success("Searching Play Store for $appName")
        } catch (_: Exception) {
            ExecutionResult.Failure("Could not find installed app matching '$appName'.")
        }
    }

    private fun callContact(contactOrPhone: String): ExecutionResult {
        if (contactOrPhone.isBlank()) return ExecutionResult.Failure("No contact or number specified.")
        val clean = contactOrPhone.trim()
        val phoneNumber = if (clean.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }) clean else findContactPhoneNumber(clean)
        val targetNum = phoneNumber ?: clean
        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
        val intent = if (hasCallPermission && phoneNumber != null) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:${Uri.encode(targetNum)}"))
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(targetNum)}"))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ExecutionResult.Success("Initiating call to $contactOrPhone ($targetNum)")
    }

    private fun findContactPhoneNumber(contactName: String): String? {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasReadPermission) return null
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
            arrayOf("%$contactName%"),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val numIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (numIndex != -1) return it.getString(numIndex)
            }
        }
        return null
    }

    private fun sendSms(contactOrPhone: String, message: String): ExecutionResult {
        val phoneNumber = findContactPhoneNumber(contactOrPhone) ?: contactOrPhone
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${Uri.encode(phoneNumber)}")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ExecutionResult.Success("Opened SMS to $contactOrPhone")
    }

    private fun sendWhatsApp(contact: String?, message: String): ExecutionResult {
        val encodedMsg = Uri.encode(message)
        val url = if (!contact.isNullOrBlank()) {
            val phone = findContactPhoneNumber(contact)?.replace("+", "")?.replace(" ", "")?.replace("-", "")
            if (phone != null) "https://api.whatsapp.com/send?phone=$phone&text=$encodedMsg"
            else "https://api.whatsapp.com/send?text=$encodedMsg"
        } else {
            "https://api.whatsapp.com/send?text=$encodedMsg"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Sent message to WhatsApp")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opened WhatsApp Web")
        }
    }

    private fun setAlarm(timeStr: String, label: String): ExecutionResult {
        var hour = 8
        var minute = 0
        val parts = timeStr.lowercase().replace("am", "").replace("pm", "").trim().split(":", ".")
        if (parts.isNotEmpty()) {
            hour = parts[0].trim().toIntOrNull() ?: 8
            if (parts.size > 1) minute = parts[1].trim().toIntOrNull() ?: 0
            if (timeStr.lowercase().contains("pm") && hour < 12) hour += 12
            else if (timeStr.lowercase().contains("am") && hour == 12) hour = 0
        }
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ExecutionResult.Success("Set alarm for %02d:%02d".format(hour, minute))
    }

    private fun setTimer(secondsStr: String, label: String): ExecutionResult {
        val seconds = secondsStr.toIntOrNull() ?: 300
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ExecutionResult.Success("Timer started for $seconds seconds")
    }

    private fun saveNote(title: String, content: String): ExecutionResult {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content.ifBlank { title })
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(sendIntent, "Save note to...").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
        return ExecutionResult.Success("Note saved: $title")
    }

    private fun searchWeb(query: String): ExecutionResult {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Searching web for '$query'")
        } catch (_: Exception) {
            val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(urlIntent)
            ExecutionResult.Success("Opened search for '$query'")
        }
    }

    private fun navigateTo(destination: String): ExecutionResult {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(destination)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(mapIntent)
            ExecutionResult.Success("Navigating to $destination")
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(destination)}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ExecutionResult.Success("Opening maps to $destination")
        }
    }

    private fun playYouTube(query: String): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ExecutionResult.Success("Playing '$query' on YouTube")
    }

    private fun readScreen(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        if (service == null) {
            return ExecutionResult.Failure("Accessibility Service is not enabled. Please enable Aether Accessibility Service in Android Settings to read on-screen text.")
        }
        return ExecutionResult.ScreenText(service.extractScreenText())
    }

    private fun goBack(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performBack()) ExecutionResult.Success("Navigated Back")
        else ExecutionResult.Failure("Accessibility Service required to perform Back navigation.")
    }

    private fun goHome(): ExecutionResult {
        val service = AetherAccessibilityService.instance
        return if (service != null && service.performHome()) ExecutionResult.Success("Navigated to Home screen")
        else {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(homeIntent)
            ExecutionResult.Success("Navigated Home")
        }
    }

    private fun openCamera(): ExecutionResult {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Camera opened")
        } catch (_: Exception) {
            openApp("camera")
        }
    }

    private fun openGallery(): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Gallery opened")
        } catch (_: Exception) {
            openApp("photos")
        }
    }

    private fun openVoiceRecorder(): ExecutionResult {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Sound Recorder opened")
        } catch (_: Exception) {
            val pm = context.packageManager
            val soundLaunch = pm.getLaunchIntentForPackage("com.google.android.soundrecorder")
                ?: pm.getLaunchIntentForPackage("com.sec.android.app.voicenote")
            if (soundLaunch != null) {
                soundLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(soundLaunch)
                ExecutionResult.Success("Sound Recorder opened")
            } else {
                searchWeb("voice recorder app")
            }
        }
    }

    private fun addCalendarEvent(title: String, description: String?): ExecutionResult {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title.ifBlank { "Aether Reminder" })
            if (!description.isNullOrBlank()) {
                putExtra(CalendarContract.Events.DESCRIPTION, description)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Opened Calendar to add: $title")
        } catch (_: Exception) {
            ExecutionResult.Failure("No Calendar app found.")
        }
    }

    private fun sendEmail(recipient: String, subject: String?, body: String?): ExecutionResult {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${Uri.encode(recipient.trim())}")
            putExtra(Intent.EXTRA_SUBJECT, subject ?: "Note from Aether AI")
            putExtra(Intent.EXTRA_TEXT, body ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success(if (recipient.isNotBlank()) "Opening email to $recipient" else "Opening email client")
        } catch (_: Exception) {
            ExecutionResult.Failure("Could not open email application.")
        }
    }

    private fun openFiles(): ExecutionResult {
        val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Opened Downloads & Files")
        } catch (_: Exception) {
            openApp("files")
        }
    }

    private fun openClock(): ExecutionResult {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Opened Clock & Alarms")
        } catch (_: Exception) {
            openApp("clock")
        }
    }

    private fun readClipboard(): ExecutionResult {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: ""
            if (text.isNotBlank()) {
                ExecutionResult.Success("Clipboard content: \"$text\"")
            } else {
                ExecutionResult.Success("Clipboard has empty text.")
            }
        } else {
            ExecutionResult.Success("Clipboard is currently empty.")
        }
    }

    private fun clearClipboard(): ExecutionResult {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipboard.clearPrimaryClip()
        } else {
            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
        }
        return ExecutionResult.Success("Clipboard cleared securely.")
    }

    private fun quickTranslate(text: String, targetLang: String?): ExecutionResult {
        val lang = targetLang ?: "en"
        val query = if (text.isNotBlank()) text else "Hello"
        val url = "https://translate.google.com/?sl=auto&tl=$lang&text=${Uri.encode(query)}&op=translate"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ExecutionResult.Success("Translating: \"$query\" ($lang)")
        } catch (_: Exception) {
            searchWeb("translate $query to $lang")
        }
    }

    private fun searchNearby(query: String): ExecutionResult {
        val clean = query.ifBlank { "restaurants" }
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(clean)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(mapIntent)
            ExecutionResult.Success("Searching nearby for $clean on Maps")
        } catch (_: Exception) {
            searchWeb("nearby $clean")
        }
    }

    private fun speedTest(): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fast.com")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ExecutionResult.Success("Launching network speed test")
    }
}
