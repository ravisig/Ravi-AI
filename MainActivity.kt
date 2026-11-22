package com.raviai.assistant

import android.app.Activity
import android.app.AlarmClock
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private lateinit var heard: TextView
    private lateinit var tts: TextToSpeech
    private var ttsReady = false
    private val voiceRequest = 4101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 48, 28, 28)
            setBackgroundColor(0xFF101D32.toInt())
        }
        val title = TextView(this).apply {
            text = "Ravi AI"
            textSize = 32f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "नमस्ते रवि जी! माइक दबाकर बोलिए।"
            textSize = 18f
            setTextColor(0xFFD6E8FF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 18)
        }
        heard = TextView(this).apply {
            text = "आपकी कमांड यहाँ दिखाई देगी"
            textSize = 16f
            setTextColor(0xFF9CCBFF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 24)
        }
        val mic = Button(this).apply {
            text = "🎙 बोलिए — Ravi AI"
            textSize = 20f
            setOnClickListener { listen() }
        }
        val hint = TextView(this).apply {
            text = "उदाहरण: WhatsApp खोलो • YouTube खोलो • समय बताओ • 9876543210 पर कॉल लगाओ • अलार्म लगाओ"
            textSize = 14f
            setTextColor(0xFFB8C9DE.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        }
        page.addView(title, match())
        page.addView(status, match())
        page.addView(heard, match())
        page.addView(mic, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64))
        page.addView(hint, match())
        setContentView(page)
    }

    private fun match() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun onInit(result: Int) {
        if (result == TextToSpeech.SUCCESS) {
            val lang = tts.setLanguage(Locale("hi", "IN"))
            ttsReady = lang != TextToSpeech.LANG_MISSING_DATA &&
                    lang != TextToSpeech.LANG_NOT_SUPPORTED
            if (!ttsReady) tts.setLanguage(Locale.US)
            // Uses the Hindi voice installed on the phone. Available voice/gender depends on Android TTS engine.
        }
    }

    private fun say(message: String) {
        status.text = message
        if (ttsReady) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "ravi-ai")
    }

    private fun listen() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ravi AI को कमांड दें")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            startActivityForResult(intent, voiceRequest)
        } catch (_: Exception) {
            say("इस फोन में वॉइस टाइपिंग सेवा उपलब्ध नहीं है।")
        }
    }

    @Deprecated("Uses the compatible Android activity-result callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == voiceRequest && resultCode == Activity.RESULT_OK) {
            val command = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!command.isNullOrBlank()) {
                heard.text = "आपने कहा: $command"
                handleCommand(command.lowercase(Locale.ROOT))
            }
        }
    }

    private fun handleCommand(command: String) {
        when {
            command.contains("व्हाट्सएप") || command.contains("whatsapp") -> openApp("com.whatsapp", "WhatsApp खोल रही हूँ।")
            command.contains("इंस्टाग्राम") || command.contains("instagram") -> openApp("com.instagram.android", "Instagram खोल रही हूँ।")
            command.contains("यूट्यूब") || command.contains("youtube") -> openApp("com.google.android.youtube", "YouTube खोल रही हूँ।")
            command.contains("क्रोम") || command.contains("chrome") || command.contains("ब्राउज़र") ->
                openApp("com.android.chrome", "Chrome खोल रही हूँ।")
            command.contains("कॉल") || command.contains("call") -> {
                val number = Regex("[0-9]{5,}").find(command)?.value
                if (number != null) {
                    say("$number पर कॉल करने के लिए डायलर खोल रही हूँ।")
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
                } else say("किस नंबर पर कॉल करना है? नंबर सहित फिर से बोलिए।")
            }
            command.contains("अलार्म") || command.contains("alarm") -> {
                val hour = Regex("""([0-2]?\d)[: ]([0-5]\d)""").find(command)
                if (hour != null) {
                    val h = hour.groupValues[1].toInt().coerceIn(0, 23)
                    val m = hour.groupValues[2].toInt()
                    val i = Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR, h)
                        .putExtra(AlarmClock.EXTRA_MINUTES, m).putExtra(AlarmClock.EXTRA_MESSAGE, "Ravi AI अलार्म")
                    say("$h बजकर $m मिनट का अलार्म सेट करने के लिए घड़ी खोल रही हूँ।")
                    startActivity(i)
                } else say("समय बताइए, जैसे 7 बजकर 30 मिनट।")
            }
            command.contains("समय") || command.contains("time") -> {
                val now = SimpleDateFormat("hh:mm a", Locale("hi", "IN")).format(Date())
                say("अभी समय है $now")
            }
            command.contains("सेटिंग") || command.contains("settings") -> {
                say("फोन की सेटिंग खोल रही हूँ।")
                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            }
            command.contains("मैसेज") || command.contains("message") || command.contains("संदेश") -> {
                say("मैसेज भेजने के लिए शेयर विकल्प खोल रही हूँ। भेजने से पहले सही व्यक्ति चुनें।")
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "नमस्ते! यह संदेश Ravi AI से तैयार किया गया है।")
                }
                startActivity(Intent.createChooser(send, "मैसेज किस ऐप से भेजें?"))
            }
            command.contains("बंद") || command.contains("stop") -> say("ठीक है, मैं सुनना बंद कर रही हूँ।")
            else -> say("यह कमांड अभी मेरे शुरुआती संस्करण में उपलब्ध नहीं है। आप WhatsApp खोलो, YouTube खोलो, समय बताओ या कॉल लगाओ कह सकते हैं।")
        }
    }

    private fun openApp(packageName: String, response: String) {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) {
            say(response)
            startActivity(launch)
        } else say("यह ऐप आपके फोन में इंस्टॉल नहीं है।")
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
