package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.example.myapplication.repository.NoteRepository
import com.example.myapplication.viewmodel.NoteViewModel
import com.example.myapplication.viewmodel.NoteViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private var currentUserId: String = ""
    private lateinit var noteAdapter: NoteAdapter

    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var btnlayout: LinearLayout
    private lateinit var writetext: TextView
    private lateinit var btnVoice: LinearLayout
    private lateinit var imgSearchIcon: ImageView
    private lateinit var imgLogout: ImageView
    private lateinit var normalHeaderContent: RelativeLayout
    private lateinit var layoutSearchBar: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var imgCloseSearch: ImageView

    private var isFabMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString("userId", "") ?: ""

        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        initViews()
        setupListeners()
        setupRecyclerView()
        setupSearch()
    }

    private fun initViews() {
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        fabAddNote = findViewById(R.id.fabAddNote)
        btnlayout = findViewById(R.id.btnlayout)
        writetext = findViewById(R.id.writetext)
        btnVoice = findViewById(R.id.btnVoice)
        imgSearchIcon = findViewById(R.id.imgSearchIcon)
        imgLogout = findViewById(R.id.imgLogout)
        normalHeaderContent = findViewById(R.id.normalHeaderContent)
        layoutSearchBar = findViewById(R.id.layoutSearchBar)
        etSearch = findViewById(R.id.etSearch)
        imgCloseSearch = findViewById(R.id.imgCloseSearch)
    }

    private fun setupListeners() {
        fabAddNote.setOnClickListener {
            toggleFabMenu()
        }

        writetext.setOnClickListener {
            toggleFabMenu()
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        btnVoice.setOnClickListener {
            toggleFabMenu()
            startActivity(Intent(this, VoiceNoteActivity::class.java))
        }


        imgSearchIcon.setOnClickListener {
            normalHeaderContent.visibility = View.GONE
            layoutSearchBar.visibility = View.VISIBLE
            etSearch.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
        }


        imgCloseSearch.setOnClickListener {
            etSearch.setText("")
            layoutSearchBar.visibility = View.GONE
            normalHeaderContent.visibility = View.VISIBLE
            noteAdapter.filter("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        }

        imgLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            finish()
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                noteAdapter.filter(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            btnlayout.visibility = View.GONE
            isFabMenuOpen = false
        } else {
            btnlayout.visibility = View.VISIBLE
            isFabMenuOpen = true
        }
    }

    private fun setupRecyclerView() {
        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(viewModel)
        recyclerViewNotes.adapter = noteAdapter

        if (currentUserId.isNotEmpty()) {
            viewModel.getNotesByUserId(currentUserId).observe(this) { notes ->
                notes?.let {
                    noteAdapter.setData(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentUserId.isNotEmpty()) {
            viewModel.fetchNotesFromServer(currentUserId)
        }
    }
}