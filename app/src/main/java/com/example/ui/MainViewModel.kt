package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AetherApp
import com.example.agent.ExecutionResult
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.service.AetherAccessibilityService
import com.example.service.FloatingAgentService
import com.example.voice.OrbVoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AetherApp

    val preferences = app.preferences
    val chatRepository = app.chatRepository
    val voiceManager = app.voiceManager
    val aiService = app.aiService
    val actionExecutor = app.actionExecutor

    // Navigation & Tab State: 0: Home, 1: Chat, 2: History, 3: Settings
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Floating service running state
    private val _isServiceActive = MutableStateFlow(FloatingAgentService.isRunning)
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    // Active Chat Session
    private val _activeSessionId = MutableStateFlow(0L)
    val activeSessionId: StateFlow<Long> = _activeSessionId.asStateFlow()

    private val _activeSessionMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeSessionMessages: StateFlow<List<ChatMessage>> = _activeSessionMessages.asStateFlow()

    val allSessions: StateFlow<List<ChatSession>> = chatRepository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Permissions & System status
    private val _hasOverlayPermission = MutableStateFlow(false)
    val hasOverlayPermission: StateFlow<Boolean> = _hasOverlayPermission.asStateFlow()

    private val _isBatteryOptimizationIgnored = MutableStateFlow(false)
    val isBatteryOptimizationIgnored: StateFlow<Boolean> = _isBatteryOptimizationIgnored.asStateFlow()

    private val _isAccessibilityEnabled = MutableStateFlow(false)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

    // Onboarding
    private val _showOnboarding = MutableStateFlow(!preferences.isOnboardingCompleted())
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    init {
        checkSystemPermissions()
        initSession()
        setupVoiceCallbacks()
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun checkSystemPermissions() {
        val context = getApplication<Application>()
        _hasOverlayPermission.value = Settings.canDrawOverlays(context)

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        _isBatteryOptimizationIgnored.value = pm.isIgnoringBatteryOptimizations(context.packageName)

        _isAccessibilityEnabled.value = AetherAccessibilityService.isServiceRunning()
        _isServiceActive.value = FloatingAgentService.isRunning
    }

    fun completeOnboarding() {
        preferences.setOnboardingCompleted(true)
        _showOnboarding.value = false
    }

    private fun initSession() {
        viewModelScope.launch {
            var currentId = preferences.getActiveSessionId()
            if (currentId == 0L) {
                currentId = chatRepository.createNewSession("Welcome to Aether")
                preferences.setActiveSessionId(currentId)
            }
            _activeSessionId.value = currentId
            observeSessionMessages(currentId)
        }
    }

    fun selectSession(sessionId: Long) {
        _activeSessionId.value = sessionId
        preferences.setActiveSessionId(sessionId)
        observeSessionMessages(sessionId)
        _selectedTab.value = 1 // Switch to Chat tab
    }

    fun createNewSession() {
        viewModelScope.launch {
            val newId = chatRepository.createNewSession("New Chat")
            selectSession(newId)
        }
    }

    fun renameSession(sessionId: Long, newTitle: String) {
        viewModelScope.launch {
            chatRepository.updateSessionTitle(sessionId, newTitle)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            if (_activeSessionId.value == sessionId) {
                initSession()
            }
        }
    }

    private fun observeSessionMessages(sessionId: Long) {
        viewModelScope.launch {
            chatRepository.getMessagesForSession(sessionId).collect { list ->
                _activeSessionMessages.value = list
            }
        }
    }

    private fun setupVoiceCallbacks() {
        voiceManager.onSpeechRecognized = { text ->
            sendMessage(text, isVoice = true)
        }
    }

    fun toggleVoiceListening() {
        if (voiceManager.voiceState.value == OrbVoiceState.LISTENING) {
            voiceManager.stopListening()
        } else {
            voiceManager.startListening()
        }
    }

    fun sendMessage(content: String, isVoice: Boolean = false) {
        if (content.isBlank()) return
        val sessionId = _activeSessionId.value

        viewModelScope.launch {
            // 1. Save user message
            val userMsg = ChatMessage(
                sessionId = sessionId,
                role = "user",
                content = content,
                timestamp = System.currentTimeMillis(),
                isVoice = isVoice
            )
            chatRepository.saveMessage(userMsg)

            _isAiThinking.value = true
            voiceManager.setThinking()

            // 2. Screen Context if asked
            var screenContext: String? = null
            if (content.lowercase().contains("screen") || content.lowercase().contains("what's on")) {
                val screenRes = actionExecutor.execute("read_screen", null, null)
                if (screenRes is ExecutionResult.ScreenText) {
                    screenContext = screenRes.text
                }
            }

            // 3. History
            val recent = chatRepository.getRecentMessages(sessionId, 6).reversed()
            val history = recent.map { it.role to it.content }

            // 4. AI Request
            val result = aiService.sendMessage(content, history, screenContext)
            _isAiThinking.value = false

            result.onSuccess { response ->
                var executedStatus: String? = null
                var executedPayload: String? = null

                if (!response.actionType.isNullOrBlank()) {
                    val exec = actionExecutor.execute(
                        response.actionType,
                        response.actionTarget,
                        response.actionExtra
                    )
                    when (exec) {
                        is ExecutionResult.Success -> {
                            executedStatus = exec.message
                            executedPayload = listOfNotNull(response.actionTarget, response.actionExtra).joinToString(" • ")
                        }
                        is ExecutionResult.Failure -> {
                            executedStatus = "Failed: ${exec.reason}"
                            executedPayload = response.actionTarget
                        }
                        is ExecutionResult.ScreenText -> {
                            executedStatus = "Screen Read Success"
                            executedPayload = exec.text.take(80) + "..."
                        }
                    }
                }

                val assistantMsg = ChatMessage(
                    sessionId = sessionId,
                    role = "assistant",
                    content = response.replyText,
                    timestamp = System.currentTimeMillis(),
                    actionType = response.actionType,
                    actionPayload = executedPayload,
                    actionStatus = executedStatus,
                    isVoice = isVoice
                )
                chatRepository.saveMessage(assistantMsg)

                if (isVoice || preferences.isVoiceReplyEnabled()) {
                    voiceManager.speak(response.replyText)
                } else {
                    voiceManager.setIdle()
                }

            }.onFailure { err ->
                val errorMsg = ChatMessage(
                    sessionId = sessionId,
                    role = "assistant",
                    content = "Error: ${err.localizedMessage ?: "Unknown error"}. Check API keys in Settings.",
                    timestamp = System.currentTimeMillis(),
                    isVoice = isVoice
                )
                chatRepository.saveMessage(errorMsg)
                voiceManager.setIdle()
            }
        }
    }

    fun startFloatingService() {
        val context = getApplication<Application>()
        if (!Settings.canDrawOverlays(context)) return

        val intent = Intent(context, FloatingAgentService::class.java).apply {
            action = FloatingAgentService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        _isServiceActive.value = true
    }

    fun stopFloatingService() {
        val context = getApplication<Application>()
        val intent = Intent(context, FloatingAgentService::class.java).apply {
            action = FloatingAgentService.ACTION_STOP
        }
        context.startService(intent)
        _isServiceActive.value = false
    }

    fun toggleFloatingService() {
        if (_isServiceActive.value) {
            stopFloatingService()
        } else {
            startFloatingService()
        }
    }

    fun launchVoiceModeFloating() {
        val context = getApplication<Application>()
        if (!Settings.canDrawOverlays(context)) return
        val intent = Intent(context, FloatingAgentService::class.java).apply {
            action = FloatingAgentService.ACTION_VOICE_MODE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        _isServiceActive.value = true
    }

    fun launchChatModeFloating() {
        val context = getApplication<Application>()
        if (!Settings.canDrawOverlays(context)) return
        val intent = Intent(context, FloatingAgentService::class.java).apply {
            action = FloatingAgentService.ACTION_EXPAND_CHAT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        _isServiceActive.value = true
    }

    fun requestIgnoreBatteryOptimization() {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val genericIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(genericIntent)
        }
    }
}
