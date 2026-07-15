package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.adapter.NoteAdapter
import com.example.kotlin.data.NoteDatabase
import com.example.kotlin.data.NoteRepository
import com.example.kotlin.viewmodel.NoteViewModel
import com.example.kotlin.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var btnAdd: Button
    private lateinit var editSearch: EditText

    private lateinit var database: NoteDatabase
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAdd = findViewById(R.id.btnAdd)
        recyclerView = findViewById(R.id.recyclerViewNotes)
        editSearch = findViewById(R.id.editTextSearch)

        database = NoteDatabase.getDatabase(this)
        repository = NoteRepository(database.noteDao())

        viewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(repository)
        )[NoteViewModel::class.java]

        noteAdapter = NoteAdapter(

            { note ->

                val intent = Intent(
                    this,
                    AddNoteActivity::class.java
                )

                intent.putExtra("NOTE_ID", note.id)
                intent.putExtra("NOTE_TITLE", note.title)
                intent.putExtra("NOTE_CONTENT", note.content)
                intent.putExtra("NOTE_CREATED_AT", note.createdAt)

                startActivity(intent)

            },

            { note ->

                AlertDialog.Builder(this)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Delete") { _, _ ->

                        viewModel.deleteNote(note)

                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }

        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = noteAdapter

        viewModel.allNotes.observe(this) { notes ->

            noteAdapter.setNotes(notes)

        }

        editSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val search = s.toString().trim()

                if (search.isEmpty()) {

                    viewModel.allNotes.observe(this@MainActivity) { notes ->

                        noteAdapter.setNotes(notes)

                    }

                } else {

                    viewModel.searchNotes(search)
                        .observe(this@MainActivity) { notes ->

                            noteAdapter.setNotes(notes)

                        }

                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        btnAdd.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddNoteActivity::class.java
                )
            )

        }

    }

}