package com.example.pledge.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.pledge.AppDatabase
import com.example.pledge.R
import com.example.pledge.databinding.PromisesStreakBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration


class PromisesStreakFragment : Fragment() {
    private lateinit var binding: PromisesStreakBinding
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
            // Инициализация биндинга
            Log.d("PromisesStreakFragment", "onCreateView - savedInstanceState: $savedInstanceState")
            binding = PromisesStreakBinding.inflate(inflater, container, false)

        // Инициализация базы данных
        db = Room.databaseBuilder(
        requireContext(),
        AppDatabase::class.java, "promise_database"
        ).build()

        Log.d("PromisesStreakFragment", "onCreateView")

        updateStreakNumber()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun updateStreakNumber() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val promises = db.promiseDao().getAll()
                if (promises.isNotEmpty()) {
                    val currentTime = System.currentTimeMillis()
                    val minDurationMillis = promises.minOf { currentTime - it.lastFailureDate }
                    val duration = Duration.ofMillis(minDurationMillis)
                    val days = duration.toDays()

                    withContext(Dispatchers.Main) {
                        binding.streakNumber.text = "$days"
                        binding.streakNumber.text = "$days"
                        binding.streakDescription.text = when {
                            days < 7 -> getString(R.string.streak_less_than_week)
                            days in 7..30 -> getString(R.string.streak_less_than_month)
                            days in 31..365 -> getString(R.string.streak_less_than_year)
                            else -> getString(R.string.streak_over_year)
                        }

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.streakNumber.text = getString(R.string.no_streak)
                        binding.streakDescription.text= getString(R.string.no_streak_description)
                    }
                }

            } catch (e: Exception) {
                Log.e("PromisesRecycleViewFragment", "Error updating streak", e)
            }
        }
    }
}