package com.example.data.network

import com.example.BuildConfig
import com.example.data.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class AgentTaskStep(
    val actionType: String,
    val actionTarget: String? = null,
    val actionExtra: String? = null
)

data class AiResponse(
    val replyText: String,
    val actionType: String? = null,
    val actionTarget: String? = null,
    val actionExtra: String? = null,
    val subActions: List<AgentTaskStep> = emptyList()
)

class AiService(private val preferences: AppPreferences) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val systemPrompt = """
        You are Aether AI, an Autonomous Android AI Agent with full device execution capabilities, multi-step workflow planning, and 100+ native actions.
        Languages supported: English, Bengali (বাংলা), Hindi (हिन्दी), Arabic, or any requested language. Keep conversational replies crisp, warm, and natural for voice speaking (1-2 sentences).
        
        When the user asks you to perform an action or multi-step routine on their phone, output a strict JSON object:
        {
          "action": "<primary_action_name>",
          "target": "<target string, number, app, contact, search query, or destination>",
          "extra": "<message text, notes body, alarm hour/minute, timer seconds, or details>",
          "steps": [
            { "action": "<action_1>", "target": "<target_1>", "extra": "<extra_1>" },
            { "action": "<action_2>", "target": "<target_2>", "extra": "<extra_2>" }
          ],
          "reply": "<natural spoken response to the user in their preferred language>"
        }

        Supported Agent Actions:
        - "open_app": target = name of app (e.g. Spotify, YouTube, WhatsApp, Camera, Chrome, Calculator, Settings)
        - "camera": open camera to take photo or video
        - "gallery": open device photos / gallery
        - "voice_recorder": open sound recorder
        - "calendar": target = event title, extra = description
        - "email": target = recipient email, extra = subject or body
        - "files": open downloads or device files
        - "clock": open clock, alarms, or stopwatch
        - "call": target = contact name or phone number
        - "sms": target = contact name or number, extra = message body
        - "whatsapp": target = contact name, extra = message text
        - "alarm": target = "HH:mm" (e.g. "07:30" or "7:30 AM"), extra = label
        - "timer": target = duration in seconds (e.g. "300" for 5 min)
        - "note": target = note title, extra = note content
        - "search": target = query to search on Google
        - "navigate": target = address/destination for Google Maps
        - "nearby": target = "restaurants", "gas stations", "hospitals", "atms"
        - "speed_test": check internet connection speed
        - "translate": target = text to translate, extra = target language (e.g. "bn", "en", "hi", "ar")
        - "youtube": target = song, artist, video title
        - "developer_facebook": open developer Facebook ID / Profile (https://www.facebook.com/Siddik9980)
        - "facebook_page": open developer Facebook Page (https://www.facebook.com/profile.php?id=61557614412925)
        - "telegram_channel": open Telegram Channel (https://t.me/SRSabbir)
        - "youtube_channel": open YouTube Channel (https://www.youtube.com/@SRSabbir9980)
        - "youtube_shorts": open YouTube Shorts feed
        - "facebook_reels": open Facebook Reels feed
        - "tiktok": open TikTok feed
        - "next_reel": scroll / swipe to next video or reel
        - "prev_reel": scroll / swipe to previous video or reel
        - "like_reel": like / double-tap the current reel
        - "comment_reel": open comment tray and type comment, target = comment text
        - "auto_scroll": hands-free auto-scrolling reels, target = seconds interval (e.g. "15")
        - "stop_auto_scroll": stop auto-scrolling
        - "flashlight": target = "on" | "off" | "toggle"
        - "battery": target = "status"
        - "device_info": target = "specs"
        - "volume_up", "volume_down", "volume_mute", "volume_max"
        - "ringer_silent", "ringer_vibrate", "ringer_normal"
        - "wifi_settings", "bluetooth_settings", "display_settings", "hotspot_settings", "location_settings", "storage_settings"
        - "screenshot", "lock_screen", "notifications", "quick_settings", "recents", "split_screen"
        - "math": target = math expression (e.g. "25 * 4 + 10")
        - "unit_convert": target = unit type (e.g. "c_to_f", "km_to_mi", "kg_to_lbs"), extra = value
        - "coin_flip", "dice_roll"
        - "random_number": target = max number (e.g. "100")
        - "generate_password": target = length (e.g. "12")
        - "read_clipboard", "clear_clipboard", "copy_clipboard"
        - "daily_briefing": target = "today"
        - "read_screen": target = "screen", extra = question
        - "go_back", "go_home"
        - "none": normal conversation, questions, reasoning, advice. Put answer in "reply".

        IMPORTANT: Always include the "reply" field with your natural spoken response.
    """.trimIndent()

    suspend fun sendMessage(
        userMessage: String,
        recentHistory: List<Pair<String, String>> = emptyList(),
        screenContext: String? = null
    ): Result<AiResponse> = withContext(Dispatchers.IO) {
        val provider = preferences.getProvider()
        val language = preferences.getLanguage()

        val fullUserPrompt = buildString {
            if (!screenContext.isNullOrBlank()) {
                append("[Context of current screen text: \"$screenContext\"]\n\n")
            }
            if (language != "en") {
                append("[Respond in $language language]\n")
            }
            append(userMessage)
        }

        try {
            if (provider == "groq") {
                sendGroqRequest(fullUserPrompt, recentHistory)
            } else {
                sendGeminiRequest(fullUserPrompt, recentHistory)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sendGeminiRequest(
        userMessage: String,
        recentHistory: List<Pair<String, String>>
    ): Result<AiResponse> {
        var key = preferences.getGeminiApiKey()
        if (key.isBlank()) {
            key = try {
                val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
                field.get(null) as? String ?: ""
            } catch (_: Exception) {
                ""
            }
        }

        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return Result.success(getFallbackOfflineResponse(userMessage))
        }

        val model = preferences.getSelectedModel().ifBlank { "gemini-2.0-flash" }
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key"

        val contentsArray = JSONArray()
        for ((role, content) in recentHistory.takeLast(6)) {
            val part = JSONObject().put("text", content)
            val msgObj = JSONObject()
                .put("role", if (role == "assistant") "model" else "user")
                .put("parts", JSONArray().put(part))
            contentsArray.put(msgObj)
        }

        val currentPart = JSONObject().put("text", userMessage)
        val currentMsg = JSONObject().put("role", "user").put("parts", JSONArray().put(currentPart))
        contentsArray.put(currentMsg)

        val rootJson = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("maxOutputTokens", 800)
            })
        }

        val body = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "Empty body"
                throw IOException("Gemini API error (${response.code}): $errBody")
            }
            val resStr = response.body?.string() ?: throw IOException("Empty response")
            val parsed = parseGeminiOutput(resStr)
            return Result.success(parsed)
        }
    }

    private fun sendGroqRequest(
        userMessage: String,
        recentHistory: List<Pair<String, String>>
    ): Result<AiResponse> {
        val keys = preferences.getGroqApiKeys()
        if (keys.isEmpty()) {
            return Result.success(getFallbackOfflineResponse(userMessage))
        }

        val model = preferences.getSelectedModel().ifBlank { "llama-3.3-70b-versatile" }
        val url = "https://api.groq.com/openai/v1/chat/completions"

        var attempts = 0
        val maxAttempts = keys.size.coerceAtLeast(1)

        while (attempts < maxAttempts) {
            val currentKey = preferences.getCurrentGroqKey() ?: return Result.failure(Exception("No Groq key available"))

            val messagesArray = JSONArray()
            messagesArray.put(JSONObject().put("role", "system").put("content", systemPrompt))

            for ((role, content) in recentHistory.takeLast(6)) {
                messagesArray.put(JSONObject().put("role", if (role == "assistant") "assistant" else "user").put("content", content))
            }
            messagesArray.put(JSONObject().put("role", "user").put("content", userMessage))

            val rootJson = JSONObject().apply {
                put("model", model)
                put("messages", messagesArray)
                put("temperature", 0.3)
                put("max_tokens", 800)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $currentKey")
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.code == 429) {
                    preferences.rotateGroqKey()
                    attempts++
                    if (attempts >= maxAttempts) {
                        throw IOException("All Groq API keys rate limited (429). Please add more keys or try later.")
                    }
                    return@use
                }

                if (!response.isSuccessful) {
                    val err = response.body?.string() ?: ""
                    throw IOException("Groq API error (${response.code}): $err")
                }

                val resStr = response.body?.string() ?: throw IOException("Empty response")
                val parsed = parseGroqOutput(resStr)
                return Result.success(parsed)
            }
        }

        return Result.failure(IOException("Failed to connect to Groq"))
    }

    private fun parseGeminiOutput(rawJson: String): AiResponse {
        val root = JSONObject(rawJson)
        val candidates = root.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                val text = parts.getJSONObject(0).optString("text", "")
                return parseAgentJson(text)
            }
        }
        return AiResponse("I heard you, but could not formulate a response.")
    }

    private fun parseGroqOutput(rawJson: String): AiResponse {
        val root = JSONObject(rawJson)
        val choices = root.optJSONArray("choices")
        if (choices != null && choices.length() > 0) {
            val msg = choices.getJSONObject(0).optJSONObject("message")
            val content = msg?.optString("content", "") ?: ""
            return parseAgentJson(content)
        }
        return AiResponse("I heard you, but could not formulate a response.")
    }

    private fun parseAgentJson(rawText: String): AiResponse {
        val trimmed = rawText.trim()
        val jsonString = if (trimmed.contains("{") && trimmed.contains("}")) {
            val start = trimmed.indexOf("{")
            val end = trimmed.lastIndexOf("}")
            trimmed.substring(start, end + 1)
        } else {
            null
        }

        if (jsonString != null) {
            try {
                val json = JSONObject(jsonString)
                val action = json.optString("action", "none")
                val target = json.optString("target", null)
                val extra = json.optString("extra", null)
                val reply = json.optString("reply", trimmed)

                val subList = mutableListOf<AgentTaskStep>()
                val stepsArray = json.optJSONArray("steps")
                if (stepsArray != null) {
                    for (i in 0 until stepsArray.length()) {
                        val sObj = stepsArray.optJSONObject(i) ?: continue
                        val sAction = sObj.optString("action", "")
                        if (sAction.isNotBlank() && sAction != "none") {
                            subList.add(
                                AgentTaskStep(
                                    actionType = sAction,
                                    actionTarget = sObj.optString("target", null),
                                    actionExtra = sObj.optString("extra", null)
                                )
                            )
                        }
                    }
                }

                return AiResponse(
                    replyText = reply.ifBlank { "Action executed: $action" },
                    actionType = if (action == "none" || action.isBlank()) null else action,
                    actionTarget = target,
                    actionExtra = extra,
                    subActions = subList
                )
            } catch (_: Exception) {}
        }

        return AiResponse(replyText = trimmed)
    }

    private fun getFallbackOfflineResponse(userPrompt: String): AiResponse {
        val lower = userPrompt.lowercase()
        return when {
            // Autonomous Agent Multi-Step Routines
            lower.contains("morning routine") || lower.contains("সকালের রুটিন") || lower.contains("গুড মর্নিং") || lower.contains("শুভ সকাল") -> {
                AiResponse(
                    replyText = "Good morning! Executing autonomous morning agent routine for you.",
                    actionType = "daily_briefing",
                    actionTarget = "today",
                    subActions = listOf(
                        AgentTaskStep("battery", "status"),
                        AgentTaskStep("search", "top news headlines today")
                    )
                )
            }
            lower.contains("night routine") || lower.contains("ঘুমের রুটিন") || lower.contains("ঘুমাতে যাচ্ছি") || lower.contains("নাইট মোড") -> {
                AiResponse(
                    replyText = "Good night! Preparing bedtime agent routine: alarm set for 7:00 AM, phone silenced, volume muted, and screen locked.",
                    actionType = "alarm",
                    actionTarget = "07:00",
                    actionExtra = "Morning Alarm",
                    subActions = listOf(
                        AgentTaskStep("ringer_silent"),
                        AgentTaskStep("volume_mute"),
                        AgentTaskStep("flashlight", "off"),
                        AgentTaskStep("lock_screen")
                    )
                )
            }
            lower.contains("focus mode") || lower.contains("ফোকাস মোড") || lower.contains("পড়াশোনা") || lower.contains("study mode") -> {
                AiResponse(
                    replyText = "Activating 45-minute Deep Focus Session. Phone placed on vibrate, media muted, and session note saved.",
                    actionType = "timer",
                    actionTarget = "2700",
                    actionExtra = "Deep Focus (45m)",
                    subActions = listOf(
                        AgentTaskStep("ringer_vibrate"),
                        AgentTaskStep("volume_mute"),
                        AgentTaskStep("note", "Deep Focus Session", "Started focused work session")
                    )
                )
            }
            lower.contains("meeting mode") || lower.contains("মিটিং মোড") || lower.contains("মিটিং এ আছি") -> {
                AiResponse(
                    replyText = "Activating Meeting Mode. Silencing phone and media volume.",
                    actionType = "ringer_silent",
                    subActions = listOf(
                        AgentTaskStep("volume_mute"),
                        AgentTaskStep("note", "Meeting Note", "In meeting - call back later")
                    )
                )
            }
            lower.contains("diagnostic") || lower.contains("সিস্টেম চেক") || lower.contains("ফোন চেক") || lower.contains("device check") -> {
                AiResponse(
                    replyText = "Running device system diagnostics and health report.",
                    actionType = "device_info",
                    subActions = listOf(
                        AgentTaskStep("battery", "status"),
                        AgentTaskStep("storage_settings")
                    )
                )
            }

            // Camera & Media
            lower.contains("camera") || lower.contains("ক্যামেরা") || lower.contains("ছবি তোল") || lower.contains("ফটো তোল") -> {
                AiResponse("Opening Camera for you.", actionType = "camera")
            }
            lower.contains("gallery") || lower.contains("গ্যালারি") || lower.contains("ছবি দেখ") -> {
                AiResponse("Opening your Photos & Gallery.", actionType = "gallery")
            }
            lower.contains("recorder") || lower.contains("রেকর্ড") || lower.contains("ভয়েস রেকর্ড") -> {
                AiResponse("Opening Voice Recorder.", actionType = "voice_recorder")
            }
            lower.contains("calendar") || lower.contains("ক্যালেন্ডার") || lower.contains("ইভেন্ট") -> {
                AiResponse("Opening Calendar to schedule an event.", actionType = "calendar", actionTarget = "Aether Scheduled Event")
            }
            lower.contains("email") || lower.contains("ইমেইল") || lower.contains("মেইল") -> {
                val emailTarget = userPrompt.substringAfter("to ").substringAfter("প্রতি ").trim()
                AiResponse("Opening email client.", actionType = "email", actionTarget = if (emailTarget.contains("@")) emailTarget else "")
            }
            lower.contains("file") || lower.contains("ফাইল") || lower.contains("ডাউনলোড") -> {
                AiResponse("Opening Downloads and File Manager.", actionType = "files")
            }
            lower.contains("clock") || lower.contains("ঘড়ি") || lower.contains("স্টপওয়াচ") -> {
                AiResponse("Opening Clock and Alarms.", actionType = "clock")
            }
            lower.contains("speed test") || lower.contains("ইন্টারনেট স্পিড") || lower.contains("নেট স্পিড") -> {
                AiResponse("Checking internet speed on Fast.com.", actionType = "speed_test")
            }
            lower.contains("nearby") || lower.contains("কাছের") || lower.contains("আশেপাশের") -> {
                val place = if (lower.contains("হসপিটাল") || lower.contains("হাসপাতাল")) "hospital"
                else if (lower.contains("খাবার") || lower.contains("রেস্টুরেন্ট")) "restaurant"
                else if (lower.contains("পাম্প") || lower.contains("তেল")) "gas station"
                else if (lower.contains("এটিএম") || lower.contains("ব্যাংক")) "atm"
                else "restaurants"
                AiResponse("Searching nearby $place on Google Maps.", actionType = "nearby", actionTarget = place)
            }
            lower.contains("translate") || lower.contains("অনুবাদ") || lower.contains("অর্থ") -> {
                val textToTrans = userPrompt.replace("translate", "", ignoreCase = true)
                    .replace("অনুবাদ করো", "", ignoreCase = true)
                    .replace("অর্থ কি", "", ignoreCase = true)
                    .trim()
                AiResponse("Translating text: \"$textToTrans\".", actionType = "translate", actionTarget = textToTrans.ifBlank { "Hello" })
            }
            lower.contains("clipboard") || lower.contains("ক্লিপবোর্ড") -> {
                if (lower.contains("clear") || lower.contains("মুছে") || lower.contains("ক্লিয়ার")) {
                    AiResponse("Clearing clipboard.", actionType = "clear_clipboard")
                } else {
                    AiResponse("Checking clipboard content.", actionType = "read_clipboard")
                }
            }
            // Next Video / Next Reel (পরের ভিডিও / পরের রিল / পাল্টাও / পাল্টাতে / স্ক্রোল করো)
            lower.contains("next reel") || lower.contains("next video") || lower.contains("পরের ভিডিও") || lower.contains("পরের রিল") || lower.contains("পাল্টাও") || lower.contains("পাল্টাতে") || lower.contains("পাল্টাও ভিডিও") || lower.contains("ভিডিও পাল্টাও") || lower.contains("পরেরটা") || lower.contains("scroll down") || lower.contains("স্ক্রোল") || lower.contains("change video") || lower.contains("change reel") -> {
                AiResponse("Swiping to next video / reel for you.", actionType = "next_reel")
            }
            // Previous Video / Reel (আগের ভিডিও / আগের রিল)
            lower.contains("prev reel") || lower.contains("previous video") || lower.contains("আগের ভিডিও") || lower.contains("আগের রিল") || lower.contains("scroll up") -> {
                AiResponse("Swiping to previous video.", actionType = "prev_reel")
            }
            // Like Video / Reel (ভিডিওতে লাইক দাও / লাইক করো)
            lower.contains("like video") || lower.contains("like reel") || lower.contains("লাইক") -> {
                AiResponse("Liking current video.", actionType = "like_reel")
            }
            // Comment on Video / Reel (কমেন্ট করো / কমেন্টে লেখো / কমেন্ট করতে)
            lower.contains("comment") || lower.contains("কমেন্ট") || lower.contains("মন্তব্য") -> {
                val commentText = userPrompt.replace("comment on video", "", ignoreCase = true)
                    .replace("comment", "", ignoreCase = true)
                    .replace("কমেন্ট করো", "", ignoreCase = true)
                    .replace("কমেন্টে লেখো", "", ignoreCase = true)
                    .replace("কমেন্ট করতে", "", ignoreCase = true)
                    .replace("মন্তব্য করো", "", ignoreCase = true)
                    .trim()
                AiResponse(
                    replyText = if (commentText.isNotBlank()) "Writing comment: \"$commentText\"" else "Opening comments tray.",
                    actionType = "comment_reel",
                    actionTarget = commentText.ifBlank { null }
                )
            }
            // Auto-scroll reels (অটো স্ক্রোল)
            lower.contains("auto scroll") || lower.contains("অটো স্ক্রোল") || lower.contains("স্বয়ংক্রিয় স্ক্রোল") -> {
                if (lower.contains("stop") || lower.contains("বন্ধ")) {
                    AiResponse("Stopping auto-scroll.", actionType = "stop_auto_scroll")
                } else {
                    AiResponse("Starting hands-free auto-scrolling every 15 seconds.", actionType = "auto_scroll", actionTarget = "15")
                }
            }
            // YouTube Shorts / Videos
            lower.contains("shorts") || lower.contains("শর্টস") -> {
                AiResponse("Opening YouTube Shorts.", actionType = "youtube_shorts")
            }
            // Developer & Owner Facebook ID / Profile (ওনার আইডি / ডেভেলপার আইডি / ফেসবুক আইডি / সিদ্দিক / developer id / owner id)
            lower.contains("facebook id") || lower.contains("ফেসবুক আইডি") || lower.contains("siddik9980") || lower.contains("সিদ্দিক") || lower.contains("developer facebook") || lower.contains("creator facebook") || lower.contains("creator profile") || lower.contains("ওনার") || lower.contains("owner") || lower.contains("ডেপলপার") || lower.contains("ডেভলপার") || lower.contains("ডেভেলপার") || lower.contains("developer id") || lower.contains("owner id") -> {
                AiResponse("Opening Developer / Owner Facebook Profile: https://www.facebook.com/Siddik9980", actionType = "developer_facebook", actionTarget = "https://www.facebook.com/Siddik9980")
            }
            // Official Facebook Page (অফিসিয়াল ফেসবুক পেইজ / পেজ / পেইল / facebook page / fb page)
            lower.contains("facebook page") || lower.contains("fb page") || lower.contains("ফেসবুক পেজ") || lower.contains("ফেসবুক পেইজ") || lower.contains("ফেসবুকের পেজ") || lower.contains("facebook pieg") || lower.contains("পেইল") || lower.contains("পেইজ") || lower.contains("official page") -> {
                AiResponse("Opening Official Facebook Page: https://www.facebook.com/profile.php?id=61557614412925", actionType = "facebook_page", actionTarget = "https://www.facebook.com/profile.php?id=61557614412925")
            }
            // Telegram Channel (টেলিগ্রাম চ্যানেল / চেনেল / telegram channel / SRSabbir)
            lower.contains("telegram") || lower.contains("টেলিগ্রাম") || lower.contains("srsabbir") || lower.contains("টেলিগ্রাম চেনেল") || lower.contains("টেলিগ্রাম চ্যানেল") || lower.contains("tg channel") -> {
                AiResponse("Opening Official Telegram Channel: https://t.me/SRSabbir", actionType = "telegram_channel", actionTarget = "https://t.me/SRSabbir")
            }
            // YouTube Channel (ইউটিউব চ্যানেল / ইউটিউব চেনেল / youtube channel / srsabbir9980)
            lower.contains("youtube channel") || lower.contains("yt channel") || lower.contains("ইউটিউব চ্যানেল") || lower.contains("ইউটিউব চেনেল") || lower.contains("srsabbir9980") -> {
                AiResponse("Opening Official YouTube Channel: https://www.youtube.com/@SRSabbir9980", actionType = "youtube_channel", actionTarget = "https://www.youtube.com/@SRSabbir9980")
            }
            // Facebook Reels (ফেসবুক রিল চালু / ফেসবুক রিল খোলো)
            lower.contains("facebook reel") || lower.contains("fb reel") || lower.contains("ফেসবুক রিল") || lower.contains("রিল চালু") || lower.contains("রিল খোলো") -> {
                AiResponse("Opening Facebook Reels.", actionType = "facebook_reels")
            }
            // TikTok (টিক টক ভিডিও চালু / টিকটক চালাও / টিকটক খোলো)
            lower.contains("tiktok") || lower.contains("টিকটক") || lower.contains("টিক টক") -> {
                AiResponse("Opening TikTok video feed.", actionType = "tiktok")
            }

            // Flashlight / Torch
            lower.contains("flashlight") || lower.contains("torch") || lower.contains("টর্চ") || lower.contains("ফ্ল্যাশলাইট") -> {
                val state = if (lower.contains("off") || lower.contains("বন্ধ")) "off" else "on"
                AiResponse("Toggling flashlight for you.", actionType = "flashlight", actionTarget = state)
            }
            // Battery
            lower.contains("battery") || lower.contains("চার্জ") || lower.contains("ব্যাটারি") -> {
                AiResponse("Checking battery status.", actionType = "battery", actionTarget = "status")
            }
            // Device specs
            lower.contains("device info") || lower.contains("specs") || lower.contains("ফোন তথ্য") -> {
                AiResponse("Checking device specifications and memory.", actionType = "device_info")
            }
            // Volume
            lower.contains("volume up") || lower.contains("আওয়াজ বাড়াও") -> {
                AiResponse("Increasing volume.", actionType = "volume_up")
            }
            lower.contains("volume down") || lower.contains("আওয়াজ কমাও") -> {
                AiResponse("Decreasing volume.", actionType = "volume_down")
            }
            lower.contains("mute") || lower.contains("মিউট") -> {
                AiResponse("Muting media volume.", actionType = "volume_mute")
            }
            // Ringer Mode
            lower.contains("silent") || lower.contains("সাইলেন্ট") -> {
                AiResponse("Setting phone to silent.", actionType = "ringer_silent")
            }
            lower.contains("vibrate") || lower.contains("ভাইব্রেশন") -> {
                AiResponse("Setting phone to vibrate.", actionType = "ringer_vibrate")
            }
            // Screenshot
            lower.contains("screenshot") || lower.contains("স্ক্রিনশট") -> {
                AiResponse("Capturing screenshot.", actionType = "screenshot")
            }
            // Lock screen
            lower.contains("lock screen") || lower.contains("স্ক্রিন লক") -> {
                AiResponse("Locking device screen.", actionType = "lock_screen")
            }
            // Notifications & Settings
            lower.contains("notification") || lower.contains("নোটিফিকেশন") -> {
                AiResponse("Opening notifications.", actionType = "notifications")
            }
            lower.contains("quick settings") || lower.contains("কুইক সেটিংস") -> {
                AiResponse("Opening quick settings.", actionType = "quick_settings")
            }
            lower.contains("wifi") || lower.contains("ওয়াইফাই") -> {
                AiResponse("Opening Wi-Fi settings.", actionType = "wifi_settings")
            }
            lower.contains("bluetooth") || lower.contains("ব্লুটুথ") -> {
                AiResponse("Opening Bluetooth settings.", actionType = "bluetooth_settings")
            }
            // Calculator / Math
            lower.contains("calc") || lower.contains("হিসাব") || lower.matches(Regex(".*[0-9]+\\s*[+\\-*/x÷]\\s*[0-9]+.*")) -> {
                val expr = userPrompt.replace("calculate", "").replace("হিসাব", "").trim()
                AiResponse("Calculating $expr.", actionType = "math", actionTarget = expr)
            }
            // Password
            lower.contains("password") || lower.contains("পাসওয়ার্ড") -> {
                AiResponse("Generating a secure random password.", actionType = "generate_password", actionTarget = "14")
            }
            // Coin / Dice
            lower.contains("coin flip") || lower.contains("টস") -> {
                AiResponse("Flipping a coin.", actionType = "coin_flip")
            }
            lower.contains("dice") || lower.contains("লুডু") -> {
                AiResponse("Rolling a die.", actionType = "dice_roll")
            }
            // Briefing
            lower.contains("briefing") || lower.contains("আজকের দিন") || lower.contains("update") -> {
                AiResponse("Here is your daily update.", actionType = "daily_briefing")
            }
            // Calls (ক্লোলিন / কলিং / কল করো / ফোন করো)
            lower.contains("call ") || lower.contains("কল ") || lower.contains("কলিং") || lower.contains("ক্লোলিন") || lower.contains("ফোন করো") || lower.contains("কল করো") -> {
                val contact = userPrompt.substringAfter("call ")
                    .substringAfter("কল ")
                    .substringAfter("কলিং ")
                    .substringAfter("ক্লোলিন ")
                    .substringAfter("ফোন করো ")
                    .substringAfter("কল করো ")
                    .trim()
                val targetContact = contact.ifBlank { "Contact" }
                AiResponse("Calling $targetContact for you.", actionType = "call", actionTarget = targetContact)
            }
            // Open App
            lower.contains("open ") || lower.contains("খোল ") || lower.contains("চালু করো") -> {
                val app = userPrompt.substringAfter("open ")
                    .substringAfter("খোল ")
                    .substringAfter("চালু করো ")
                    .trim()
                AiResponse("Opening $app on your device.", actionType = "open_app", actionTarget = app)
            }
            // Alarms & Timers
            lower.contains("alarm") || lower.contains("অ্যালার্ম") -> {
                AiResponse("Setting your alarm now.", actionType = "alarm", actionTarget = "08:00")
            }
            lower.contains("timer") || lower.contains("টাইমার") -> {
                AiResponse("Setting a 5 minute timer.", actionType = "timer", actionTarget = "300")
            }
            // Search
            lower.contains("search ") || lower.contains("খুঁজ ") -> {
                val query = userPrompt.substringAfter("search ").substringAfter("খুঁজ ").trim()
                AiResponse("Searching Google for $query.", actionType = "search", actionTarget = query)
            }
            // YouTube & Videos (ইউটিউবে ভিডিও / গান বাজাও / ভিডিও চালু)
            lower.contains("youtube") || lower.contains("play ") || lower.contains("গান বাজাও") || lower.contains("ইউটিউবে ভিডিও") || lower.contains("ভিডিও চালাও") || lower.contains("ইউটিউব চালাও") -> {
                val song = userPrompt.replace("play", "", ignoreCase = true)
                    .replace("on youtube", "", ignoreCase = true)
                    .replace("গান বাজাও", "", ignoreCase = true)
                    .replace("ইউটিউবে ভিডিও চালু করো", "", ignoreCase = true)
                    .replace("ইউটিউবে ভিডিও চালাও", "", ignoreCase = true)
                    .replace("ইউটিউবে ভিডিও", "", ignoreCase = true)
                    .replace("ভিডিও চালাও", "", ignoreCase = true)
                    .replace("ইউটিউব চালাও", "", ignoreCase = true)
                    .trim()
                val targetQuery = song.ifBlank { "Trending music and videos" }
                AiResponse("Playing $targetQuery on YouTube.", actionType = "youtube", actionTarget = targetQuery)
            }
            // Notes
            lower.contains("note") || lower.contains("নোট") -> {
                AiResponse("Saving note for you.", actionType = "note", actionTarget = "Quick Note", actionExtra = userPrompt)
            }
            // Screen
            lower.contains("screen") || lower.contains("স্ক্রিন") -> {
                AiResponse("Analyzing what's currently on your screen.", actionType = "read_screen", actionTarget = "screen")
            }
            lower.contains("go home") || lower.contains("হোম") -> {
                AiResponse("Going to home screen.", actionType = "go_home")
            }
            lower.contains("go back") || lower.contains("ব্যাক") -> {
                AiResponse("Going back.", actionType = "go_back")
            }
            else -> {
                AiResponse(
                    "Hello! Aether AI has 100+ smart device commands and tools ready: 'Turn on flashlight', 'Battery status', 'Screenshot', 'Mute volume', 'Call Mom', 'Set 7 AM alarm', 'Roll dice', 'Generate password', or ask any question in English, বাংলা, or हिन्दी!"
                )
            }
        }
    }
}
