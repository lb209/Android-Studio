package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    fun getAllNotes(): LiveData<List<Note>> {
        return repository.getAllNotes()
    }

    fun getNotesByUserId(userId: String): LiveData<List<Note>> {
        return repository.getNotesByUserId(userId)
    }

    fun fetchNotesFromServer(userId: String) {
        viewModelScope.launch {
            repository.fetchNotesFromServer(userId)
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.addNote(note)
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }
}