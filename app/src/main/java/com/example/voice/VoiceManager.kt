package com.example.voice

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.example.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class OrbVoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

class VoiceManager(
    private val context: Context,
    private val preferences: AppPreferences
) : RecognitionListener, TextToSpeech.OnInitListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var currentSpeakOnComplete: (() -> Unit)? = null

    private val _voiceState = MutableStateFlow(OrbVoiceState.IDLE)
    val voiceState: StateFlow<OrbVoiceState> = _voiceState.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices.asStateFlow()

    var onSpeechRecognized: ((String) -> Unit)? = null
    var onSpeechError: ((String) -> Unit)? = null
    var onContinuousListenRequest: (() -> Unit)? = null

    private var toneGenerator: ToneGenerator? = null

    init {
        initTts()
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 45)
        } catch (_: Exception) {}
    }

    fun playBeep(toneType: Int = ToneGenerator.TONE_PROP_BEEP, durationMs: Int = 60) {
        try {
            toneGenerator?.startTone(toneType, durationMs)
        } catch (_: Exception) {}
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            updateTtsLocale()
            refreshAvailableVoices()

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = OrbVoiceState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    if (_voiceState.value == OrbVoiceState.SPEAKING) {
                        _voiceState.value = OrbVoiceState.IDLE
                    }
                    mainHandler.post {
                        currentSpeakOnComplete?.invoke()
                        currentSpeakOnComplete = null
                    }
                }

                override fun onError(utteranceId: String?) {
                    if (_voiceState.value == OrbVoiceState.SPEAKING) {
                        _voiceState.value = OrbVoiceState.IDLE
                    }
                    mainHandler.post {
                        currentSpeakOnComplete?.invoke()
                        currentSpeakOnComplete = null
                    }
                }
            })
        }
    }

    fun updateTtsLocale() {
        val tts = textToSpeech ?: return
        val lang = preferences.getLanguage()
        val locale = when (lang) {
            "bn" -> Locale("bn", "BD")
            "hi" -> Locale("hi", "IN")
            else -> Locale.ENGLISH
        }
        tts.language = locale
        tts.setPitch(preferences.getVoicePitch())
        tts.setSpeechRate(preferences.getVoiceSpeed())

        val customVoiceName = preferences.getVoiceName()
        if (!customVoiceName.isNullOrBlank()) {
            tts.voices?.find { it.name == customVoiceName }?.let {
                tts.voice = it
            }
        }
    }

    fun refreshAvailableVoices() {
        textToSpeech?.voices?.let { list ->
            _availableVoices.value = list.toList().sortedBy { it.name }
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!preferences.isVoiceReplyEnabled() || text.isBlank() || !isTtsReady) {
            onComplete?.invoke()
            return
        }

        updateTtsLocale()
        _voiceState.value = OrbVoiceState.SPEAKING
        currentSpeakOnComplete = onComplete

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "aether_${System.currentTimeMillis()}")
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "aether_utterance")
    }

    fun previewVoice(voice: Voice) {
        textToSpeech?.let { tts ->
            tts.voice = voice
            preferences.setVoiceName(voice.name)
            val sample = when (preferences.getLanguage()) {
                "bn" -> "হ্যালো, আমি আপনার এথার এআই ভয়েস সহকারী।"
                "hi" -> "नमस्ते, मैं आपका एथर एआई वॉयस असिस्टेंट हूँ।"
                else -> "Hello, this is your Aether AI voice."
            }
            tts.speak(sample, TextToSpeech.QUEUE_FLUSH, null, "preview")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        if (_voiceState.value == OrbVoiceState.SPEAKING) {
            _voiceState.value = OrbVoiceState.IDLE
        }
        currentSpeakOnComplete = null
    }

    fun triggerHaptic(durationMs: Long = 40) {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun startListening() {
        stopSpeaking()
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onSpeechError?.invoke("Speech recognition is not available on this device.")
            return
        }

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@VoiceManager)
            }
        }

        val lang = preferences.getLanguage()
        val localeTag = when (lang) {
            "bn" -> "bn-BD"
            "hi" -> "hi-IN"
            else -> "en-US"
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        _voiceState.value = OrbVoiceState.LISTENING
        triggerHaptic(25)
        playBeep(ToneGenerator.TONE_PROP_BEEP, 50)

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _voiceState.value = OrbVoiceState.IDLE
            onSpeechError?.invoke(e.localizedMessage ?: "Failed to start listening")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        if (_voiceState.value == OrbVoiceState.LISTENING) {
            _voiceState.value = OrbVoiceState.IDLE
        }
    }

    fun setThinking() {
        _voiceState.value = OrbVoiceState.THINKING
    }

    fun setIdle() {
        _voiceState.value = OrbVoiceState.IDLE
        _audioRms.value = 0f
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
        isTtsReady = false
        currentSpeakOnComplete = null
    }

    // ------------------------------------------------------------------------
    // RecognitionListener Callbacks
    // ------------------------------------------------------------------------
    override fun onReadyForSpeech(params: Bundle?) {
        _voiceState.value = OrbVoiceState.LISTENING
    }

    override fun onBeginningOfSpeech() {
        _voiceState.value = OrbVoiceState.LISTENING
    }

    override fun onRmsChanged(rmsdB: Float) {
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
        _audioRms.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _voiceState.value = OrbVoiceState.THINKING
    }

    override fun onError(error: Int) {
        _voiceState.value = OrbVoiceState.IDLE
        _audioRms.value = 0f
        val msg = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "Waiting for speech..."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            SpeechRecognizer.ERROR_NETWORK -> "Network error."
            else -> "Speech recognition error ($error)"
        }
        onSpeechError?.invoke(msg)

        // For continuous voice mode: if no match / timeout, automatically resume listening after brief delay!
        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            mainHandler.postDelayed({
                onContinuousListenRequest?.invoke()
            }, 500)
        }
    }

    override fun onResults(results: Bundle?) {
        _voiceState.value = OrbVoiceState.THINKING
        triggerHaptic(30)
        playBeep(ToneGenerator.TONE_PROP_ACK, 70)
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull()?.trim()
        if (!text.isNullOrBlank()) {
            onSpeechRecognized?.invoke(text)
        } else {
            _voiceState.value = OrbVoiceState.IDLE
            mainHandler.postDelayed({
                onContinuousListenRequest?.invoke()
            }, 400)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
