package com.example.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.R
import com.example.kotlin.data.Note

class NoteAdapter(

    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit

) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val noteList = ArrayList<Note>()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)

        val txtContent: TextView = itemView.findViewById(R.id.txtDescription)

        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)

        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)

        return NoteViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {

        val note = noteList[position]

        holder.txtTitle.text = note.title

        holder.txtContent.text = note.content

        holder.btnEdit.setOnClickListener {

            onEditClick(note)

        }

        holder.btnDelete.setOnClickListener {

            onDeleteClick(note)

        }

    }

    override fun getItemCount(): Int {

        return noteList.size

    }

    fun setNotes(notes: List<Note>) {

        noteList.clear()

        noteList.addAll(notes)

        notifyDataSetChanged()

    }

}