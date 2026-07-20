package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import com.example.myapplication.viewmodel.NoteViewModel
import com.example.myapplication.viewmodel.NoteViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class VoiceNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var tvVoiceStatus: TextView
    private lateinit var etVoiceContent: TextInputEditText
    private lateinit var etVoiceTitle: TextInputEditText

    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!spokenText.isNullOrEmpty()) {
                    etVoiceContent.setText(spokenText[0])
                    tvVoiceStatus.text = "Speech Recognized"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvVoiceStatus.text = "Error in recognition"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_note)

        try {
            val database = AppDatabase.getDatabase(this)
            val repository = NoteRepository(database.noteDao())
            val factory = NoteViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

            tvVoiceStatus = findViewById(R.id.tvVoiceStatus)
            etVoiceContent = findViewById(R.id.etVoiceContent)
            etVoiceTitle = findViewById(R.id.etVoiceTitle)

            val fabMic = findViewById<FloatingActionButton>(R.id.fabMic)
            val btnSaveVoiceNote = findViewById<Button>(R.id.btnSaveVoiceNote)

            fabMic.setOnClickListener {
                try {
                    if (SpeechRecognizer.isRecognitionAvailable(this)) {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                        }
                        tvVoiceStatus.text = "Listening..."
                        speechRecognizerLauncher.launch(intent)
                    } else {
                        tvVoiceStatus.text = "Mic not supported"
                        Toast.makeText(this, "Mic or Speech Recognition is not available on this device", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    tvVoiceStatus.text = "Mic Error"
                    Toast.makeText(this, "Microphone feature failed to open safely", Toast.LENGTH_SHORT).show()
                }
            }

            btnSaveVoiceNote.setOnClickListener {
                try {
                    val title = etVoiceTitle.text.toString().trim()
                    val content = etVoiceContent.text.toString().trim()

                    if (title.isNotEmpty() && content.isNotEmpty()) {
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                        if (currentUserId.isEmpty()) {
                            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val voiceNote = Note(
                            title = title,
                            body = content,
                            userId = currentUserId
                        )

                        viewModel.insertNote(voiceNote)

                        Toast.makeText(this, "Voice Note Saved Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Please write title and record content!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}