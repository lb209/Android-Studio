package com.example.kotlin.data

import androidx.lifecycle.LiveData

class NoteRepository(
    private val noteDao: NoteDao
) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Note) {

        noteDao.insertNote(note)

    }

    suspend fun updateNote(note: Note) {

        noteDao.updateNote(note)

    }

    suspend fun deleteNote(note: Note) {

        noteDao.deleteNote(note)

    }

    fun searchNotes(search: String): LiveData<List<Note>> {

        return noteDao.searchNotes(search)

    }

}