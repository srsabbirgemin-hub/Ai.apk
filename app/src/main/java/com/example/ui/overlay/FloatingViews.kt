package com.example.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.components.ActionCard
import com.example.ui.components.GlowingOrbCanvas
import com.example.ui.theme.CardBorderGradient
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.voice.OrbVoiceState

@Composable
fun FloatingBubbleOverlay(
    state: OrbVoiceState,
    audioRms: Float,
    opacity: Float,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("floating_bubble_root"),
        contentAlignment = Alignment.Center
    ) {
        GlowingOrbCanvas(
            state = state,
            audioRms = audioRms,
            size = 64.dp,
            onClick = onTap,
            modifier = Modifier.clip(CircleShape)
        )
    }
}

@Composable
fun FloatingChatWindow(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    voiceState: OrbVoiceState,
    audioRms: Float,
    initialOpacity: Float,
    onSendMessage: (String) -> Unit,
    onStartVoice: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onResizeDelta: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    var windowOpacity by remember { mutableFloatStateOf(initialOpacity) }
    var showOpacitySlider by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, CardBorderGradient, RoundedCornerShape(20.dp))
            .testTag("floating_chat_window"),
        color = DarkNavyBackground.copy(alpha = windowOpacity),
        shadowElevation = 12.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlowingOrbCanvas(
                        state = voiceState,
                        audioRms = audioRms,
                        size = 28.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Aether AI",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isThinking) "Thinking..." else if (voiceState == OrbVoiceState.SPEAKING) "Speaking..." else "Floating Multitask",
                            fontSize = 10.sp,
                            color = ElectricCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showOpacitySlider = !showOpacitySlider },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = "Adjust Opacity",
                            tint = if (showOpacitySlider) ElectricCyan else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.size(32.dp).testTag("chat_minimize_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomInMap,
                            contentDescription = "Minimize to Bubble",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp).testTag("chat_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Floating Window",
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Opacity slider panel
            AnimatedVisibility(visible = showOpacitySlider) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard.copy(alpha = 0.85f))
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Window Opacity: ${(windowOpacity * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.width(130.dp)
                    )
                    Slider(
                        value = windowOpacity,
                        onValueChange = { windowOpacity = it },
                        valueRange = 0.35f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = NeonPurple,
                            inactiveTrackColor = GlassBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Messages List
            Box(modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 6.dp)) {
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "How can I help you right now?",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try: 'Call Mom', 'Open Spotify', 'Set alarm for 7:30', or 'What\'s on my screen?'",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            val isUser = msg.role == "user"
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) NeonPurple.copy(alpha = 0.35f) else SurfaceCard.copy(alpha = 0.7f)
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isUser) 14.dp else 2.dp,
                                        bottomEnd = if (isUser) 2.dp else 14.dp
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isUser) NeonPurple.copy(alpha = 0.5f) else GlassBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = msg.content,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )

                                        if (msg.actionType != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            ActionCard(
                                                actionType = msg.actionType,
                                                statusText = msg.actionStatus,
                                                payloadText = msg.actionPayload
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isThinking) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    GlowingOrbCanvas(state = OrbVoiceState.THINKING, size = 20.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Aether is thinking...", color = ElectricCyan, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Floating Quick Tools Strip
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val quickChips = listOf(
                    "🎬 Next Reel" to "next reel",
                    "❤️ Like Reel" to "like video",
                    "💬 Comment" to "comment on video",
                    "🔄 Auto-Scroll" to "auto scroll",
                    "📱 Shorts" to "shorts",
                    "📱 FB Reels" to "facebook reel",
                    "📱 TikTok" to "tiktok",
                    "🔦 Flashlight" to "Toggle flashlight",
                    "🔋 Battery" to "Battery status",
                    "📸 Screenshot" to "Take screenshot",
                    "🔊 Volume +" to "Volume up",
                    "🔇 Mute" to "Mute volume",
                    "🧮 Calc" to "Calculate 25 * 4",
                    "🔒 Lock" to "Lock screen",
                    "📝 Note" to "Add note: Quick thought"
                )
                items(quickChips) { (label, cmd) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceNavy)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable { onSendMessage(cmd) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = label, fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }

            // Input Bar & Resize Handle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy.copy(alpha = 0.95f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onStartVoice,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (voiceState == OrbVoiceState.LISTENING) NeonPink.copy(alpha = 0.3f) else Color.Transparent)
                        .testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = if (voiceState == OrbVoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (voiceState == OrbVoiceState.LISTENING) NeonPink else ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text("Ask or give command...", color = TextSecondary.copy(alpha = 0.7f), fontSize = 12.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .testTag("chat_text_input"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard.copy(alpha = 0.6f),
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (textInput.isNotBlank()) {
                            val msg = textInput
                            textInput = ""
                            onSendMessage(msg)
                        }
                    })
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            val msg = textInput
                            textInput = ""
                            onSendMessage(msg)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = NeonPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(40.dp).testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Drag handle to resize window in bottom corner
                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(24.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onResizeDelta(dragAmount.x.toInt(), dragAmount.y.toInt())
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOutMap,
                        contentDescription = "Resize Window Handle",
                        tint = TextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingVoiceModeOverlay(
    state: OrbVoiceState,
    audioRms: Float,
    statusText: String,
    onToggleMute: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(28.dp))
            .border(1.5.dp, CardBorderGradient, RoundedCornerShape(28.dp))
            .testTag("floating_voice_mode_root"),
        color = DarkNavyBackground.copy(alpha = 0.95f),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with minimize and close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VOICE AGENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    letterSpacing = 1.sp
                )

                Row {
                    IconButton(onClick = onMinimize, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ZoomInMap,
                            contentDescription = "Minimize",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "End Voice",
                            tint = NeonPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Big Animated Orb
            GlowingOrbCanvas(
                state = state,
                audioRms = audioRms,
                size = 100.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // State label & live transcription text
            Text(
                text = when (state) {
                    OrbVoiceState.LISTENING -> "Listening to you..."
                    OrbVoiceState.THINKING -> "Processing command..."
                    OrbVoiceState.SPEAKING -> "Speaking..."
                    OrbVoiceState.IDLE -> "Ready"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = when (state) {
                    OrbVoiceState.LISTENING -> ElectricCyan
                    OrbVoiceState.THINKING -> NeonPurple
                    OrbVoiceState.SPEAKING -> Color(0xFF10B981)
                    OrbVoiceState.IDLE -> TextPrimary
                }
            )

            if (statusText.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom control buttons: Mute, End
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleMute,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state == OrbVoiceState.LISTENING) SurfaceCard else NeonPink.copy(alpha = 0.2f),
                        contentColor = if (state == OrbVoiceState.LISTENING) ElectricCyan else NeonPink
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (state == OrbVoiceState.LISTENING) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mute or Unmute"
                    )
                }

                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = NeonPink,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "End Voice Mode"
                    )
                }
            }
        }
    }
}
