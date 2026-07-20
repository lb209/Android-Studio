package com.example.myapplication.model

data class NoteResponse(

    val notes: List<Note>,

    val total: Int,

    val skip: Int,

    val limit: Int

)