# Aether AI — Floating AI Voice & Multitasking Assistant (SRS Edition)

**Aether AI** is a native Android application (Kotlin + Jetpack Compose + Material 3) inspired by floating multitasking overlays and voice assistants. It floats over any Android application as a glowing animated orb bubble or an expandable chat window, executing voice and text commands directly on the user's phone while multitasking.

---

## 📱 Direct APK Download & GitHub Actions Automation (`srs.apk`)

### 1. Direct Download from AI Studio
- In Google AI Studio, click on **Settings** (⚙️ or top menu) -> **Export / Generate APK**.
- The compiled APK is saved at:
  - `.build-outputs/srs.apk`
  - `app/build/outputs/apk/debug/srs.apk`

### 2. GitHub Actions Automation (`srsabbirgemin-hub`)
The automated CI/CD pipeline is configured in `.github/workflows/build-apk.yml`.
When this repository is pushed to `srsabbirgemin-hub`:
1. **Automated Compilation:** GitHub Actions spins up an Ubuntu runner, configures JDK 17, and runs `./gradlew :app:assembleDebug`.
2. **Artifact Generation:** Produces `srs.apk` and uploads it to GitHub Actions Artifacts (`srs-apk`) for 1-click download.
3. **Automated GitHub Release:** Automatically attaches `srs.apk` to GitHub Releases on tag or manual workflow run (`Run workflow` button).

### 👨‍💻 Developer & Official Channels
- **Developer Profile:** [Siddik9980 (Facebook)](https://www.facebook.com/Siddik9980)
- **Official Facebook Page:** [SRS Sabbir Page](https://www.facebook.com/profile.php?id=61557614412925)
- **Telegram Channel:** [t.me/SRSabbir](https://t.me/SRSabbir)
- **YouTube Channel:** [@SRSabbir9980](https://www.youtube.com/@SRSabbir9980)

---

## 🌟 100+ Features & Capabilities Overview

### 1. Hardware & System Toggles (Direct On-Device Controls)
1. **Flashlight Toggle**: Instant turn on / turn off / toggle via camera torch mode.
2. **Battery Diagnostic**: Live battery percentage, charging state, battery health status.
3. **Volume Up**: Increments media volume with system UI feedback.
4. **Volume Down**: Decrements media volume.
5. **Mute Volume**: Instantly mutes all media streams.
6. **Max Volume**: Sets media stream to maximum loudness.
7. **Ringer Normal**: Restores phone to normal ringtone mode.
8. **Ringer Silent**: Places phone in complete silence.
9. **Ringer Vibrate**: Sets phone ringer to vibration.
10. **Device Specs & Diagnostics**: Total and available RAM, internal storage free space, manufacturer, model, and Android OS version.
11. **Wi-Fi Settings Shortcut**: Quick 1-tap jump to network settings.
12. **Bluetooth Settings Shortcut**: Quick jump to Bluetooth pairing.
13. **Display Settings Shortcut**: Brightness and screen timeout adjustment.
14. **Sound Settings Shortcut**: Quick audio settings dispatcher.
15. **Date & Time Settings**: System clock and timezone settings.

### 2. Accessibility System Gestures (Zero-Interruption Multitasking)
16. **Screen Screenshot**: Captures screenshot without physical hardware buttons.
17. **Screen Lock**: Locks device with one command or tap.
18. **Pull Down Notifications**: Displays the notification shade over any app.
19. **Quick Settings Shade**: Expands Android's quick toggles panel.
20. **Recent Apps Switcher**: Opens the multitasking app switcher.
21. **Split Screen Toggle**: Enters or leaves split-screen mode.
22. **System Navigation Back**: Simulates hardware Back button.
23. **System Navigation Home**: Simulates hardware Home button.
24. **"What's on my screen?"**: Reads on-screen text via Accessibility node inspection and feeds context to AI.

### 3. Smart Productivity, Math & Decision Utilities
25. **Expression Evaluator**: Instant calculation of addition, subtraction, multiplication, division, and percentages.
26. **Unit Converter (Celsius to Fahrenheit)**.
27. **Unit Converter (Fahrenheit to Celsius)**.
28. **Unit Converter (Kilometers to Miles)**.
29. **Unit Converter (Miles to Kilometers)**.
30. **Unit Converter (Kilograms to Pounds)**.
31. **Unit Converter (Pounds to Kilograms)**.
32. **High-Entropy Password Generator**: Generates 16-character secure passwords and copies to clipboard.
33. **Coin Flipper**: Generates random HEADS or TAILS.
34. **6-Sided Dice Roller**: Random 1-6 outcomes for board games and decisions.
35. **Random Number Generator**: Generates numbers within customizable ranges.
36. **Daily Briefing**: Synthesizes today's date, time, battery level, and personalized greetings.
37. **Clipboard Copy**: Direct 1-tap clipboard copying.
38. **Quick Note Dispatcher**: Saves notes to user's favorite note apps via standard share sheet.

### 4. Communications & Device Automation
39. **Call Contact by Name**: Queries `ContactsContract` and places direct phone calls.
40. **Dial Phone Number**: Formats and dials telephone numbers.
41. **Send SMS by Contact Name**: Composes and dispatches text messages.
42. **Send WhatsApp Message**: Opens WhatsApp chat directly with recipient and prefilled message.
43. **Emergency Call**: One-step emergency dialing shortcut.
44. **Set Alarms**: Sets system clock alarms with time and label.
45. **Set Timers**: Starts system countdown timers.
46. **Pomodoro Timer**: Quick 25-minute productivity timer.

### 5. Media & Navigation
47. **Launch Any Installed App**: Fuzzy matches installed applications by label.
48. **Play YouTube Music/Video**: Searches and streams requested content on YouTube.
49. **Open Spotify**: Direct shortcut to Spotify music streaming.
50. **Google Maps Navigation**: Turn-by-turn navigation via `geo:` intents.
51. **Find Nearest Coffee/Gas/Pharmacy**: Instant location query via Maps.
52. **Web Search**: Google web searches via `SearchManager`.

### 6. Bengali (বাংলা) Native Voice Commands (15+ commands)
53. "টর্চ জ্বালাও" / "ফ্ল্যাশলাইট বন্ধ করো"
54. "ব্যাটারি কত?" / "চার্জ কত শতাংশ?"
55. "স্ক্রিনশট নাও"
56. "স্ক্রিন লক করো"
57. "মাকে কল করো"
58. "ইউটিউবে গান বাজাও"
59. "আজকের দিন কেমন?"
60. "হিসাব করো ২৫ * ৪"
61. "পাসওয়ার্ড তৈরি করো"
62. "স্পটিফাই ওপেন করো"
63. "ক্যামেরা খোল"
64. "হোম স্ক্রিনে যাও"
65. "ব্যাক করো"

### 7. Floating Multitasking System & UI
66. Draggable floating orb with edge snapping.
67. Bottom "X" drop zone to close floating service.
68. Expandable floating chat window with drag handle.
69. Corner resize handle for floating window dimensions.
70. Window transparency slider (35% to 100%).
71. Floating quick tools strip (Flashlight, Battery, Screenshot, Volume, Calc).
72. Floating voice mode with live animated orb.
73. Microphone audio RMS decibel visualizer wave ripples.
74. Soft keyboard adjustResize support inside WindowManager overlay.
75. Persistent foreground notification with quick actions.
76. Auto-start on reboot receiver.
77. Battery optimization exclusion prompt.

### 8. AI Models & Speech Settings
78. Google Gemini direct REST integration.
79. Gemini 2.0 Flash support.
80. Gemini 1.5 Flash support.
81. Gemini 1.5 Pro support.
82. Groq Cloud ultra-fast inference.
83. Groq Llama 3.3 70B Versatile support.
84. Groq Mixtral 8x7B 32k support.
85. Groq Llama 3.1 8B Instant support.
86. Multi-key Groq auto-rotation on HTTP 429 rate limit.
87. Offline fallback intent parser for instant zero-key testing.
88. SpeechRecognizer with partial and final transcription.
89. TextToSpeech with voice reply toggle.
90. Speech pitch slider (0.6x to 1.5x).
91. Speech speed rate slider (0.6x to 1.8x).
92. Installed system TTS voice selector.
93. Voice sample preview button.
94. English language localization.
95. Bengali (বাংলা) localization.
96. Hindi (हिन्दी) localization.
97. Bubble size customization (48dp to 84dp).
98. Room database local conversation persistence.
99. Conversation session title renaming and search.
100. Step-by-step permission onboarding wizard.
101. Tools Hub tab with categorized 100+ prompt templates.
