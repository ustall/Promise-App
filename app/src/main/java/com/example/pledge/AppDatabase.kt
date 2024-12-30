package com.example.pledge

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pledge.db.Promise
import com.example.pledge.db.PromiseDao
import com.example.pledge.db.StatsDao

@Database(entities = [Promise::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promiseDao(): PromiseDao
    abstract fun statsDao(): StatsDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "promise_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "promise_database"
                )
                    .fallbackToDestructiveMigration() // Убедитесь, что данные миграции обрабатываются
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
