package com.example.myapplication

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
import com.google.firebase.auth.FirebaseAuth

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_note)

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val etNoteTitle = findViewById<TextInputEditText>(R.id.etNoteTitle)
        val etNoteContent = findViewById<TextInputEditText>(R.id.etNoteContent)
        val btnUpdateNote = findViewById<Button>(R.id.btnUpdateNote)

        if (intent.hasExtra("NOTE_ID")) {
            noteId = intent.getIntExtra("NOTE_ID", -1)
            etNoteTitle.setText(intent.getStringExtra("NOTE_TITLE") ?: "")
            etNoteContent.setText(intent.getStringExtra("NOTE_BODY") ?: "")
        }

        btnUpdateNote.setOnClickListener {
            val title = etNoteTitle.text.toString().trim()
            val body = etNoteContent.text.toString().trim()

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (currentUserId.isEmpty()) {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedNote = Note(
                id = noteId,
                title = title,
                body = body,
                userId = currentUserId
            )

            viewModel.updateNote(updatedNote)
            Toast.makeText(this, "Note Updated Successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}