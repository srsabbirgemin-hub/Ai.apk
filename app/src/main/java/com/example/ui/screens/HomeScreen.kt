package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlowingOrbCanvas
import com.example.ui.theme.CardBorderGradient
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.voice.OrbVoiceState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val voiceState by viewModel.voiceManager.voiceState.collectAsState()
    val audioRms by viewModel.voiceManager.audioRms.collectAsState()
    val hasOverlay by viewModel.hasOverlayPermission.collectAsState()
    val isBatteryOptimizedIgnored by viewModel.isBatteryOptimizationIgnored.collectAsState()

    val quickActions = listOf(
        "🤖 সকালের রুটিন" to "সকালের রুটিন চালাও",
        "🌙 ঘুমের রুটিন" to "ঘুমের রুটিন চালু করো",
        "🎯 ফোকাস মোড" to "ফোকাস মোড চালু করো",
        "🔍 সিস্টেম ডায়াগনস্টিক" to "সিস্টেম ডায়াগনস্টিক চেক করো",
        "📷 ক্যামেরা" to "open camera",
        "🎙️ ভয়েস রেকর্ড" to "open voice recorder",
        "👑 ওনার ফেসবুক" to "Open developer Facebook ID",
        "📄 অফিসিয়াল পেইজ" to "Open Facebook page",
        "✈️ টেলিগ্রাম চ্যানেল" to "Open Telegram channel",
        "▶️ ইউটিউব চ্যানেল" to "Open YouTube channel",
        "Open YouTube" to "Open YouTube",
        "Call Mom" to "Call Mom",
        "Set Alarm 7 AM" to "Set an alarm for 7:00 AM",
        "Open Spotify" to "Open Spotify",
        "What's on screen?" to "What's on my screen?",
        "5 min Timer" to "Set a timer for 5 minutes"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AETHER AI",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Floating Voice & Multitask Agent",
                    fontSize = 12.sp,
                    color = ElectricCyan
                )
            }

            // Quick Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isServiceActive) NeonGreen.copy(alpha = 0.15f) else Color(0x3364748B))
                    .border(1.dp, if (isServiceActive) NeonGreen.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isServiceActive) NeonGreen else Color(0xFF64748B))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isServiceActive) "FLOATING ON" else "STANDBY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isServiceActive) NeonGreen else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Big Glowing Orb
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .clickable { viewModel.toggleVoiceListening() },
            contentAlignment = Alignment.Center
        ) {
            GlowingOrbCanvas(
                state = voiceState,
                audioRms = audioRms,
                size = 180.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (voiceState) {
                OrbVoiceState.LISTENING -> "Listening to your voice..."
                OrbVoiceState.THINKING -> "Aether is thinking..."
                OrbVoiceState.SPEAKING -> "Speaking response..."
                OrbVoiceState.IDLE -> "Tap orb to speak command"
            },
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = when (voiceState) {
                OrbVoiceState.LISTENING -> ElectricCyan
                OrbVoiceState.THINKING -> NeonPurple
                OrbVoiceState.SPEAKING -> NeonGreen
                OrbVoiceState.IDLE -> TextSecondary
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Service Control Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("service_control_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Floating Bubble Service",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isServiceActive) "Bubble overlay visible over all apps" else "Turn on to float over any app",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = isServiceActive,
                        onCheckedChange = { active ->
                            if (active) {
                                if (hasOverlay) {
                                    viewModel.startFloatingService()
                                } else {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                            } else {
                                viewModel.stopFloatingService()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonPurple,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceCard
                        ),
                        modifier = Modifier.testTag("floating_service_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Launch shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (!hasOverlay) {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                                context.startActivity(intent)
                            } else {
                                viewModel.launchVoiceModeFloating()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("launch_voice_floating_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Voice Mode", fontSize = 12.sp, color = TextPrimary)
                    }

                    Button(
                        onClick = {
                            if (!hasOverlay) {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                                context.startActivity(intent)
                            } else {
                                viewModel.launchChatModeFloating()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("launch_chat_floating_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat Window", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Battery Optimization Warning Card (if not yet ignored)
        if (!isBatteryOptimizedIgnored) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("battery_optimization_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1528)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Disable Battery Optimization",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Prevents Android from killing the floating voice bubble when backgrounded.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.requestIgnoreBatteryOptimization() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                    ) {
                        Text("Disable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick Action Chips Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TRY QUICK COMMANDS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ElectricCyan
            )
            Text(
                text = "Tap to run",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for ((label, prompt) in quickActions) {
                SuggestionChip(
                    onClick = {
                        viewModel.sendMessage(prompt)
                        onNavigateToChat()
                    },
                    label = { Text(label, fontSize = 12.sp, color = TextPrimary) },
                    shape = RoundedCornerShape(20.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = SurfaceCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // 1. Owner & Developer ID Card (ওনার ও ডেভেলপার আইডি)
        // --------------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_owner_developer_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF1877F2))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1877F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ওনার ও ডেভেলপার আইডি",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ElectricCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("OWNER / DEVELOPER", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                            }
                        }
                        Text(
                            text = "Siddik (SRSabbir) • fb.com/Siddik9980",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.actionExecutor.openFacebookProfile("https://www.facebook.com/Siddik9980") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("home_open_owner_facebook_btn")
                ) {
                    Text("👑 ওনার আইডি দেখুন (Facebook Profile)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --------------------------------------------------------------------
        // 2. Official Facebook Page Card (অফিসিয়াল ফেসবুক পেইজ)
        // --------------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_facebook_page_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF0D8AF0))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0D8AF0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📄", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "অফিসিয়াল ফেসবুক পেইজ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0D8AF0).copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("PAGE / পেইজ", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D8AF0))
                            }
                        }
                        Text(
                            text = "Aether AI Official Page • আপডেট ও সাপোর্ট",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.actionExecutor.openFacebookPage("https://www.facebook.com/profile.php?id=61557614412925") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D8AF0)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("home_open_facebook_page_btn")
                ) {
                    Text("📄 অফিসিয়াল পেইজ দেখুন (Facebook Page)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --------------------------------------------------------------------
        // 3. Official Channels Card (অফিসিয়াল চ্যানেলসমূহ - টেলিগ্রাম ও ইউটিউব)
        // --------------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_official_channels_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📢", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "অফিসিয়াল চ্যানেলসমূহ (CHANNELS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }
                    Text(
                        text = "1-Tap Open",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Telegram Channel
                    Button(
                        onClick = { viewModel.actionExecutor.openTelegramChannel("https://t.me/SRSabbir") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("home_open_telegram_channel_btn")
                    ) {
                        Text("✈️ Telegram চ্যানেল", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // YouTube Channel
                    Button(
                        onClick = { viewModel.actionExecutor.openYouTubeChannel("https://www.youtube.com/@SRSabbir9980") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("home_open_youtube_channel_btn")
                    ) {
                        Text("▶️ YouTube চ্যানেল", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
