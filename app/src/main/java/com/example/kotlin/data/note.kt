package com.example.kotlin.data

import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.Entity


@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)