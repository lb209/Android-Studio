package com.example.kotlin.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>


    @Query("SELECT * FROM notes WHERE title LIKE '%' || :search || '%' OR content LIKE '%' || :search || '%' ORDER BY id DESC")
    fun searchNotes(search: String): LiveData<List<Note>>
}