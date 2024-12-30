package com.example.pledge.db

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class Promise (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var text: String,
    var creationDate: Long,
    var lastFailureDate: Long,
    val failureCount: Int = 0
)