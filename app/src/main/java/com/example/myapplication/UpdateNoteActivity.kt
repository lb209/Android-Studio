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

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private var noteId: Int = -1
    private var userId: Int = 5

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

        noteId = intent.getIntExtra("note_id", -1)
        userId = intent.getIntExtra("user_id", 5)
        val oldTitle = intent.getStringExtra("note_title")
        val oldBody = intent.getStringExtra("note_body")

        etUpdateNoteTitle.setText(oldTitle)
        etUpdateNoteContent.setText(oldBody)

        btnUpdateNote.setOnClickListener {
            val updatedTitle = etUpdateNoteTitle.text.toString().trim()
            val updatedContent = etUpdateNoteContent.text.toString().trim()

            if (updatedTitle.isNotEmpty() && updatedContent.isNotEmpty()) {
                val updatedNote = Note(id = noteId, title = updatedTitle, body = updatedContent, userId = userId)

                viewModel.insertNote(updatedNote)

                Toast.makeText(this, "Note Updated Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Title and Content cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}