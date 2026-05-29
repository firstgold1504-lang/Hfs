package com.example.data

import kotlinx.coroutines.flow.Flow

class MechatronicsRepository(private val sensorDao: SensorDao) {

    val allSensorProgress: Flow<List<SensorProgress>> = sensorDao.getAllSensorProgress()
    val allQuizResults: Flow<List<QuizResult>> = sensorDao.getAllQuizResults()

    suspend fun getSensorProgress(sensorId: String): SensorProgress? {
        return sensorDao.getSensorProgress(sensorId)
    }

    suspend fun getQuizResult(sensorId: String): QuizResult? {
        return sensorDao.getQuizResult(sensorId)
    }

    suspend fun markIntroductionRead(sensorId: String) {
        val current = sensorDao.getSensorProgress(sensorId) ?: SensorProgress(sensorId = sensorId)
        sensorDao.insertSensorProgress(
            current.copy(
                introductionRead = true,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun markSimulationCompleted(sensorId: String) {
        val current = sensorDao.getSensorProgress(sensorId) ?: SensorProgress(sensorId = sensorId)
        sensorDao.insertSensorProgress(
            current.copy(
                simulationCompleted = true,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveQuizResult(sensorId: String, score: Int, totalQuestions: Int, correctAnswers: Int) {
        val current = sensorDao.getQuizResult(sensorId)
        val attempts = (current?.attemptCount ?: 0) + 1
        // Keep the high score or the latest score, let's keep the latest score and track metrics
        sensorDao.insertQuizResult(
            QuizResult(
                sensorId = sensorId,
                score = score,
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                timestamp = System.currentTimeMillis(),
                attemptCount = attempts
            )
        )
    }

    suspend fun resetAllProgress() {
        sensorDao.clearSensorProgress()
        sensorDao.clearQuizResults()
    }
}
