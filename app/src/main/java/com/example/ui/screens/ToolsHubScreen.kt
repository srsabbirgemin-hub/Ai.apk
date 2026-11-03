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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.testTag
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
    var toolResultText by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Quick Toggles", "Smart Tools", "100+ Commands")

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
                    text = "AI TOOLS & ACTIONS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextPrimary
                )
                Text(
                    text = "100+ hardware controls, gestures & utilities",
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
                            fontSize = 12.sp,
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
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
                    // Quick Hardware Toggles Grid
                    // --------------------------------------------------------
                    Text("DEVICE HARDWARE & VOLUME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)

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
                    }
                }

                1 -> {
                    // --------------------------------------------------------
                    // Smart Productivity Tools
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

                2 -> {
                    // --------------------------------------------------------
                    // 100+ Commands Catalog
                    // --------------------------------------------------------
                    val commandCategories = listOf(
                        "Bengali বাংলা Commands" to listOf(
                            "টর্চ জ্বালাও",
                            "ব্যাটারি কত?",
                            "স্ক্রিনশট নাও",
                            "মাকে কল করো",
                            "ইউটিউবে গান বাজাও",
                            "আজকের দিন কেমন?",
                            "হিসাব করো ২৫ * ৪",
                            "পাসওয়ার্ড তৈরি করো",
                            "স্পটিফাই ওপেন করো",
                            "ক্যামেরা খোল"
                        ),
                        "Phone & Communications" to listOf(
                            "Call Mom",
                            "Call Alex mobile",
                            "Send SMS to Sarah: I will be late",
                            "Send WhatsApp to John: Hey!",
                            "Dial 911"
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
                            "Open quick settings"
                        ),
                        "Apps & Media" to listOf(
                            "Open YouTube",
                            "Play Bohemian Rhapsody",
                            "Open Spotify",
                            "Open Camera",
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
                            "Navigate to Central Park",
                            "Navigate to nearest pharmacy",
                            "Search Google for AI news",
                            "Search web for weather"
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
