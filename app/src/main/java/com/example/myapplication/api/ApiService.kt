package com.example.myapplication.api

import com.example.myapplication.model.AddNoteRequest
import com.example.myapplication.model.Note
import com.example.myapplication.model.NoteResponse
import com.example.myapplication.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

   @GET("users")
   suspend fun getUsers(): Response<List<User>>

   @POST("users")
   suspend fun registerUser(@Body user: User): Response<User>

   @GET("notes")
   suspend fun getUserNotes(@Query("userId") userId: String): Response<List<Note>>

   @POST("notes")
   suspend fun addNote(@Body request: AddNoteRequest): Response<Note>

   @DELETE("notes/{id}")
   suspend fun deleteNote(@Path("id") id: Int): Response<Unit>

   @PUT("notes/{id}")
   suspend fun updateNote(@Path("id") id: Int, @Body note: Note): Response<Note>
}