package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("aether_preferences", Context.MODE_PRIVATE)

    private val _providerFlow = MutableStateFlow(getProvider())
    val providerFlow: StateFlow<String> = _providerFlow.asStateFlow()

    private val _geminiKeyFlow = MutableStateFlow(getGeminiApiKey())
    val geminiKeyFlow: StateFlow<String> = _geminiKeyFlow.asStateFlow()

    private val _groqKeysFlow = MutableStateFlow(getGroqApiKeys())
    val groqKeysFlow: StateFlow<List<String>> = _groqKeysFlow.asStateFlow()

    private val _bubbleOpacityFlow = MutableStateFlow(getBubbleOpacity())
    val bubbleOpacityFlow: StateFlow<Float> = _bubbleOpacityFlow.asStateFlow()

    private val _bubbleSizeFlow = MutableStateFlow(getBubbleSize())
    val bubbleSizeFlow: StateFlow<Int> = _bubbleSizeFlow.asStateFlow()

    private val _voiceReplyFlow = MutableStateFlow(isVoiceReplyEnabled())
    val voiceReplyFlow: StateFlow<Boolean> = _voiceReplyFlow.asStateFlow()

    private val _languageFlow = MutableStateFlow(getLanguage())
    val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    private val _freeModeFlow = MutableStateFlow(isFreeMode())
    val freeModeFlow: StateFlow<Boolean> = _freeModeFlow.asStateFlow()

    fun getProvider(): String = prefs.getString("provider", "gemini") ?: "gemini"
    fun setProvider(provider: String) {
        prefs.edit().putString("provider", provider).apply()
        _providerFlow.value = provider
    }

    fun getGeminiApiKey(): String = prefs.getString("gemini_api_key", "") ?: ""
    fun setGeminiApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
        _geminiKeyFlow.value = key.trim()
    }

    fun getGroqApiKeys(): List<String> {
        val raw = prefs.getString("groq_api_keys", "") ?: ""
        return raw.split(",", "\n", ";").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun setGroqApiKeys(keys: List<String>) {
        val joined = keys.joinToString(",")
        prefs.edit().putString("groq_api_keys", joined).apply()
        _groqKeysFlow.value = keys
    }

    fun getActiveGroqKeyIndex(): Int = prefs.getInt("groq_key_index", 0)

    fun rotateGroqKey(): String? {
        val keys = getGroqApiKeys()
        if (keys.isEmpty()) return null
        val nextIndex = (getActiveGroqKeyIndex() + 1) % keys.size
        prefs.edit().putInt("groq_key_index", nextIndex).apply()
        return keys[nextIndex]
    }

    fun getCurrentGroqKey(): String? {
        val keys = getGroqApiKeys()
        if (keys.isEmpty()) return null
        val idx = getActiveGroqKeyIndex().coerceIn(0, keys.size - 1)
        return keys[idx]
    }

    fun getSelectedModel(): String {
        return if (getProvider() == "groq") {
            prefs.getString("selected_groq_model", "llama-3.3-70b-versatile") ?: "llama-3.3-70b-versatile"
        } else {
            prefs.getString("selected_gemini_model", "gemini-2.0-flash") ?: "gemini-2.0-flash"
        }
    }

    fun setSelectedModel(model: String) {
        if (getProvider() == "groq") {
            prefs.edit().putString("selected_groq_model", model).apply()
        } else {
            prefs.edit().putString("selected_gemini_model", model).apply()
        }
    }

    fun getLanguage(): String = prefs.getString("app_language", "en") ?: "en"
    fun setLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        _languageFlow.value = lang
    }

    fun isVoiceReplyEnabled(): Boolean = prefs.getBoolean("voice_reply_enabled", true)
    fun setVoiceReplyEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("voice_reply_enabled", enabled).apply()
        _voiceReplyFlow.value = enabled
    }

    fun getVoiceSpeed(): Float = prefs.getFloat("voice_speed", 1.0f)
    fun setVoiceSpeed(speed: Float) = prefs.edit().putFloat("voice_speed", speed).apply()

    fun getVoicePitch(): Float = prefs.getFloat("voice_pitch", 1.0f)
    fun setVoicePitch(pitch: Float) = prefs.edit().putFloat("voice_pitch", pitch).apply()

    fun getVoiceName(): String? = prefs.getString("voice_name", null)
    fun setVoiceName(name: String?) = prefs.edit().putString("voice_name", name).apply()

    fun getBubbleSize(): Int = prefs.getInt("bubble_size", 64)
    fun setBubbleSize(size: Int) {
        prefs.edit().putInt("bubble_size", size).apply()
        _bubbleSizeFlow.value = size
    }

    fun getBubbleOpacity(): Float = prefs.getFloat("bubble_opacity", 0.95f)
    fun setBubbleOpacity(opacity: Float) {
        prefs.edit().putFloat("bubble_opacity", opacity).apply()
        _bubbleOpacityFlow.value = opacity
    }

    fun isAutoStartOnBoot(): Boolean = prefs.getBoolean("auto_start_boot", false)
    fun setAutoStartOnBoot(enabled: Boolean) = prefs.edit().putBoolean("auto_start_boot", enabled).apply()

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)
    fun setOnboardingCompleted(completed: Boolean) = prefs.edit().putBoolean("onboarding_completed", completed).apply()

    fun getActiveSessionId(): Long = prefs.getLong("active_session_id", 0L)
    fun setActiveSessionId(id: Long) = prefs.edit().putLong("active_session_id", id).apply()

    fun isFreeMode(): Boolean = prefs.getBoolean("free_ai_mode", true)
    fun setFreeMode(enabled: Boolean) {
        prefs.edit().putBoolean("free_ai_mode", enabled).apply()
        _freeModeFlow.value = enabled
    }
}
