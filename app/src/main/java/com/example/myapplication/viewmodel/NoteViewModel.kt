package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.AddNoteRequest
import com.example.myapplication.model.Note
import com.example.myapplication.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {


    val allNotes: LiveData<List<Note>> = repository.allNotes


    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getNotesFromApi()
                if (response.isSuccessful) {
                    response.body()?.notes?.let {
                        repository.insertNotes(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun addNote(request: AddNoteRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addNoteToApi(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(id)
        }
    }


    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotes()
        }
    }


    fun searchNotes(search: String): LiveData<List<Note>> {
        return repository.searchNotes(search)
    }
}