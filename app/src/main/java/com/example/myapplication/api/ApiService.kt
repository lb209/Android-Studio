package com.example.myapplication.api

import com.example.myapplication.model.AddNoteRequest
import com.example.myapplication.model.Note
import com.example.myapplication.model.NoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("notes")
    suspend fun getNotes(): Response<NoteResponse>

    @POST("notes/add")
    suspend fun addNote(
        @Body note: AddNoteRequest
    ): Response<Note>

}