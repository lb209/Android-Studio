package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import com.example.myapplication.viewmodel.NoteViewModel
import com.example.myapplication.viewmodel.NoteViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class VoiceNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var etVoiceTitle: TextInputEditText
    private lateinit var etVoiceContent: TextInputEditText
    private lateinit var tvVoiceStatus: TextView
    private lateinit var fabMic: FloatingActionButton
    private lateinit var btnSaveVoiceNote: Button

    private val REQUEST_CODE_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_note)

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        etVoiceTitle = findViewById(R.id.etVoiceTitle)
        etVoiceContent = findViewById(R.id.etVoiceContent)
        tvVoiceStatus = findViewById(R.id.tvVoiceStatus)
        fabMic = findViewById(R.id.fabMic)
        btnSaveVoiceNote = findViewById(R.id.btnSaveVoiceNote)

        checkAudioPermission()
        initializeSpeechRecognizer()

        fabMic.setOnClickListener {
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
                speechRecognizer?.startListening(speechIntent)
            } else {
                Toast.makeText(this, "Speech recognition is not available on this device", Toast.LENGTH_LONG).show()
            }
        }

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getString("userId", "") ?: ""

        btnSaveVoiceNote.setOnClickListener {
            val title = etVoiceTitle.text.toString().trim()
            val content = etVoiceContent.text.toString().trim()

            if (content.isEmpty()) {
                Toast.makeText(this, "No voice text to save", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val finalTitle = if (title.isEmpty()) "Voice Note" else title

            val newNote = Note(
                id = 0,
                title = finalTitle,
                body = content,
                userId = currentUserId,
                isSynced = false
            )

            viewModel.addNote(newNote)

            Toast.makeText(this, "Voice note saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE_PERMISSION)
        }
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    tvVoiceStatus.text = "Listening... Speak now"
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    tvVoiceStatus.text = "Processing speech..."
                }
                override fun onError(error: Int) {
                    tvVoiceStatus.text = "Tap Mic and Speak"
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized, please try again"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer service is busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Error recognizing speech"
                    }
                    Toast.makeText(this@VoiceNoteActivity, message, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    tvVoiceStatus.text = "Tap Mic and Speak"
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val currentText = etVoiceContent.text.toString()
                        val newSpeech = matches[0]
                        if (currentText.isEmpty()) {
                            etVoiceContent.setText(newSpeech)
                        } else {
                            etVoiceContent.setText("$currentText $newSpeech")
                        }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Microphone permission is required for voice notes", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}