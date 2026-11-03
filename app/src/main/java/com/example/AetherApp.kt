package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.agent.ActionExecutor
import com.example.data.db.AetherDatabase
import com.example.data.network.AiService
import com.example.data.preferences.AppPreferences
import com.example.data.repository.ChatRepository
import com.example.voice.VoiceManager

class AetherApp : Application() {

    lateinit var database: AetherDatabase
        private set
    lateinit var chatRepository: ChatRepository
        private set
    lateinit var preferences: AppPreferences
        private set
    lateinit var aiService: AiService
        private set
    lateinit var actionExecutor: ActionExecutor
        private set
    lateinit var voiceManager: VoiceManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AetherDatabase.getDatabase(this)
        chatRepository = ChatRepository(database.chatSessionDao(), database.chatMessageDao())
        preferences = AppPreferences(this)
        aiService = AiService(preferences)
        actionExecutor = ActionExecutor(this)
        voiceManager = VoiceManager(this, preferences)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Aether Floating Assistant",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps Aether AI floating bubble and voice assistant active"
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "aether_floating_channel"
        lateinit var instance: AetherApp
            private set
    }
}
