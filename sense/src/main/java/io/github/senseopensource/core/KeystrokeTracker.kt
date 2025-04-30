package io.github.senseopensource.core

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import androidx.annotation.RequiresApi
import java.util.Locale

internal class KeystrokeTracker(private val editText: EditText, private val context: Context) {
    private val timestamps: MutableList<Long> = mutableListOf()
    private val keyCounts: MutableMap<String, Int> = mutableMapOf()
    private var sessionStart = 0L
    private val keystrokeTrackers = mutableListOf<KeystrokeTracker>()
    var info: MutableMap<String, String> = mutableMapOf()
    init {
        trackKeystrokes()
        trackers[editText.id] = this
    }

    private fun trackKeystrokes() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentTime = System.currentTimeMillis()

                if (s.isNullOrEmpty()) return

                if (timestamps.isEmpty()) {
                    sessionStart = currentTime
                }

                timestamps.add(currentTime)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnKeyListener { _, keyCode, event ->
            @RequiresApi(Build.VERSION_CODES.N)
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DEL -> {
                        // Backspace
                        keyCounts["backspace"] = keyCounts.getOrDefault("backspace", 0) + 1
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        // Enter key
                        keyCounts["enter"] = keyCounts.getOrDefault("enter", 0) + 1
                    }
                    KeyEvent.KEYCODE_TAB -> {
                        // Tab key
                        keyCounts["tab"] = keyCounts.getOrDefault("tab", 0) + 1
                    }
                    KeyEvent.KEYCODE_ESCAPE -> {
                        // Escape key
                        keyCounts["escape"] = keyCounts.getOrDefault("escape", 0) + 1
                    }
                }
            }
            false
        }
    }

    fun getKeystrokeData(): Map<String, Any>? {
        if (timestamps.isEmpty()) return null

        val avgTransitionTime = if (timestamps.size > 1)
            (timestamps.last() - timestamps.first()) / timestamps.size.toDouble()
        else 0.0
        return mapOf(
            "id" to editText.id,
            "keyStroke" to mapOf(
                "avgTransitionTime" to avgTransitionTime,
                "typingSpeed" to timestamps.size / ((System.currentTimeMillis() - sessionStart) / 1000.0),
                "keystrokeRhythms" to timestamps,
                "sessionStart" to sessionStart,
                "sessionEnd" to System.currentTimeMillis(),
                "keyCounts" to keyCounts,
                "info" to getKeyboardInfo(context)
            )
        )
    }
    private fun getKeyboardInfo(context: Context): MutableMap<String, String> {
        return mutableMapOf(
            "language" to Locale.getDefault().toString(),
            "layout" to getKeyBoardLayout(Locale.getDefault().toString())
        )
    }

    private fun getKeyBoardLayout(lang: String): String{
        return when {
            lang == "en_US" ->  "QWERTY"
            lang == "en_IN" ->  "QWERTY"
            lang == "en_GB" ->  "QWERTY (UK)"
            lang == "fr" ->  "AZERTY"
            lang == "fr_FR" -> "AZERTY"
            lang == "fr_BE" ->  "AZERTY (Belgium)"
            lang == "de" ->  "QWERTZ"
            lang == "de_CH" ->  "QWERTZ (Swiss)"
            lang == "de_AT" ->  "QWERTZ (Austrian)"
            lang == "en_DV" ->  "DVORAK"
            lang == "en_CM" ->  "COLEMAK"
            lang == "ja_JP" ->  "JIS (Japanese)"
            lang == "ja_Kana" ->  "Kana Input (Japanese)"
            lang == "ko_KR" ->  "Hangul (Korean 2-set)"
            lang == "ko_Hanja" ->  "Hanja (Korean 3-set)"
            lang == "ru_RU" ->  "JCUKEN (Cyrillic)"
            lang == "bg" ->  "Bulgarian Phonetic"
            lang == "sr_Cyrl" ->  "Serbian Cyrillic"
            lang == "sr_Latn" ->  "Serbian Latin"
            lang == "uk_UA" ->  "Ukrainian Keyboard"
            lang == "ar_SA" ->  "Arabic (101 Layout)"
            lang == "ar" ->  "Arabic (102 Layout)"
            lang == "fa_IR" ->  "Persian Keyboard"
            lang == "ur_PK" ->  "Urdu Keyboard"
            lang == "he_IL" ->  "Hebrew Keyboard"
            lang == "el_GR" ->  "Greek Keyboard"
            lang == "th_TH" ->  "Thai Keyboard"
            lang == "tr_TR" ->  "Turkish Q Layout"
            lang == "tr_F" ->  "Turkish F Layout"
            lang == "cs_CZ" ->  "Czech Keyboard"
            lang == "hu_HU" ->  "Hungarian Keyboard"
            lang == "pl_PL" ->  "Polish Keyboard"
            lang == "ro_RO" ->  "Romanian Keyboard"
            lang == "pt_BR" ->  "Portuguese (Brazilian ABNT2)"
            lang == "pt_PT" ->  "Portuguese (Portugal)"
            lang == "es_419" ->  "Spanish (Latin America)"
            lang == "es_ES" ->  "Spanish (Spain QWERTY)"
            lang == "it_IT" ->  "Italian Keyboard"
            lang == "vi_VN" ->  "Vietnamese Keyboard (Telex, VNI, VIQR)"
            lang == "en_PROG" ->  "Programmer's Keyboard"
            lang == "en_GAME" ->  "Gaming Keyboard"
            lang == "num" ->  "Numeric Keypad (Numpad)"
            lang == "one_hand" ->  "One-Handed Keyboards"
            lang == "brl" ->  "Braille Keyboard"
            else ->  "Unknown Layout"
        }
    }

    companion object {
        private val trackers: MutableMap<Int, KeystrokeTracker> = mutableMapOf()
        fun initKeyStrokeBehaviour(context: Context, vararg editTexts: EditText) {
            for (editText in editTexts) {
                KeystrokeTracker(editText, context)
            }
        }
        fun getKeyStrokes(): MutableList<Any> {
            val keyStrokeData = mutableListOf<Any>()
            if (trackers.values.isNotEmpty()) {
                trackers.values.forEach { tracker ->
                    tracker.getKeystrokeData()?.let { keyStrokeData.add(it) }
                }
            }
            return keyStrokeData
        }
    }
}
