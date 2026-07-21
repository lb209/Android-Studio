package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.UpdateNoteActivity
import com.example.myapplication.model.Note
import com.example.myapplication.viewmodel.NoteViewModel

class NoteAdapter(private val viewModel: NoteViewModel) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notesList = emptyList<Note>()
    private var fullNotesList = emptyList<Note>()
    private var currentQuery: String = ""

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoteTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val tvNoteBody: TextView = itemView.findViewById(R.id.tvNoteContent)
        val imgEdit: ImageView = itemView.findViewById(R.id.btnEditNote)
        val imgDelete: ImageView = itemView.findViewById(R.id.btnDeleteNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]

        holder.tvNoteTitle.text = currentNote.title
        holder.tvNoteBody.text = currentNote.body

        holder.imgDelete.setOnClickListener {
            viewModel.deleteNote(currentNote.id)
        }

        holder.imgEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UpdateNoteActivity::class.java).apply {
                putExtra("note_id", currentNote.id)
                putExtra("note_title", currentNote.title)
                putExtra("note_body", currentNote.body)
                putExtra("user_id", currentNote.userId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun setData(newNotes: List<Note>) {
        this.fullNotesList = newNotes
        if (currentQuery.isNotEmpty()) {
            notesList = fullNotesList.filter {
                it.title.contains(currentQuery, ignoreCase = true) || it.body.contains(currentQuery, ignoreCase = true)
            }
        } else {
            notesList = newNotes
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        currentQuery = query
        notesList = if (query.isEmpty()) {
            fullNotesList
        } else {
            fullNotesList.filter {
                it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}