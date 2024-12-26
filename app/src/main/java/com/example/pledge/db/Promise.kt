package com.example.pledge.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Promise (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val creationDate: Long,
    val lastFailureDate: Long,
    val failureCount: Int = 0
)