package com.example.pledge.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface ProfileDataDao {
    @Query("SELECT * FROM profile_data LIMIT 1")
    suspend fun getProfileData(): ProfileData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profileData: ProfileData)
}
