package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.database.NoteDao
import com.example.myapplication.model.AddNoteRequest
import com.example.myapplication.model.Note

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun getNotesByUserId(userId: String): LiveData<List<Note>> {
        return noteDao.getNotesByUserId(userId)
    }

    suspend fun fetchNotesFromServer(userId: String) {
        try {
            val response = RetrofitInstance.api.getUserNotes(userId)
            if (response.isSuccessful) {
                val serverNotes = response.body() ?: emptyList()
                if (serverNotes.isNotEmpty()) {
                    val roomNotes = serverNotes.map { item ->
                        Note(
                            id = item.id,
                            title = item.title,
                            body = item.body,
                            userId = userId,
                            isSynced = true
                        )
                    }
                    noteDao.insertNotes(roomNotes)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addNote(note: Note) {
        try {
            val request = AddNoteRequest(title = note.title, body = note.body, userId = note.userId)
            val response = RetrofitInstance.api.addNote(request)
            if (response.isSuccessful) {
                response.body()?.let { savedNote ->
                    noteDao.insertNote(savedNote)
                } ?: run {
                    noteDao.insertNote(note)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteNote(noteId: Int) {
        try {
            val response = RetrofitInstance.api.deleteNote(noteId)
            if (response.isSuccessful) {
                noteDao.deleteNoteById(noteId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateNote(note: Note) {
        try {
            val cleanNote = Note(
                id = note.id,
                title = note.title,
                body = note.body,
                userId = note.userId,
                isSynced = true
            )
            val response = RetrofitInstance.api.updateNote(note.id, cleanNote)
            if (response.isSuccessful) {
                noteDao.updateNote(cleanNote)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}