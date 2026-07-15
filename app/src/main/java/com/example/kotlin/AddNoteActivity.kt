package com.example.kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin.data.Note
import com.example.kotlin.data.NoteDatabase
import com.example.kotlin.data.NoteRepository
import com.example.kotlin.viewmodel.NoteViewModel
import com.example.kotlin.viewmodel.NoteViewModelFactory
import java.util.Locale

class AddNoteActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editContent: EditText
    private lateinit var btnSave: Button
    private lateinit var btnVoice: ImageButton

    private lateinit var database: NoteDatabase
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    private var noteId = 0
    private var isUpdate = false
    private var createdAt = System.currentTimeMillis()

    private val speechLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data

                val resultList =
                    data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                if (!resultList.isNullOrEmpty()) {

                    editContent.setText(resultList[0])

                }

            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTitle = findViewById(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        btnSave = findViewById(R.id.btnSave)
        btnVoice = findViewById(R.id.btnVoice)

        database = NoteDatabase.getDatabase(this)

        repository = NoteRepository(database.noteDao())

        viewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(repository)
        )[NoteViewModel::class.java]

        noteId = intent.getIntExtra("NOTE_ID", 0)

        if (noteId != 0) {

            isUpdate = true

            editTitle.setText(intent.getStringExtra("NOTE_TITLE"))

            editContent.setText(intent.getStringExtra("NOTE_CONTENT"))

            createdAt = intent.getLongExtra(
                "NOTE_CREATED_AT",
                System.currentTimeMillis()
            )

            btnSave.text = "Update"

        }

        btnVoice.setOnClickListener {

            val intent = Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak your note"
            )

            speechLauncher.launch(intent)

        }

        btnSave.setOnClickListener {

            val title = editTitle.text.toString().trim()

            val content = editContent.text.toString().trim()

            if (title.isEmpty()) {

                editTitle.error = "Enter Title"

                return@setOnClickListener

            }

            if (content.isEmpty()) {

                editContent.error = "Enter Description"

                return@setOnClickListener

            }

            if (isUpdate) {

                val note = Note(
                    id = noteId,
                    title = title,
                    content = content,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis()
                )

                viewModel.updateNote(note)

            } else {

                val note = Note(
                    title = title,
                    content = content
                )

                viewModel.insertNote(note)

            }

            finish()

        }

    }

}