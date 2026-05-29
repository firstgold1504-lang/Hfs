package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_progress")
data class SensorProgress(
    @PrimaryKey val sensorId: String,
    val introductionRead: Boolean = false,
    val simulationCompleted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_result")
data class QuizResult(
    @PrimaryKey val sensorId: String,
    val score: Int, // e.g., 100
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val attemptCount: Int = 1
)
