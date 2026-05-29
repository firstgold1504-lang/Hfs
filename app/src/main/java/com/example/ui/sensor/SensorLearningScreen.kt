package com.example.ui.sensor

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MechatronicsData
import com.example.data.SensorModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.SensorSimulationContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorLearningScreen(
    sensorId: String,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val sensor = MechatronicsData.sensors.find { it.id == sensorId } ?: return

    var activeTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("📖 Pengenalan", "🔌 Simulasi Wiring", "📝 Kuis Modul")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
    ) {
        // Top app bar with back navigation and material primary matching colors
        Surface(
            color = sensor.primaryColor.copy(alpha = 0.08f),
            border = BorderStroke(0.5.dp, sensor.primaryColor.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.Dashboard) },
                    modifier = Modifier.testTag("back_to_dashboard")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali ke Beranda", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = sensor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = sensor.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = sensor.primaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Subtabs for learning stages
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color(0xFF161523),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = sensor.primaryColor
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    modifier = Modifier.testTag("sensor_detail_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeTab == index) sensor.primaryColor else Color.Gray
                        )
                    }
                )
            }
        }

        // Swipeable or simple state tab views
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                0 -> TheoryTab(sensor = sensor, viewModel = viewModel)
                1 -> SimulationWiringTab(sensor = sensor, viewModel = viewModel)
                2 -> QuizTab(sensor = sensor, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TheoryTab(sensor: SensorModel, viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    
    // Automatically flag that intro is read
    LaunchedEffect(sensor.id) {
        viewModel.markIntroductionRead(sensor.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Quick definition card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161523)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(sensor.primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = sensor.primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Deskripsi Kompetensi",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = sensor.description,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Full explanation
        Text(
            text = "📖 Pengenalan Teori Lengkap",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = sensor.fullExplanation,
            fontSize = 13.sp,
            color = Color.LightGray,
            lineHeight = 19.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Working principle card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C2A)),
            border = BorderStroke(1.dp, sensor.primaryColor.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔋 Prinsip Kerja Mekatronik",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = sensor.primaryColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = sensor.workingPrinciple,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Components in catalog lists
        Text(
            text = "🛠️ Komponen Laboratorium yang Dibutuhkan",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        sensor.componentsNeeded.forEach { component ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = sensor.primaryColor,
                    modifier = Modifier.size(6.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = component,
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SimulationWiringTab(sensor: SensorModel, viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    var simVerified by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Virtual lab panel
        SensorSimulationContainer(
            sensor = sensor,
            viewModel = viewModel,
            onCompleted = {
                simVerified = true
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Interactive steps list for physical wiring
        Text(
            text = "👣 Langkah Rangkaian / Wiring Arduino",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))

        sensor.wiringSteps.forEachIndexed { index, step ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161523)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(sensor.primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = step,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizTab(sensor: SensorModel, viewModel: MainViewModel) {
    val quizState = viewModel.getQuizState(sensor.id)
    val totalQuestions = sensor.quizQuestions.size

    AnimatedContent(
        targetState = quizState.isQuizFinished,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "quiz_transition"
    ) { isFinished ->
        if (isFinished) {
            QuizScoreBoard(
                sensor = sensor,
                quizState = quizState,
                totalQuestions = totalQuestions,
                viewModel = viewModel,
                onRetry = {
                    viewModel.resetQuiz(sensor.id)
                }
            )
        } else {
            val idx = quizState.currentQuestionIndex
            val question = sensor.quizQuestions.getOrNull(idx)
            
            if (question != null) {
                QuizQuestionCard(
                    sensor = sensor,
                    question = question,
                    quizState = quizState,
                    totalQuestions = totalQuestions,
                    onOptionSelected = { optId ->
                        viewModel.selectQuizOption(sensor.id, optId)
                    },
                    onSubmit = {
                        val isCorrect = quizState.selectedOptionIndex == question.correctAnswerIndex
                        viewModel.submitQuizAnswer(sensor.id, isCorrect)
                    },
                    onNext = {
                        viewModel.nextQuizQuestion(sensor.id, totalQuestions)
                    }
                )
            } else {
                // Safeguard blank state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error loading quiz", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun QuizQuestionCard(
    sensor: SensorModel,
    question: com.example.data.QuizQuestion,
    quizState: MainViewModel.QuizProgressState,
    totalQuestions: Int,
    onOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Question tracker indicator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pertanyaan ${question.id} dari $totalQuestions",
                    color = sensor.primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "${((question.id - 1).toFloat() / totalQuestions.toFloat() * 100).toInt()}% Selesai",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (question.id - 1).toFloat() / totalQuestions.toFloat() },
                color = sensor.primaryColor,
                trackColor = Color(0xFF1E1C2A),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // The question text box
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161523)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2B283D), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = question.question,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quiz options
        itemsIndexed(question.options) { optionIdx, option ->
            val isSelected = quizState.selectedOptionIndex == optionIdx
            val isSubmitted = quizState.isAnswerSubmitted
            val isCorrectIndex = optionIdx == question.correctAnswerIndex

            // Color status based on validation outputs
            val containerColor = when {
                isSubmitted && isCorrectIndex -> Color(0xFF1B5E20) // Correct (Green)
                isSubmitted && isSelected && !isCorrectIndex -> Color(0xFFB71C1C) // Incorrect chosen (Red)
                isSelected -> sensor.primaryColor.copy(alpha = 0.25f) // Selected highlighting
                else -> Color(0xFF1E1C2A) // Idle state
            }

            val strokeColor = when {
                isSubmitted && isCorrectIndex -> Color(0xFF4CAF50)
                isSubmitted && isSelected && !isCorrectIndex -> Color(0xFFF44336)
                isSelected -> sensor.primaryColor
                else -> Color(0xFF312F40)
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(1.5.dp, strokeColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable(enabled = !isSubmitted) {
                        onOptionSelected(optionIdx)
                    }
                    .testTag("quiz_option_${question.id}_$optionIdx")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circle letter marker
                    val marker = ('A' + optionIdx).toString()
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) sensor.primaryColor else Color(0x33FFFFFF)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = marker,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = option,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Correct/Wrong small icon
                    if (isSubmitted) {
                        if (isCorrectIndex) {
                            Icon(Icons.Default.Check, contentDescription = "Benar", tint = Color.Green)
                        } else if (isSelected) {
                            Icon(Icons.Default.Close, contentDescription = "Salah", tint = Color.Red)
                        }
                    }
                }
            }
        }

        // Explanatory feedback box (Visible only after submission)
        if (quizState.isAnswerSubmitted) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)), // Deep blue tint info
                    border = BorderStroke(1.dp, Color(0xFF42A5F5).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val isUserCorrect = quizState.selectedOptionIndex == question.correctAnswerIndex
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isUserCorrect) Icons.Default.Campaign else Icons.Default.Lightbulb,
                                tint = if (isUserCorrect) Color(0xFF81C784) else Color(0xFFFBC02D),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUserCorrect) "Jawaban Benar!" else "Penjelasan Kompetensi:",
                                fontWeight = FontWeight.Bold,
                                color = if (isUserCorrect) Color(0xFF81C784) else Color(0xFFFFF176),
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.explanation,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Navigation CTA action buttons
        item {
            Spacer(modifier = Modifier.height(22.dp))
            if (!quizState.isAnswerSubmitted) {
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = sensor.primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_answer_button"),
                    enabled = quizState.selectedOptionIndex != null
                ) {
                    Text("Kirim Jawaban", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = sensor.primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_question_button")
                ) {
                    Text(
                        text = if (question.id == totalQuestions) "Lihat Hasil Kuis" else "Lanjut Pertanyaan",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuizScoreBoard(
    sensor: SensorModel,
    quizState: MainViewModel.QuizProgressState,
    totalQuestions: Int,
    viewModel: MainViewModel,
    onRetry: () -> Unit
) {
    val isPassed = quizState.score >= 70

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Star or Trophy element
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    if (isPassed) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFE53935).copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPassed) Icons.Default.EmojiEvents else Icons.Default.SentimentVeryDissatisfied,
                contentDescription = null,
                tint = if (isPassed) Color(0xFF4CAF50) else Color(0xFFE53935),
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isPassed) "Selamat, Kompeten!" else "Belum Memenuhi Capaian",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Hasil Kuis Modul: ${sensor.name}",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Visual Score Ring
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C2A)),
            border = BorderStroke(1.5.dp, if (isPassed) Color(0xFF4CAF50) else Color(0xFFE53935)),
            modifier = Modifier
                .width(220.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NILAI ANDA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Text(
                    text = "${quizState.score}",
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPassed) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
                Divider(color = Color(0xFF312F40), modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Jawaban Benar: ${quizState.correctAnswersCount} / $totalQuestions",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRetry,
                border = BorderStroke(1.5.dp, sensor.primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("retry_quiz_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = sensor.primaryColor)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Ulang")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ulangi Kuis", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.navigateTo(AppScreen.Dashboard) },
                colors = ButtonDefaults.buttonColors(containerColor = sensor.primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("score_back_home_button")
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Capaian Modul", fontWeight = FontWeight.Bold)
            }
        }
    }
}
