package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.database.NoteDao
import com.example.myapplication.model.Note

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNotes(notes: List<Note>) {
        noteDao.insertNotes(notes)
    }

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(id: Int) {
        noteDao.deleteNote(id)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    fun searchNotes(search: String): LiveData<List<Note>> {
        return noteDao.searchNotes(search)
    }
}