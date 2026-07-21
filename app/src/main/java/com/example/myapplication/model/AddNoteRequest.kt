package com.example.myapplication.model

data class AddNoteRequest(
    val title: String,
    val body: String,
    val userId: String
)