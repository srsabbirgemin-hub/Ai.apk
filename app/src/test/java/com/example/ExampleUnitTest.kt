package com.example

import com.example.data.network.AiResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAiResponseCreation() {
        val response = AiResponse(
            replyText = "Opening Spotify",
            actionType = "open_app",
            actionTarget = "Spotify",
            actionExtra = null
        )
        assertEquals("Opening Spotify", response.replyText)
        assertEquals("open_app", response.actionType)
        assertEquals("Spotify", response.actionTarget)
    }

    @Test
    fun testGroqKeyRotationLogic() {
        val keys = listOf("key_alpha", "key_beta", "key_gamma")
        var currentIndex = 0
        val rotate = {
            currentIndex = (currentIndex + 1) % keys.size
            keys[currentIndex]
        }

        assertEquals("key_beta", rotate())
        assertEquals("key_gamma", rotate())
        assertEquals("key_alpha", rotate())
    }
}
