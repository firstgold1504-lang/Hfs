package com.example.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MechatronicsRepository
import com.example.data.QuizResult
import com.example.data.SensorProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppScreen {
    object Dashboard : AppScreen
    data class SensorDetail(val sensorId: String, val initialTab: Int = 0) : AppScreen
    object Stats : AppScreen
}

class MainViewModel(private val repository: MechatronicsRepository) : ViewModel() {

    // Navigation and active UI selection states
    var currentScreen by mutableStateOf<AppScreen>(AppScreen.Dashboard)
        private set

    // Progress DB feeds
    val sensorProgress: StateFlow<List<SensorProgress>> = repository.allSensorProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quizResults: StateFlow<List<QuizResult>> = repository.allQuizResults
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Interactive simulation live factors
    var ldrBrightness by mutableStateOf(50f) // 0-100%
    var ultrasonicDistance by mutableStateOf(100f) // 2-200cm
    var pirMovementDetected by mutableStateOf(false)
    var pirTimerRemaining by mutableStateOf(0)
    var dhtTemperature by mutableStateOf(25f) // 0-50 C
    var dhtHumidity by mutableStateOf(60f) // 0-100%

    // Live Quiz state dictionary: sensorId -> QuizState
    data class QuizProgressState(
        val currentQuestionIndex: Int = 0,
        val selectedOptionIndex: Int? = null,
        val isAnswerSubmitted: Boolean = false,
        val score: Int = 0,
        val correctAnswersCount: Int = 0,
        val isQuizFinished: Boolean = false
    )

    var activeQuizStates = mutableStateOf<Map<String, QuizProgressState>>(emptyMap())
        private set

    fun getQuizState(sensorId: String): QuizProgressState {
        return activeQuizStates.value[sensorId] ?: QuizProgressState()
    }

    fun navigateTo(screen: AppScreen) {
        currentScreen = screen
        // Manage reset or mark progress appropriately when navigating
        if (screen is AppScreen.SensorDetail) {
            markIntroductionRead(screen.sensorId)
        }
    }

    fun selectQuizOption(sensorId: String, optionIndex: Int) {
        val states = activeQuizStates.value.toMutableMap()
        val curr = states[sensorId] ?: QuizProgressState()
        if (!curr.isAnswerSubmitted) {
            states[sensorId] = curr.copy(selectedOptionIndex = optionIndex)
            activeQuizStates.value = states
        }
    }

    fun submitQuizAnswer(sensorId: String, isCorrect: Boolean) {
        val states = activeQuizStates.value.toMutableMap()
        val curr = states[sensorId] ?: QuizProgressState()
        if (curr.selectedOptionIndex != null && !curr.isAnswerSubmitted) {
            val newCorrectCount = curr.correctAnswersCount + if (isCorrect) 1 else 0
            states[sensorId] = curr.copy(
                isAnswerSubmitted = true,
                correctAnswersCount = newCorrectCount
            )
            activeQuizStates.value = states
        }
    }

    fun nextQuizQuestion(sensorId: String, totalQuestions: Int) {
        val states = activeQuizStates.value.toMutableMap()
        val curr = states[sensorId] ?: QuizProgressState()
        val nextIndex = curr.currentQuestionIndex + 1
        
        if (nextIndex >= totalQuestions) {
            // Finish quiz & calculate final score out of 100
            val finalScore = (curr.correctAnswersCount.toFloat() / totalQuestions.toFloat() * 100).toInt()
            states[sensorId] = curr.copy(
                isQuizFinished = true,
                score = finalScore
            )
            activeQuizStates.value = states

            // Save results to Room persistence
            viewModelScope.launch {
                repository.saveQuizResult(
                    sensorId = sensorId,
                    score = finalScore,
                    totalQuestions = totalQuestions,
                    correctAnswers = curr.correctAnswersCount
                )
            }
        } else {
            states[sensorId] = curr.copy(
                currentQuestionIndex = nextIndex,
                selectedOptionIndex = null,
                isAnswerSubmitted = false
            )
            activeQuizStates.value = states
        }
    }

    fun resetQuiz(sensorId: String) {
        val states = activeQuizStates.value.toMutableMap()
        states[sensorId] = QuizProgressState()
        activeQuizStates.value = states
    }

    fun markIntroductionRead(sensorId: String) {
        viewModelScope.launch {
            repository.markIntroductionRead(sensorId)
        }
    }

    fun markSimulationCompleted(sensorId: String) {
        viewModelScope.launch {
            repository.markSimulationCompleted(sensorId)
        }
    }

    fun triggerPirMotion() {
        if (!pirMovementDetected) {
            pirMovementDetected = true
            pirTimerRemaining = 5
            viewModelScope.launch {
                while (pirTimerRemaining > 0) {
                    kotlinx.coroutines.delay(1000)
                    pirTimerRemaining -= 1
                }
                pirMovementDetected = false
            }
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            activeQuizStates.value = emptyMap()
            // Reset simulation factors
            ldrBrightness = 50f
            ultrasonicDistance = 100f
            pirMovementDetected = false
            pirTimerRemaining = 0
            dhtTemperature = 25f
            dhtHumidity = 60f
        }
    }
}

class ViewModelFactory(private val repository: MechatronicsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
