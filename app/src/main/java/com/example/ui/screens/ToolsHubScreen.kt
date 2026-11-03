package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.ExecutionResult
import com.example.ui.MainViewModel
import com.example.ui.theme.CardBorderGradient
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolsHubScreen(
    viewModel: MainViewModel,
    onExecutePrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    var mathInput by remember { mutableStateOf("150 * 12 + 45") }
    var translateInput by remember { mutableStateOf("How are you today?") }
    var toolResultText by remember { mutableStateOf<String?>(null) }

    val categories = listOf("🤖 Agent Routines", "⚡ Quick Toggles", "🛠️ Smart Tools", "📜 100+ Commands")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI AGENT & TOOLS HUB",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Autonomous multi-step routines & 100+ native device controls",
                    fontSize = 11.sp,
                    color = ElectricCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedCategoryTab,
            containerColor = SurfaceNavy,
            contentColor = ElectricCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCategoryTab]),
                    color = NeonPurple
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategoryTab == index,
                    onClick = { selectedCategoryTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedCategoryTab == index) Color.White else TextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Result Banner if active
        toolResultText?.let { res ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = res, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text(
                        text = "Dismiss",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { toolResultText = null }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedCategoryTab) {
                0 -> {
                    // --------------------------------------------------------
                    // Tab 0: Autonomous Agent Routines
                    // --------------------------------------------------------
                    Text(
                        text = "AUTONOMOUS AGENT WORKFLOWS (স্বয়ংক্রিয় এজেন্ট রুটিনস)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )

                    AgentRoutineCard(
                        title = "Morning Agent Routine",
                        titleBn = "সকালের রুটিন (Morning Briefing & News)",
                        description = "Runs daily briefing, verifies battery health, and loads top news headlines.",
                        steps = listOf("Speak Daily Briefing", "Check Battery Status", "Search Top News on Google"),
                        badgeColor = Color(0xFFFBBF24),
                        buttonText = "Execute Morning Routine",
                        onExecute = {
                            viewModel.runAgentRoutine("morning")
                            toolResultText = "Executing Morning Agent Routine..."
                        }
                    )

                    AgentRoutineCard(
                        title = "Bedtime & Sleep Mode",
                        titleBn = "ঘুমের রুটিন (Bedtime Routine)",
                        description = "Schedules 7:00 AM alarm, silences phone, mutes media, turns off flashlight, and locks device.",
                        steps = listOf("Set 7:00 AM Alarm", "Set Phone to Silent", "Mute Media Volume", "Turn Off Flashlight", "Lock Screen"),
                        badgeColor = NeonPurple,
                        buttonText = "Execute Bedtime Routine",
                        onExecute = {
                            viewModel.runAgentRoutine("night")
                            toolResultText = "Executing Bedtime Routine..."
                        }
                    )

                    AgentRoutineCard(
                        title = "45-Min Deep Focus Mode",
                        titleBn = "পড়াশোনা ও ফোকাস মোড (Focus Session)",
                        description = "Starts a 45-minute focus timer, switches phone to vibrate, mutes media, and logs note.",
                        steps = listOf("Set 45m Timer", "Phone to Vibrate", "Mute Media Volume", "Save Focus Session Note"),
                        badgeColor = NeonGreen,
                        buttonText = "Start Focus Sprint",
                        onExecute = {
                            viewModel.runAgentRoutine("focus")
                            toolResultText = "Executing Focus Routine..."
                        }
                    )

                    AgentRoutineCard(
                        title = "Meeting / Silent Mode",
                        titleBn = "মিটিং মোড (Instant Silence)",
                        description = "Silences phone ringer, mutes media, and creates quick meeting note.",
                        steps = listOf("Phone to Silent", "Mute Media Volume", "Save Meeting Note"),
                        badgeColor = NeonPink,
                        buttonText = "Activate Meeting Mode",
                        onExecute = {
                            viewModel.runAgentRoutine("meeting")
                            toolResultText = "Activated Meeting Mode"
                        }
                    )

                    AgentRoutineCard(
                        title = "System Diagnostics & Health",
                        titleBn = "সিস্টেম ডায়াগনস্টিক ও অপ্টিমাইজেশন",
                        description = "Reads device specs, free RAM, battery status, and opens storage manager.",
                        steps = listOf("Check Model, RAM & Android Version", "Check Battery Level & Charging", "Open Storage Manager"),
                        badgeColor = ElectricCyan,
                        buttonText = "Run System Diagnostics",
                        onExecute = {
                            viewModel.runAgentRoutine("diagnostic")
                            toolResultText = "Running Diagnostics..."
                        }
                    )

                    AgentRoutineCard(
                        title = "Hands-Free Reels & Shorts",
                        titleBn = "অটো রিলস ও শর্টস মোড",
                        description = "Launches YouTube Shorts and starts hands-free auto-scrolling every 15 seconds.",
                        steps = listOf("Open YouTube Shorts", "Enable Auto-Scroll (15s interval)"),
                        badgeColor = Color(0xFFFF0000),
                        buttonText = "Start Auto Reels",
                        onExecute = {
                            viewModel.runAgentRoutine("social")
                            toolResultText = "Starting Auto Reels..."
                        }
                    )

                    // Developer & Creator Profile Hub
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "OFFICIAL DEVELOPER & CREATOR HUB",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonGreen
                            )
                            Text(
                                text = "অফিসিয়াল ফেসবুক আইডি, পেজ, টেলিগ্রাম ও ইউটিউব চ্যানেল",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.actionExecutor.openFacebookProfile() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("FB Profile", fontSize = 11.sp, color = Color.White)
                                }
                                Button(
                                    onClick = { viewModel.actionExecutor.openFacebookPage() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D8BF0)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("FB Page", fontSize = 11.sp, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.actionExecutor.openTelegramChannel() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Telegram", fontSize = 11.sp, color = Color.White)
                                }
                                Button(
                                    onClick = { viewModel.actionExecutor.openYouTubeChannel() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("YouTube", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // --------------------------------------------------------
                    // Tab 1: Quick Hardware & Media Toggles Grid
                    // --------------------------------------------------------
                    Text("DEVICE HARDWARE & MEDIA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ToolActionButton(
                            icon = Icons.Default.FlashlightOn,
                            label = "Flashlight",
                            accentColor = Color(0xFFFBBF24),
                            onClick = {
                                val res = viewModel.actionExecutor.toggleFlashlight()
                                if (res is ExecutionResult.Success) toolResultText = res.message
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.BatteryChargingFull,
                            label = "Battery Info",
                            accentColor = NeonGreen,
                            onClick = {
                                val res = viewModel.actionExecutor.getBatteryInfo()
                                if (res is ExecutionResult.Success) toolResultText = res.message
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.CameraAlt,
                            label = "Camera 📷",
                            accentColor = NeonPink,
                            onClick = {
                                viewModel.actionExecutor.execute("camera", null, null)
                                toolResultText = "Camera opened"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PhotoLibrary,
                            label = "Gallery 🖼️",
                            accentColor = ElectricCyan,
                            onClick = {
                                viewModel.actionExecutor.execute("gallery", null, null)
                                toolResultText = "Gallery opened"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Mic,
                            label = "Recorder 🎙️",
                            accentColor = NeonPurple,
                            onClick = {
                                viewModel.actionExecutor.execute("voice_recorder", null, null)
                                toolResultText = "Sound recorder opened"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.CalendarMonth,
                            label = "Calendar 📅",
                            accentColor = Color(0xFF38BDF8),
                            onClick = {
                                viewModel.actionExecutor.execute("calendar", "Aether Reminder", null)
                                toolResultText = "Opened Calendar"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Folder,
                            label = "Downloads 📁",
                            accentColor = Color(0xFFF59E0B),
                            onClick = {
                                viewModel.actionExecutor.execute("files", null, null)
                                toolResultText = "Opened Downloads / Files"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Alarm,
                            label = "Clock / Alarm",
                            accentColor = Color(0xFFE879F9),
                            onClick = {
                                viewModel.actionExecutor.execute("clock", null, null)
                                toolResultText = "Opened Clock & Alarms"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.VolumeUp,
                            label = "Volume Up",
                            accentColor = ElectricCyan,
                            onClick = {
                                viewModel.actionExecutor.execute("volume_up", null, null)
                                toolResultText = "Media volume increased"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.VolumeDown,
                            label = "Volume Down",
                            accentColor = ElectricCyan,
                            onClick = {
                                viewModel.actionExecutor.execute("volume_down", null, null)
                                toolResultText = "Media volume decreased"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.VolumeMute,
                            label = "Mute Volume",
                            accentColor = NeonPink,
                            onClick = {
                                viewModel.actionExecutor.execute("volume_mute", null, null)
                                toolResultText = "Media volume muted"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Settings,
                            label = "Device Specs",
                            accentColor = NeonPurple,
                            onClick = {
                                val res = viewModel.actionExecutor.getDeviceInfo()
                                if (res is ExecutionResult.Success) toolResultText = res.message
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("REELS, SHORTS & TIKTOK GESTURES (হ্যান্ডস-ফ্রি স্ক্রোল ও কমেন্ট)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPink)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Next Reel ⬇️",
                            accentColor = ElectricCyan,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("next_reel", null, null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Prev Reel ⬆️",
                            accentColor = ElectricCyan,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("prev_reel", null, null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Like Reel ❤️",
                            accentColor = NeonPink,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("like_reel", null, null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Comment 💬",
                            accentColor = NeonGreen,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("comment_reel", "Great video! 🔥", null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Auto Scroll 🔄",
                            accentColor = NeonPurple,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("auto_scroll", "15", null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Stop Scroll ⏹️",
                            accentColor = TextSecondary,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("stop_auto_scroll", null, null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Stopped"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "YT Shorts",
                            accentColor = NeonPink,
                            onClick = {
                                viewModel.actionExecutor.execute("youtube_shorts", null, null)
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "FB Reels",
                            accentColor = Color(0xFF1877F2),
                            onClick = {
                                viewModel.actionExecutor.execute("facebook_reels", null, null)
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "TikTok",
                            accentColor = Color(0xFF00F2FE),
                            onClick = {
                                viewModel.actionExecutor.execute("tiktok", null, null)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("SYSTEM GESTURES & SHORTCUTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ToolActionButton(
                            icon = Icons.Default.PhotoCamera,
                            label = "Screenshot",
                            accentColor = NeonPink,
                            onClick = {
                                val res = viewModel.actionExecutor.execute("screenshot", null, null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility Service required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Lock,
                            label = "Lock Device",
                            accentColor = Color(0xFFF43F5E),
                            onClick = {
                                val res = viewModel.actionExecutor.execute("lock_screen", null, null)
                                toolResultText = if (res is ExecutionResult.Success) res.message else "Accessibility Service required"
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Notifications,
                            label = "Notifications",
                            accentColor = NeonPurple,
                            onClick = {
                                viewModel.actionExecutor.execute("notifications", null, null)
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Wifi,
                            label = "Wi-Fi Settings",
                            accentColor = ElectricCyan,
                            onClick = {
                                viewModel.actionExecutor.execute("wifi_settings", null, null)
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.NearMe,
                            label = "Location / GPS",
                            accentColor = NeonGreen,
                            onClick = {
                                viewModel.actionExecutor.execute("location_settings", null, null)
                            }
                        )

                        ToolActionButton(
                            icon = Icons.Default.Settings,
                            label = "Storage Settings",
                            accentColor = Color(0xFFFBBF24),
                            onClick = {
                                viewModel.actionExecutor.execute("storage_settings", null, null)
                            }
                        )
                    }
                }

                2 -> {
                    // --------------------------------------------------------
                    // Tab 2: Smart Productivity & Web Tools
                    // --------------------------------------------------------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = ElectricCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Math & Expression Evaluator", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = mathInput,
                                    onValueChange = { mathInput = it },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = ElectricCyan,
                                        unfocusedBorderColor = GlassBorder
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val res = viewModel.actionExecutor.execute("math", mathInput, null)
                                        if (res is ExecutionResult.Success) toolResultText = res.message
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                                ) {
                                    Text("Calc")
                                }
                            }
                        }
                    }

                    // Quick Translation Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeonGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Quick Language Translation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = translateInput,
                                onValueChange = { translateInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonGreen,
                                    unfocusedBorderColor = GlassBorder
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.actionExecutor.execute("translate", translateInput, "bn")
                                        toolResultText = "Translating to Bengali (বাংলা)..."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("To বাংলা", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        viewModel.actionExecutor.execute("translate", translateInput, "en")
                                        toolResultText = "Translating to English..."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("To English", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Nearby Search on Maps
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nearby Places Finder (Google Maps)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Restaurants", "Hospitals", "Gas Stations", "ATMs", "Pharmacies").forEach { place ->
                                    Button(
                                        onClick = {
                                            viewModel.actionExecutor.execute("nearby", place.lowercase(), null)
                                            toolResultText = "Searching nearby $place..."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(place, fontSize = 11.sp, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // Network Speed Test
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Network Speed Test", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Check real-time upload and download speeds on Fast.com", fontSize = 11.sp, color = TextSecondary)
                            }
                            Button(
                                onClick = {
                                    viewModel.actionExecutor.execute("speed_test", null, null)
                                    toolResultText = "Launching speed test on Fast.com..."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test")
                            }
                        }
                    }

                    // Clipboard Tools
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFFFBBF24))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Smart Clipboard Manager", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val res = viewModel.actionExecutor.execute("read_clipboard", null, null)
                                        if (res is ExecutionResult.Success) toolResultText = res.message
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Read Clipboard", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        val res = viewModel.actionExecutor.execute("clear_clipboard", null, null)
                                        if (res is ExecutionResult.Success) toolResultText = res.message
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF43F5E)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Clear Clipboard", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Secure Password Generator
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Password, contentDescription = null, tint = NeonPink)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Secure Password Generator", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Generates high-entropy passwords and copies to clipboard.", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val res = viewModel.actionExecutor.execute("generate_password", "16", null)
                                    if (res is ExecutionResult.Success) {
                                        toolResultText = res.message
                                        Toast.makeText(context, "Password copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Generate 16-Char Strong Password", color = TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }

                    // Decision Randomizers
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = NeonGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Decision Maker & Randomizers", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val res = viewModel.actionExecutor.execute("coin_flip", null, null)
                                        if (res is ExecutionResult.Success) toolResultText = res.message
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Coin Flip", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val res = viewModel.actionExecutor.execute("dice_roll", null, null)
                                        if (res is ExecutionResult.Success) toolResultText = res.message
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Roll Die (1-6)", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // --------------------------------------------------------
                    // Tab 3: 100+ Commands Catalog
                    // --------------------------------------------------------
                    val commandCategories = listOf(
                        "Autonomous Agent Workflows (এজেন্ট রুটিন)" to listOf(
                            "সকালের রুটিন চালাও",
                            "ঘুমের রুটিন চালু করো",
                            "ফোকাস মোড অন করো",
                            "মিটিং মোড চালু করো",
                            "সিস্টেম ডায়াগনস্টিক চেক করো",
                            "অটো রিলস চালু করো",
                            "Run morning routine",
                            "Run night routine",
                            "Start focus mode",
                            "System health check"
                        ),
                        "Bengali বাংলা Commands" to listOf(
                            "টর্চ জ্বালাও",
                            "ব্যাটারি কত?",
                            "ক্যামেরা খোল",
                            "গ্যালারি দেখাও",
                            "ভয়েস রেকর্ড করো",
                            "স্ক্রিনশট নাও",
                            "মাকে কল করো",
                            "ইউটিউবে গান বাজাও",
                            "আজকের দিন কেমন?",
                            "হিসাব করো ২৫ * ৪",
                            "পাসওয়ার্ড তৈরি করো",
                            "স্পটিফাই ওপেন করো",
                            "অনুবাদ করো How are you",
                            "কাছের রেস্টুরেন্ট খোঁজো",
                            "নেট স্পিড টেস্ট করো"
                        ),
                        "Phone & Communications" to listOf(
                            "Call Mom",
                            "Call Alex mobile",
                            "Send SMS to Sarah: I will be late",
                            "Send WhatsApp to John: Hey!",
                            "Send email to test@gmail.com",
                            "Open calendar and add reminder",
                            "Open Downloads"
                        ),
                        "Device & Hardware Control" to listOf(
                            "Turn on flashlight",
                            "Turn off flashlight",
                            "Battery percentage",
                            "Volume up",
                            "Volume down",
                            "Mute sound",
                            "Set phone to silent",
                            "Set phone to vibrate",
                            "Take screenshot",
                            "Lock phone",
                            "Pull down notifications",
                            "Open quick settings",
                            "Open Wi-Fi settings",
                            "Open location settings",
                            "Open storage settings"
                        ),
                        "Apps & Media" to listOf(
                            "Open Camera",
                            "Open Gallery",
                            "Open Voice Recorder",
                            "Open YouTube",
                            "Play Bohemian Rhapsody",
                            "Open Spotify",
                            "Open WhatsApp",
                            "Open Chrome",
                            "Open Calculator",
                            "Open Maps"
                        ),
                        "Alarms & Daily Productivity" to listOf(
                            "Set alarm for 7:30 AM",
                            "Set a 15 minute timer",
                            "Daily briefing",
                            "Add note: Buy groceries",
                            "What's on my screen?",
                            "Read clipboard",
                            "Clear clipboard",
                            "Go to home screen",
                            "Go back"
                        ),
                        "Calculators & Conversion" to listOf(
                            "Calculate 450 * 1.15",
                            "Convert 100 Celsius to Fahrenheit",
                            "Convert 5 miles to km",
                            "Convert 80 kg to lbs",
                            "Flip a coin",
                            "Roll a 6-sided dice",
                            "Generate 16-character password"
                        ),
                        "Navigation & Searches" to listOf(
                            "Search Google for latest tech news",
                            "Navigate to nearest coffee shop",
                            "Find nearby hospitals",
                            "Find nearby gas stations",
                            "Test internet speed",
                            "Translate Hello to Bengali"
                        )
                    )

                    for ((catTitle, cmds) in commandCategories) {
                        Column {
                            Text(catTitle.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (cmd in cmds) {
                                    SuggestionChip(
                                        onClick = { onExecutePrompt(cmd) },
                                        label = { Text(cmd, fontSize = 11.sp, color = TextPrimary) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = SurfaceCard
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgentRoutineCard(
    title: String,
    titleBn: String,
    description: String,
    steps: List<String>,
    badgeColor: Color,
    buttonText: String = "Execute Routine",
    onExecute: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = titleBn, fontSize = 12.sp, color = badgeColor)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "AUTONOMOUS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = badgeColor)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                steps.forEachIndexed { idx, step ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(badgeColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${idx + 1}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = step, fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onExecute,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor.copy(alpha = 0.25f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = badgeColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = buttonText, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ToolActionButton(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1
            )
        }
    }
}
