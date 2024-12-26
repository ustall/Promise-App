package com.example.pledge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.pledge.db.Promise
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Должно быть первым!

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "promise_database"
        ).build()

        // Используйте базу данных внутри корутины
//    lifecycleScope.launch {
//            val dao = db.promiseDao()
//            dao.insert(Promise(text = "Обещание 1", creationDate = System.currentTimeMillis(), lastFailureDate = System.currentTimeMillis() ))
//            val promises = dao.getAll()
//            println(promises)
//        }.start()
     }
}
