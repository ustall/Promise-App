package com.example.pledge.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PromiseDao {
    @Insert
    suspend fun insertPromise(promise: Promise)

    @Query("SELECT * FROM Promise")
    suspend fun getAll(): List<Promise>

    @Delete
    suspend fun deletePromise(promise: Promise)

    @Update
    suspend fun updatePromise(promise: Promise)
}