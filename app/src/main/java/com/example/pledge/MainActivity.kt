package com.example.pledge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.pledge.AppDatabase.Companion.MIGRATION_1_2
import com.example.pledge.db.Promise
import kotlinx.coroutines.launch
import updateProfileData


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Должно быть первым!

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "promise_database"
        ).addMigrations(MIGRATION_1_2) // Добавляем миграцию, если необходимо
            .build()

// Вызов updateProfileData после создания базы данных
        lifecycleScope.launch {
            updateProfileData(db.promiseDao(), db.profileDataDao())
        }
        // Используйте базу данных внутри корутины
//    lifecycleScope.launch {
//            val dao = db.promiseDao()
//            dao.insert(Promise(text = "Обещание 1", creationDate = System.currentTimeMillis(), lastFailureDate = System.currentTimeMillis() ))
//            val promises = dao.getAll()
//            println(promises)
//        }.start()
     }
}

