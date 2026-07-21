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

class AddNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val etNoteTitle = findViewById<TextInputEditText>(R.id.etNoteTitle)
        val etNoteContent = findViewById<TextInputEditText>(R.id.etNoteContent)
        val btnSaveNote = findViewById<Button>(R.id.btnSaveNote)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getString("userId", "") ?: ""

        btnSaveNote.setOnClickListener {
            val title = etNoteTitle.text.toString().trim()
            val body = etNoteContent.text.toString().trim()

            if (title.isEmpty() && body.isEmpty()) {
                Toast.makeText(this, "Please write something to save", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newNote = Note(
                id = 0,
                title = title,
                body = body,
                userId = currentUserId,
                isSynced = false
            )

            viewModel.addNote(newNote)

            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}