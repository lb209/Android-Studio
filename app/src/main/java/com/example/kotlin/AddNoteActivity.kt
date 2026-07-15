package com.example.kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin.data.Note
import com.example.kotlin.data.NoteDatabase
import com.example.kotlin.data.NoteRepository
import com.example.kotlin.viewmodel.NoteViewModel
import com.example.kotlin.viewmodel.NoteViewModelFactory

class AddNoteActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editContent: EditText
    private lateinit var btnSave: Button

    private lateinit var database: NoteDatabase
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTitle = findViewById(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        btnSave = findViewById(R.id.btnSave)

        database = NoteDatabase.getDatabase(this)

        repository = NoteRepository(database.noteDao())

        viewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(repository)
        )[NoteViewModel::class.java]

        btnSave.setOnClickListener {

            val title = editTitle.text.toString().trim()

            val content = editContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {

                val note = Note(
                    title = title,
                    content = content
                )

                viewModel.insertNote(note)

                finish()

            }

        }

    }

}