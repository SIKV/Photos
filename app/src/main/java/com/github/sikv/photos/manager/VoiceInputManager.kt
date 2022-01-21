package com.github.sikv.photos.manager

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*

class VoiceInputManager(
    private val activity: AppCompatActivity,
    private val onResult: (String?) -> Unit
) : DefaultLifecycleObserver {

    private val key = UUID.randomUUID().toString()
    private lateinit var getText: ActivityResultLauncher<Intent>

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        getText = activity.activityResultRegistry.register(key, ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                        results[0]
                    }
                onResult(spokenText)
            } else {
                onResult(null)
            }
        }
    }

    fun startSpeechRecognizer() {
        getText.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        })
    }
}
