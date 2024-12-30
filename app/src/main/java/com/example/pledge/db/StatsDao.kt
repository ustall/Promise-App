package com.example.pledge.db

import androidx.room.Dao
import androidx.room.Query

@Dao

interface StatsDao {

    @Query("SELECT COUNT(*) FROM Promise WHERE lastFailureDate = 0")
    suspend fun getCurrentStreak(): Int

    @Query("""
        SELECT MAX(
            CASE 
                WHEN lastFailureDate = 0 THEN (strftime('%s', 'now') - creationDate) / 86400
                ELSE 0 
            END
        ) 
        FROM Promise
    """)
    suspend fun getLongestStreak(): Int

    @Query("SELECT * FROM Promise ORDER BY failureCount DESC LIMIT 1")
    suspend fun getMostChallengingPromise(): Promise?

    @Query("SELECT SUM(failureCount) FROM Promise")
    suspend fun getTotalViolations(): Int

    @Query("SELECT COUNT(*) FROM Promise WHERE failureCount = 0")
    suspend fun getTotalPromises(): Int
}
