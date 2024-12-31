import com.example.pledge.db.ProfileData
import com.example.pledge.db.ProfileDataDao
import com.example.pledge.db.PromiseDao
import java.time.Duration

suspend fun updateProfileData(promiseDao: PromiseDao, profileDataDao: ProfileDataDao) {
    val promises = promiseDao.getAll()
    val currentTime = System.currentTimeMillis()

    // Вычисляем текущий стрик
    val currentStreak = if (promises.isNotEmpty()) {
        val minDurationMillis = promises.minOf { currentTime - it.lastFailureDate }
        Duration.ofMillis(minDurationMillis).toDays()
    } else {
        0
    }

    // Находим самое сложное обещание
    val mostChallenging = promises.maxByOrNull { it.failureCount }
    val newMostChallengingPromise = mostChallenging?.text ?: "None"
    val newMostChallengingViolations = mostChallenging?.failureCount ?: 0

    // Считаем общее количество нарушений в текущей базе
    val totalViolations = promises.sumOf { it.failureCount }

    // Получаем или создаем текущую запись ProfileData
    var profileData = profileDataDao.getProfileData()
    if (profileData == null) {
        profileData = ProfileData(
            currentStreak = currentStreak,
            longestStreak = currentStreak, // Первый запуск: текущий стрик становится самым длинным
            mostChallengingPromise = newMostChallengingPromise,
            mostChallengingViolations = newMostChallengingViolations,
            totalViolations = totalViolations,
            lifetimeViolations = totalViolations // Начальное значение для общего количества
        )
    } else {
        // Обновляем данные
        profileData.currentStreak = currentStreak
        if (currentStreak > profileData.longestStreak) {
            profileData.longestStreak = currentStreak
        }

        // Обновляем только если новое обещание имеет больше нарушений
        if (newMostChallengingViolations > profileData.mostChallengingViolations) {
            profileData.mostChallengingPromise = newMostChallengingPromise
            profileData.mostChallengingViolations = newMostChallengingViolations
        }

        // Обновляем totalViolations и lifetimeViolations
        profileData.totalViolations = totalViolations
        if(profileData.lifetimeViolations>=profileData.lifetimeViolations+totalViolations){
        profileData.lifetimeViolations += totalViolations}
    }

    // Сохраняем данные в базу
    profileDataDao.insertOrUpdate(profileData)
}
