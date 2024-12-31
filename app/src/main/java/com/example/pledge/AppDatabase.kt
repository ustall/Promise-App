package com.example.pledge


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pledge.db.Promise
import com.example.pledge.db.PromiseDao
import com.example.pledge.db.ProfileData
import com.example.pledge.db.ProfileDataDao


@Database(entities = [Promise::class, ProfileData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promiseDao(): PromiseDao
    abstract fun profileDataDao(): ProfileDataDao
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
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS profile_data (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "currentStreak INTEGER NOT NULL, " +
                            "longestStreak INTEGER NOT NULL, " +
                            "mostChallengingPromise TEXT, " +
                            "mostChallengingViolations INTEGER NOT NULL, " +
                            "totalViolations INTEGER NOT NULL)" +
                            "lifetimeViolations INTEGER NOT NULL)"
                )
            }
        }
    }
}
