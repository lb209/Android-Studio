package com.example.kotlin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin.data.Note
import com.example.kotlin.data.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun insertNote(note: Note) {

        viewModelScope.launch {

            repository.insertNote(note)

        }

    }

    fun updateNote(note: Note) {

        viewModelScope.launch {

            repository.updateNote(note)

        }

    }

    fun deleteNote(note: Note) {

        viewModelScope.launch {

            repository.deleteNote(note)

        }

    }

}

class NoteViewModelFactory(
    private val repository: NoteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {

            return NoteViewModel(repository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel Class")

    }

}