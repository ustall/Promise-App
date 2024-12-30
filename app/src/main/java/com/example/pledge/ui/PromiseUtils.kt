package com.example.pledge.ui

import android.content.Context
import com.example.pledge.R
import java.time.Duration

object PromiseUtils {
    fun formatTimeHeld(context: Context, lastFailureDate: Long): String {
        val currentTime = System.currentTimeMillis()
        val durationMillis = currentTime - lastFailureDate

        val duration = Duration.ofMillis(durationMillis)
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        return context.getString(
            R.string.keep_from,
            days,
            context.getString(R.string.days),
            hours,
            context.getString(R.string.hours),
            minutes,
            context.getString(R.string.minutes)
        )
    }

    fun formatViolationsCount(context: Context, failureCount: Int): String {
        return context.getString(R.string.violations, failureCount)
    }
}
