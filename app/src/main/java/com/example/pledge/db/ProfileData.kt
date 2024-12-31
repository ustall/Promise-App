package com.example.pledge.db
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "profile_data")
data class ProfileData(
    @PrimaryKey val id: Long = 0, // Единственная запись, можно использовать фиксированный ID
    var currentStreak: Long = 0, // Текущий стрик в днях
    var longestStreak: Long = 0, // Самый длинный стрик в днях
    var mostChallengingPromise: String = "", // Самое сложное обещание
    var mostChallengingViolations: Int = 0, // Количество нарушений для самого сложного обещания
    var totalViolations: Int = 0,
    var lifetimeViolations: Int = 0 /// Общее количество нарушений
)
