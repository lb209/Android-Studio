package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.database.NoteDao
import com.example.myapplication.model.AddNoteRequest
import com.example.myapplication.model.Note

class NoteRepository(
    private val noteDao: NoteDao
) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun getNotesFromApi() =
        RetrofitInstance.api.getNotes()

    suspend fun addNoteToApi(request: AddNoteRequest) =
        RetrofitInstance.api.addNote(request)

    suspend fun insertNotes(notes: List<Note>) =
        noteDao.insertNotes(notes)

    suspend fun insertNote(note: Note) =
        noteDao.insertNote(note)

    suspend fun deleteNote(id: Int) =
        noteDao.deleteNote(id)

    suspend fun deleteAllNotes() =
        noteDao.deleteAllNotes()

    fun searchNotes(search: String): LiveData<List<Note>> =
        noteDao.searchNotes(search)

}