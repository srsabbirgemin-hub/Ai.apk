package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.tts.Voice
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AetherAccessibilityService
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = viewModel.preferences

    val provider by prefs.providerFlow.collectAsState()
    val geminiKey by prefs.geminiKeyFlow.collectAsState()
    val groqKeys by prefs.groqKeysFlow.collectAsState()
    val language by prefs.languageFlow.collectAsState()
    val voiceReply by prefs.voiceReplyFlow.collectAsState()
    val bubbleSize by prefs.bubbleSizeFlow.collectAsState()
    val bubbleOpacity by prefs.bubbleOpacityFlow.collectAsState()

    val availableVoices by viewModel.voiceManager.availableVoices.collectAsState()
    val hasOverlay by viewModel.hasOverlayPermission.collectAsState()
    val isBatteryOptimizedIgnored by viewModel.isBatteryOptimizationIgnored.collectAsState()
    val isAccessibilityActive by viewModel.isAccessibilityEnabled.collectAsState()

    var geminiInput by remember(geminiKey) { mutableStateOf(geminiKey) }
    var showGeminiKey by remember { mutableStateOf(false) }

    var groqInput by remember(groqKeys) { mutableStateOf(groqKeys.joinToString("\n")) }
    var showGroqKeys by remember { mutableStateOf(false) }

    var voicePitch by remember { mutableFloatStateOf(prefs.getVoicePitch()) }
    var voiceSpeed by remember { mutableFloatStateOf(prefs.getVoiceSpeed()) }

    var autoStartBoot by remember { mutableStateOf(prefs.isAutoStartOnBoot()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "AI provider, voice, bubble overlay, and device actions",
            fontSize = 12.sp,
            color = ElectricCyan
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // Free AI Mode Banner & 1-Click Free Setup
        // --------------------------------------------------------------------
        val isFreeMode by prefs.freeModeFlow.collectAsState()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isFreeMode) NeonGreen else Color(0xFF64748B))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Free AI Mode (১০০% ফ্রি মোড)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFreeMode) NeonGreen else TextPrimary
                        )
                    }

                    Switch(
                        checked = isFreeMode,
                        onCheckedChange = { prefs.setFreeMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "কোনো API Key ছাড়াই আনলিমিটেড ব্যবহার করুন। ফোন কল, ফ্ল্যাশলাইট, রিল স্ক্রোল, টাইমার, ক্যালকুলেটর ও চ্যাট সম্পূর্ণ ফ্রিতে চলবে।",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ফ্রি অফিসিয়াল API Key পান (১০০% ফ্রি - কোনো ক্রেডিট কার্ড লাগে না):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElectricCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🎁 Free Gemini Key", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.groq.com/keys")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚡ Free Groq Key", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // AI Provider Section
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.SmartToy, title = "AI Provider & Model")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Provider Selector (Gemini vs Groq)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = provider == "gemini",
                        onClick = { prefs.setProvider("gemini") },
                        label = { Text("Google Gemini", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = provider == "groq",
                        onClick = { prefs.setProvider("groq") },
                        label = { Text("Groq Cloud", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (provider == "gemini") {
                    // Gemini Models
                    Text("Select Model", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    val currentModel = prefs.getSelectedModel()
                    val geminiModels = listOf("gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (m in geminiModels) {
                            FilterChip(
                                selected = currentModel == m,
                                onClick = { prefs.setSelectedModel(m) },
                                label = { Text(m.replace("gemini-", ""), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gemini API Key
                    Text("Gemini API Key", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = geminiInput,
                        onValueChange = { geminiInput = it },
                        placeholder = { Text("Paste your Gemini API key", color = TextSecondary, fontSize = 12.sp) },
                        visualTransformation = if (showGeminiKey) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showGeminiKey = !showGeminiKey }) {
                                Icon(
                                    imageVector = if (showGeminiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("gemini_key_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = GlassBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                prefs.setGeminiApiKey(geminiInput)
                                Toast.makeText(context, "Gemini API Key saved!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save Key", fontSize = 12.sp)
                        }
                    }
                } else {
                    // Groq Models
                    Text("Select Model", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    val currentModel = prefs.getSelectedModel()
                    val groqModels = listOf("llama-3.3-70b-versatile", "mixtral-8x7b-32768", "llama-3.1-8b-instant")

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (m in groqModels) {
                            FilterChip(
                                selected = currentModel == m,
                                onClick = { prefs.setSelectedModel(m) },
                                label = { Text(m, fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Groq Multiple API Keys with auto rotation
                    Text(
                        text = "Groq API Keys (Support multi-key auto-rotation on 429 rate limit)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter one key per line or comma separated. Active keys: ${groqKeys.size}",
                        fontSize = 10.sp,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = groqInput,
                        onValueChange = { groqInput = it },
                        placeholder = { Text("key_1\nkey_2\nkey_3...", color = TextSecondary, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("groq_keys_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = GlassBorder
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                val keys = groqInput.split("\n", ",").map { it.trim() }.filter { it.isNotEmpty() }
                                prefs.setGroqApiKeys(keys)
                                Toast.makeText(context, "${keys.size} Groq keys saved!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save Groq Keys", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // Language Section
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.Language, title = "Language")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Assistant Language & Voice", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val languages = listOf("en" to "English", "bn" to "বাংলা (Bengali)", "hi" to "हिन्दी (Hindi)")
                    for ((code, label) in languages) {
                        FilterChip(
                            selected = language == code,
                            onClick = {
                                prefs.setLanguage(code)
                                viewModel.voiceManager.updateTtsLocale()
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // Voice Settings Section
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.RecordVoiceOver, title = "Voice Settings")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Spoken Voice Replies", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("AI speaks back responses out loud", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = voiceReply,
                        onCheckedChange = { prefs.setVoiceReplyEnabled(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonPurple)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pitch Slider
                Text("Voice Pitch: ${String.format("%.1fx", voicePitch)}", fontSize = 12.sp, color = TextSecondary)
                Slider(
                    value = voicePitch,
                    onValueChange = {
                        voicePitch = it
                        prefs.setVoicePitch(it)
                        viewModel.voiceManager.updateTtsLocale()
                    },
                    valueRange = 0.6f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Speed Slider
                Text("Speech Rate: ${String.format("%.1fx", voiceSpeed)}", fontSize = 12.sp, color = TextSecondary)
                Slider(
                    value = voiceSpeed,
                    onValueChange = {
                        voiceSpeed = it
                        prefs.setVoiceSpeed(it)
                        viewModel.voiceManager.updateTtsLocale()
                    },
                    valueRange = 0.6f..1.8f,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = NeonPurple)
                )

                // Voice Picker & Test Preview Button
                if (availableVoices.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Installed TTS Voices (${availableVoices.size})", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val currentVoice = availableVoices.firstOrNull()
                                if (currentVoice != null) {
                                    viewModel.voiceManager.previewVoice(currentVoice)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceNavy),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Preview Spoken Voice Sample", fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // Floating Bubble Customization
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.Tune, title = "Floating Overlay Customization")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bubble Size: ${bubbleSize}dp", fontSize = 12.sp, color = TextSecondary)
                Slider(
                    value = bubbleSize.toFloat(),
                    onValueChange = { prefs.setBubbleSize(it.toInt()) },
                    valueRange = 48f..84f,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Bubble & Window Opacity: ${(bubbleOpacity * 100).toInt()}%", fontSize = 12.sp, color = TextSecondary)
                Slider(
                    value = bubbleOpacity,
                    onValueChange = { prefs.setBubbleOpacity(it) },
                    valueRange = 0.35f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-start after reboot", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Launches floating assistant on device startup", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = autoStartBoot,
                        onCheckedChange = {
                            autoStartBoot = it
                            prefs.setAutoStartOnBoot(it)
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonPurple)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------------------------
        // System Permissions & Accessibility
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.Accessibility, title = "System Integration & Accessibility")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Overlay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Overlay Permission", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(if (hasOverlay) "Granted" else "Missing - Tap to enable", fontSize = 11.sp, color = if (hasOverlay) NeonGreen else NeonPink)
                    }
                    if (!hasOverlay) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enable", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Battery Optimization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Battery Optimization", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(if (isBatteryOptimizedIgnored) "Unrestricted (Service won't be killed)" else "Optimized (May be killed by OS)", fontSize = 11.sp, color = if (isBatteryOptimizedIgnored) NeonGreen else NeonPink)
                    }
                    if (!isBatteryOptimizedIgnored) {
                        OutlinedButton(
                            onClick = { viewModel.requestIgnoreBatteryOptimization() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Disable", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Accessibility Service
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Accessibility Service (Optional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(
                                if (isAccessibilityActive) "Active (Can read screen & perform Back/Home)" else "Off by default",
                                fontSize = 11.sp,
                                color = if (isAccessibilityActive) NeonGreen else TextSecondary
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isAccessibilityActive) "Manage" else "Enable", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Why use this? When enabled, you can ask Aether 'What's on my screen?' and it will analyze on-screen text to help you, or execute 'Go back' and 'Go home'. Completely private, runs on your request only.",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --------------------------------------------------------------------
        // 1. Owner & Developer Profile (ওনার ও ডেভেলপার আইডি)
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.Person, title = "Owner & Developer Profile (ওনার ও ডেভেলপার আইডি)")

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1877F2)),
            modifier = Modifier.fillMaxWidth().testTag("owner_developer_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF1877F2), ElectricCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Siddik (SRSabbir)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ElectricCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("OWNER / DEVELOPER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                            }
                        }
                        Text(
                            text = "অ্যাপের মূল ওনার ও ডেভেলপার ব্যক্তিগত ফেসবুক আইডি",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "fb.com/Siddik9980",
                            fontSize = 11.sp,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.actionExecutor.openFacebookProfile("https://www.facebook.com/Siddik9980") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(42.dp)
                            .testTag("open_owner_facebook_id_btn")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ওনার আইডি দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Owner Facebook ID", "https://www.facebook.com/Siddik9980")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ওনার ফেসবুক আইডি লিঙ্ক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("copy_owner_facebook_id_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = ElectricCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি লিঙ্ক", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --------------------------------------------------------------------
        // 2. Official Facebook Page (অফিসিয়াল ফেসবুক পেইজ)
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.Public, title = "Official Facebook Page (অফিসিয়াল ফেসবুক পেইজ)")

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0D8AF0)),
            modifier = Modifier.fillMaxWidth().testTag("official_facebook_page_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF0D8AF0), NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📄", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Aether AI Official Page",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0D8AF0).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PAGE / পেইজ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D8AF0))
                            }
                        }
                        Text(
                            text = "অফিসিয়াল পেজ - নিয়মিত আপডেট, খবর ও সাপোর্ট পেতে ফলো করুন",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "facebook.com/profile.php?id=61557614412925",
                            fontSize = 11.sp,
                            color = Color(0xFF0D8AF0),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.actionExecutor.openFacebookPage("https://www.facebook.com/profile.php?id=61557614412925") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D8AF0)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(42.dp)
                            .testTag("open_official_facebook_page_btn")
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("অফিসিয়াল পেইজ দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Official Facebook Page", "https://www.facebook.com/profile.php?id=61557614412925")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ফেসবুক পেইজ লিঙ্ক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D8AF0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D8AF0)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("copy_official_facebook_page_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = Color(0xFF0D8AF0), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি লিঙ্ক", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --------------------------------------------------------------------
        // 3. Official Channels (অফিসিয়াল চ্যানেলসমূহ - টেলিগ্রাম ও ইউটিউব)
        // --------------------------------------------------------------------
        SettingsSectionHeader(icon = Icons.Default.Campaign, title = "Official Channels (অফিসিয়াল চ্যানেলসমূহ)")

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorderGradient),
            modifier = Modifier.fillMaxWidth().testTag("official_channels_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "সব নতুন ফিচার, নোটিশ ও ভিডিও টিউটোরিয়াল পেতে অফিসিয়াল চ্যানেলগুলোতে যুক্ত থাকুন:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Telegram Channel
                ChannelItemRow(
                    badgeText = "TG",
                    badgeColor = Color(0xFF229ED9),
                    categoryTag = "📢 TELEGRAM CHANNEL",
                    title = "Official Telegram Channel",
                    handle = "t.me/SRSabbir",
                    url = "https://t.me/SRSabbir",
                    buttonLabel = "টেলিগ্রাম চ্যানেল যুক্ত হোন",
                    onOpen = { viewModel.actionExecutor.openTelegramChannel("https://t.me/SRSabbir") },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Telegram Channel", "https://t.me/SRSabbir")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "টেলিগ্রাম চ্যানেল লিঙ্ক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // YouTube Channel
                ChannelItemRow(
                    badgeText = "YT",
                    badgeColor = Color(0xFFFF0000),
                    categoryTag = "▶️ YOUTUBE CHANNEL",
                    title = "Official YouTube Channel",
                    handle = "@SRSabbir9980",
                    url = "https://www.youtube.com/@SRSabbir9980",
                    buttonLabel = "ইউটিউব চ্যানেল সাবস্ক্রাইব করুন",
                    onOpen = { viewModel.actionExecutor.openYouTubeChannel("https://www.youtube.com/@SRSabbir9980") },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("YouTube Channel", "https://www.youtube.com/@SRSabbir9980")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "ইউটিউব চ্যানেল লিঙ্ক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ChannelItemRow(
    badgeText: String,
    badgeColor: Color,
    title: String,
    handle: String,
    url: String,
    categoryTag: String = "",
    buttonLabel: String = "Open",
    onOpen: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (categoryTag.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = categoryTag,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = handle,
                        fontSize = 11.sp,
                        color = ElectricCyan,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                ) {
                    Text(buttonLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = TextPrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("কপি লিঙ্ক", fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ElectricCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = ElectricCyan
        )
    }
}
