package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.AddNoteRequest
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import com.example.myapplication.viewmodel.NoteViewModel
import com.example.myapplication.viewmodel.NoteViewModelFactory

class AddNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val etNoteTitle = findViewById<EditText>(R.id.etNoteTitle)
        val etNoteContent = findViewById<EditText>(R.id.etNoteContent)
        val btnSaveNote = findViewById<Button>(R.id.btnSaveNote)

        btnSaveNote.setOnClickListener {
            val title = etNoteTitle.text.toString().trim()
            val content = etNoteContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val newNote = Note(title = title, body = content, userId = 5)
                val apiRequest = AddNoteRequest(body = content, userId = 5)

                viewModel.insertNote(newNote)
                viewModel.addNote(apiRequest)

                Toast.makeText(this, "Note Saved Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}