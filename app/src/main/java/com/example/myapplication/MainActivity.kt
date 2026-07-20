package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.NoteAdapter
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import com.example.myapplication.viewmodel.NoteViewModel
import com.example.myapplication.viewmodel.NoteViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var viewModel: NoteViewModel
    private var fullNotesList: List<Note> = emptyList()
    private var isSearching: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = Color.parseColor("#161622")

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val layoutHeader = findViewById<RelativeLayout>(R.id.layoutHeader)
        val layoutSearchBar = findViewById<LinearLayout>(R.id.layoutSearchBar)
        val imgSearchIcon = findViewById<ImageView>(R.id.imgSearchIcon)
        val imgCloseSearch = findViewById<ImageView>(R.id.imgCloseSearch)
        val etSearch = findViewById<EditText>(R.id.etSearch)

        val fabAddNote = findViewById<FloatingActionButton>(R.id.fabAddNote)
        val btnlayout = findViewById<LinearLayout>(R.id.btnlayout)
        val writetext = findViewById<TextView>(R.id.writetext)
        val btnVoice = findViewById<LinearLayout>(R.id.btnVoice)

        recyclerView = findViewById(R.id.recyclerViewNotes)
        adapter = NoteAdapter(viewModel)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.allNotes.observe(this) { notes ->
            if (notes != null) {
                fullNotesList = notes
                if (!isSearching && etSearch.text.toString().trim().isEmpty()) {
                    adapter.setData(notes)
                }
            }
        }

        viewModel.getNotes()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                if (query.isEmpty()) {
                    adapter.setData(fullNotesList)
                } else {
                    val filteredList = fullNotesList.filter {
                        it.title.lowercase().contains(query) || it.body.lowercase().contains(query)
                    }
                    adapter.setData(filteredList)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        imgSearchIcon.setOnClickListener {
            isSearching = true
            layoutHeader.visibility = View.INVISIBLE
            layoutSearchBar.visibility = View.VISIBLE

            val params = recyclerView.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.layoutSearchBar)
            recyclerView.layoutParams = params

            etSearch.post {
                etSearch.requestFocus()
            }
        }

        imgCloseSearch.setOnClickListener {
            isSearching = false
            etSearch.text.clear()
            layoutSearchBar.visibility = View.GONE
            layoutHeader.visibility = View.VISIBLE

            val params = recyclerView.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.layoutHeader)
            recyclerView.layoutParams = params

            adapter.setData(fullNotesList)
        }

        fabAddNote.setOnClickListener {
            if (btnlayout.visibility == View.GONE) {
                btnlayout.visibility = View.VISIBLE
                fabAddNote.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                btnlayout.visibility = View.GONE
                fabAddNote.setImageResource(android.R.drawable.ic_input_add)
            }
        }

        writetext.setOnClickListener {
            btnlayout.visibility = View.GONE
            fabAddNote.setImageResource(android.R.drawable.ic_input_add)
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        btnVoice.setOnClickListener {
            btnlayout.visibility = View.GONE
            fabAddNote.setImageResource(android.R.drawable.ic_input_add)
            startActivity(Intent(this, VoiceNoteActivity::class.java))
        }
    }
}