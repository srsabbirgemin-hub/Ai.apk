package com.example.data.repository

import com.example.data.db.ChatMessageDao
import com.example.data.db.ChatSessionDao
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val sessionDao: ChatSessionDao,
    private val messageDao: ChatMessageDao
) {
    val allSessions: Flow<List<ChatSession>> = sessionDao.getAllSessions()

    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForSession(sessionId)
    }

    suspend fun getRecentMessages(sessionId: Long, limit: Int = 10): List<ChatMessage> {
        return messageDao.getRecentMessages(sessionId, limit)
    }

    suspend fun createNewSession(title: String = "New Conversation"): Long {
        val session = ChatSession(
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return sessionDao.insertSession(session)
    }

    suspend fun updateSessionTitle(sessionId: Long, newTitle: String) {
        val existing = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.updateSession(existing.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteSessionById(sessionId)
    }

    suspend fun saveMessage(message: ChatMessage): Long {
        val id = messageDao.insertMessage(message)
        val session = sessionDao.getSessionById(message.sessionId)
        if (session != null) {
            // If it's a user message and session has default title, update title
            val newTitle = if (session.title == "New Conversation" && message.role == "user") {
                message.content.take(30).trim()
            } else {
                session.title
            }
            sessionDao.updateSession(session.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
        }
        return id
    }

    suspend fun updateMessage(message: ChatMessage) {
        messageDao.updateMessage(message)
    }
}
