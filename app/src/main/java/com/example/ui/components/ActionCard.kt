package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorderGradient
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextSecondary

@Composable
fun ActionCard(
    actionType: String,
    statusText: String?,
    payloadText: String?,
    modifier: Modifier = Modifier
) {
    val (icon: ImageVector, iconColor: Color, title: String) = when (actionType.lowercase()) {
        "open_app" -> Triple(Icons.Default.Apps, ElectricCyan, "Open Application")
        "call" -> Triple(Icons.Default.Call, NeonGreen, "Phone Call")
        "sms" -> Triple(Icons.Default.Message, ElectricCyan, "Send SMS")
        "whatsapp" -> Triple(Icons.Default.Message, NeonGreen, "WhatsApp")
        "alarm" -> Triple(Icons.Default.Alarm, NeonPink, "Set Alarm")
        "timer" -> Triple(Icons.Default.Timer, ElectricCyan, "Timer")
        "note" -> Triple(Icons.Default.Note, Color(0xFFF59E0B), "Note Added")
        "search" -> Triple(Icons.Default.Search, ElectricCyan, "Web Search")
        "navigate" -> Triple(Icons.Default.Map, ElectricCyan, "Navigation")
        "youtube" -> Triple(Icons.Default.PlayArrow, NeonPink, "YouTube")
        "youtube_shorts", "shorts" -> Triple(Icons.Default.PlayArrow, NeonPink, "YouTube Shorts")
        "facebook_reels", "fb_reels" -> Triple(Icons.Default.PlayArrow, Color(0xFF1877F2), "Facebook Reels")
        "tiktok" -> Triple(Icons.Default.PlayArrow, Color(0xFF00F2FE), "TikTok Video")
        "next_reel", "next_video", "scroll_down" -> Triple(Icons.Default.PlayArrow, ElectricCyan, "Next Video / Reel")
        "prev_reel", "prev_video", "scroll_up" -> Triple(Icons.Default.PlayArrow, ElectricCyan, "Previous Video / Reel")
        "like_reel", "like_video" -> Triple(Icons.Default.CheckCircle, NeonPink, "Liked Video")
        "comment_reel", "comment_video", "comment" -> Triple(Icons.Default.Message, ElectricCyan, "Video Comment")
        "auto_scroll" -> Triple(Icons.Default.PlayArrow, NeonGreen, "Auto-Scrolling")
        "stop_auto_scroll" -> Triple(Icons.Default.CheckCircle, TextSecondary, "Stopped Auto-Scroll")
        "read_screen" -> Triple(Icons.Default.Smartphone, ElectricCyan, "Screen Analysis")
        "flashlight", "torch" -> Triple(Icons.Default.FlashlightOn, Color(0xFFFBBF24), "Flashlight")
        "battery", "battery_status" -> Triple(Icons.Default.BatteryChargingFull, NeonGreen, "Battery Check")
        "volume_up", "volume_down", "volume_mute", "volume_max" -> Triple(Icons.Default.VolumeUp, ElectricCyan, "Volume Adjustment")
        "screenshot" -> Triple(Icons.Default.PhotoCamera, NeonPink, "Screenshot")
        "lock_screen" -> Triple(Icons.Default.Lock, Color(0xFFF43F5E), "Device Lock")
        "notifications" -> Triple(Icons.Default.Notifications, ElectricCyan, "Notifications")
        "math", "calculate" -> Triple(Icons.Default.Calculate, ElectricCyan, "Math Evaluator")
        "generate_password" -> Triple(Icons.Default.Password, NeonPink, "Password Generated")
        "coin_flip", "dice_roll" -> Triple(Icons.Default.Casino, NeonGreen, "Decision Tool")
        "go_home", "go_back" -> Triple(Icons.Default.Smartphone, ElectricCyan, "System Navigation")
        else -> Triple(Icons.Default.CheckCircle, ElectricCyan, "System Action")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard.copy(alpha = 0.85f))
            .border(1.dp, CardBorderGradient, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = iconColor
                )
            }

            val isSuccess = statusText?.contains("failed", ignoreCase = true) != true
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isSuccess) NeonGreen else NeonPink,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSuccess) "Executed" else "Failed",
                    fontSize = 11.sp,
                    color = if (isSuccess) NeonGreen else NeonPink,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (!statusText.isNullOrBlank()) {
            Text(
                text = statusText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (!payloadText.isNullOrBlank()) {
            Text(
                text = payloadText,
                fontSize = 11.sp,
                color = ElectricCyan.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
