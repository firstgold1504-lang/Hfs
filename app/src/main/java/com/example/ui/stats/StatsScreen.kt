package com.example.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MechatronicsData
import com.example.ui.MainViewModel

@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.sensorProgress.collectAsState()
    val quizList by viewModel.quizResults.collectAsState()

    val totalSensors = MechatronicsData.sensors.size
    val readCount = progressList.count { it.introductionRead }
    val simCount = progressList.count { it.simulationCompleted }
    val quizDoneCount = quizList.size
    val averageScore = if (quizDoneCount > 0) quizList.sumOf { it.score } / quizDoneCount else 0

    // Overall vocational progress calculations
    val totalRequiredMetrics = totalSensors * 3 // Theory, Sim, Quiz
    val currentCompletedMetrics = readCount + simCount + quizList.count { it.score >= 70 }
    val competencyPercentage = if (totalRequiredMetrics > 0) {
        ((currentCompletedMetrics.toFloat() / totalRequiredMetrics.toFloat()) * 100).toInt()
    } else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App bar style header
        Text(
            text = "📊 Dashboard Capaian Belajar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        Text(
            text = "Statistik Rekapitulasi Pembelajaran Vokasional Mekatronika",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )

        // Semi circular progress meter or doughnut chart in Custom Canvas!
        CapaianDoughnutChart(percentage = competencyPercentage)

        Spacer(modifier = Modifier.height(20.dp))

        // Average score card & Summary text
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C2A)),
            border = BorderStroke(1.dp, Color(0xFF312F40)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Catatan Rekapitulasi",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Selamat! Progress kompetensi kejuruan Anda berada pada angka $competencyPercentage% dengan rata-rata nilai kuis sebesar $averageScore. Capaian ini menunjukkan pemahaman materi teori sensor, pembagian wiring diagram, dan kemampuan menyelesaikan kuis virtual.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom Visual Barchart for Quiz Scores comparisons
        Text(
            text = "📈 Hasil Nilai Kuis per Modul",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        QuizScoresBarChart(quizList = quizList)

        Spacer(modifier = Modifier.height(24.dp))

        // Complete detailed item progress list
        Text(
            text = "🏁 Status Detail Kriteria Kompetensi",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        MechatronicsData.sensors.forEach { sensor ->
            val progress = progressList.find { it.sensorId == sensor.id }
            val quiz = quizList.find { it.sensorId == sensor.id }

            SensorMetricProgressItem(
                sensorName = sensor.name,
                primaryColor = sensor.primaryColor,
                hasRead = progress?.introductionRead == true,
                hasSimulated = progress?.simulationCompleted == true,
                score = quiz?.score
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Clear statistics / reset data button
        OutlinedButton(
            onClick = { viewModel.resetAllProgress() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
            border = BorderStroke(1.5.dp, Color(0xFFE53935)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_progress_button")
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = "Reset Progress")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Ulang Seluruh Statistik Belajar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CapaianDoughnutChart(percentage: Int) {
    Box(
        modifier = Modifier
            .size(160.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFF161523))
            .border(1.dp, Color(0xFF312F40), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Doughnut track and active arc in canvas
        Canvas(modifier = Modifier.size(130.dp)) {
            val strokeWidthPx = 12.dp.toPx()
            
            // Background Track Circle
            drawCircle(
                color = Color(0x11FFFFFF),
                style = Stroke(width = strokeWidthPx)
            )

            // Active Arc
            val sweepAngle = (percentage.toFloat() / 100f) * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784), Color(0xFF4CAF50))
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        // Inside layout text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$percentage%",
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 28.sp
            )
            Text(
                text = "Kompetensi",
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun QuizScoresBarChart(quizList: List<com.example.data.QuizResult>) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C2A)),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color(0xFF312F40), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // We have 4 mechatronic sensors
                MechatronicsData.sensors.forEach { sensor ->
                    val quiz = quizList.find { it.sensorId == sensor.id }
                    val score = quiz?.score ?: 0 // Default 0 if not completed

                    // Single Bar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Floating Score Indicator
                        Text(
                            text = if (quiz != null) "$score" else "-",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (score >= 70) Color(0xFF81C784) else sensor.primaryColor
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))

                        // High fidelity bar canvas drawing
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(maxOf((score * 1.1).dp, 5.dp)) // Min height so bar exists
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(sensor.primaryColor, sensor.primaryColor.copy(alpha = 0.4f))
                                    )
                                )
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))

                        // Little badge icon below bar
                        Icon(
                            imageVector = when (sensor.iconName) {
                                "wb_sunny" -> Icons.Default.WbSunny
                                "settings_input_antenna" -> Icons.Default.SettingsInputAntenna
                                "directions_run" -> Icons.Default.DirectionsRun
                                else -> Icons.Default.Thermostat
                            },
                            contentDescription = sensor.name,
                            tint = sensor.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Divider(color = Color(0xFF312F40), modifier = Modifier.padding(vertical = 8.dp))
            
            // Graph X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MechatronicsData.sensors.forEach { sensor ->
                    Text(
                        text = sensor.id.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = sensor.primaryColor,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SensorMetricProgressItem(
    sensorName: String,
    primaryColor: Color,
    hasRead: Boolean,
    hasSimulated: Boolean,
    score: Int?
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161523)),
        border = BorderStroke(1.dp, Color(0xFF262436)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row title
            Text(
                text = sensorName,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Sub items grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Criteria A: Teori
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasRead) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (hasRead) primaryColor else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Teori", fontSize = 11.sp, color = if (hasRead) Color.White else Color.Gray)
                }

                // Criteria B: Simulasi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasSimulated) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (hasSimulated) Color(0xFF03A9F4) else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Laboratorium", fontSize = 11.sp, color = if (hasSimulated) Color.White else Color.Gray)
                }

                // Criteria C: Kuis Mark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (score != null) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (score != null) Color(0xFFE91E63) else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (score != null) "Kuis: $score" else "Kuis: -",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (score != null) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}
