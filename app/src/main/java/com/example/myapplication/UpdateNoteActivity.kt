package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import com.example.myapplication.viewmodel.NoteViewModel
import com.example.myapplication.viewmodel.NoteViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private var noteId: Int = -1
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_note)

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val etUpdateNoteTitle = findViewById<TextInputEditText>(R.id.etUpdateNoteTitle)
        val etUpdateNoteContent = findViewById<TextInputEditText>(R.id.etUpdateNoteContent)
        val btnUpdateNote = findViewById<Button>(R.id.btnUpdateNote)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString("userId", "") ?: ""

        if (intent.hasExtra("note_id")) {
            noteId = intent.getIntExtra("note_id", -1)
            etUpdateNoteTitle.setText(intent.getStringExtra("note_title") ?: "")
            etUpdateNoteContent.setText(intent.getStringExtra("note_body") ?: "")
        }

        btnUpdateNote.setOnClickListener {
            val title = etUpdateNoteTitle.text.toString().trim()
            val body = etUpdateNoteContent.text.toString().trim()

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (noteId == -1) {
                Toast.makeText(this, "Error: Invalid Note ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedNote = Note(
                id = noteId,
                title = title,
                body = body,
                userId = currentUserId,
                isSynced = true
            )

            viewModel.updateNote(updatedNote)
            Toast.makeText(this, "Note Updated Successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}