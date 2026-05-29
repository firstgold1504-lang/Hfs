package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MechatronicsData
import com.example.data.QuizResult
import com.example.data.SensorModel
import com.example.data.SensorProgress
import com.example.ui.AppScreen
import com.example.ui.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.sensorProgress.collectAsState()
    val quizList by viewModel.quizResults.collectAsState()

    // Calculate completion metrics
    val totalSensors = MechatronicsData.sensors.size
    val readCount = progressList.count { it.introductionRead }
    val simCount = progressList.count { it.simulationCompleted }
    val quizCount = quizList.size
    
    val totalPossibleMetrics = totalSensors * 3 // 3 parts: read, sim, quiz
    val completedMetrics = readCount + simCount + quizList.count { it.score >= 70 }
    val progressPercent = if (totalPossibleMetrics > 0) {
        (completedMetrics.toFloat() / totalPossibleMetrics.toFloat() * 100).toInt()
    } else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17)) // Dark slate cyberpunk space
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp)
    ) {
        // App header banner
        item {
            HeaderBanner(progressPercent = progressPercent)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(title = "📊 Ringkasan Vokasional", subtitle = "Pantau kurikulum belajar mekatronika Anda")
            Spacer(modifier = Modifier.height(12.dp))
            StatsGridCard(
                readCount = readCount,
                simCount = simCount,
                quizCount = quizCount,
                totalSensors = totalSensors,
                viewModel = viewModel
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(title = "🔌 Daftar Sensor Mekatronika", subtitle = "Pilih modul untuk mulai bereksperimen")
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Sensor cards
        items(MechatronicsData.sensors) { sensor ->
            val progress = progressList.find { it.sensorId == sensor.id }
            val quizResult = quizList.find { it.sensorId == sensor.id }
            
            SensorCardRow(
                sensor = sensor,
                progress = progress,
                quizResult = quizResult,
                onClick = {
                    viewModel.navigateTo(AppScreen.SensorDetail(sensor.id))
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
fun HeaderBanner(progressPercent: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF3F51B5), Color(0xFF673AB7))
                )
            )
            .border(1.dp, Color(0xFF7986CB).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "MekaSensor Learn",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Model Pembelajaran Vokasional Kejuruan Mekatronika",
                    fontSize = 12.sp,
                    color = Color(0xFFE0E0E0),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick progress text
                Text(
                    text = "Progres Kompetensi: $progressPercent%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    color = Color.Yellow,
                    trackColor = Color(0xFF3F51B5).copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Beautiful generated app icon displayed inside a high tech circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(2.dp, Color.White, CircleShape)
                    .padding(4.dp)
            ) {
                // If drawables contains generated image, reference it
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1780019244590),
                    contentDescription = "Mechatronics Icon",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun StatsGridCard(
    readCount: Int,
    simCount: Int,
    quizCount: Int,
    totalSensors: Int,
    viewModel: MainViewModel
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C2A)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF312F40), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressStatItem(
                value = "$readCount/$totalSensors",
                label = "Materi Dibaca",
                color = Color(0xFF4CAF50),
                icon = Icons.Default.MenuBook
            )
            
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = Color(0xFF312F40)
            )

            ProgressStatItem(
                value = "$simCount/$totalSensors",
                label = "Simulasi Selesai",
                color = Color(0xFF03A9F4),
                icon = Icons.Default.Construction
            )
            
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = Color(0xFF312F40)
            )

            ProgressStatItem(
                value = "$quizCount/$totalSensors",
                label = "Kuis Dikerjakan",
                color = Color(0xFFE91E63),
                icon = Icons.Default.Quiz
            )
        }
    }
}

@Composable
fun ProgressStatItem(
    value: String,
    label: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SensorCardRow(
    sensor: SensorModel,
    progress: SensorProgress?,
    quizResult: QuizResult?,
    onClick: () -> Unit
) {
    val isRead = progress?.introductionRead == true
    val isSim = progress?.simulationCompleted == true
    val isQuiz = quizResult != null

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161523)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.5.dp, sensor.primaryColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .testTag("sensor_card_${sensor.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Sensor Badge
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(sensor.primaryColor.copy(alpha = 0.15f))
                        .border(1.dp, sensor.primaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (sensor.iconName) {
                            "wb_sunny" -> Icons.Default.WbSunny
                            "settings_input_antenna" -> Icons.Default.SettingsInputAntenna
                            "directions_run" -> Icons.Default.DirectionsRun
                            else -> Icons.Default.Thermostat
                        },
                        contentDescription = sensor.name,
                        tint = sensor.primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = sensor.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = sensor.type,
                        fontSize = 11.sp,
                        color = sensor.primaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Quiz Score Badge
                if (isQuiz) {
                    Surface(
                        color = if ((quizResult?.score ?: 0) >= 70) Color(0xFF43A047).copy(alpha = 0.2f) else Color(0xFFE53935).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if ((quizResult?.score ?: 0) >= 70) Color(0xFF43A047) else Color(0xFFE53935)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Kuis: ${quizResult?.score}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if ((quizResult?.score ?: 0) >= 70) Color(0xFF81C784) else Color(0xFFE57373),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = sensor.description,
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress chips/indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProgressChip(label = "Teori", active = isRead, color = sensor.primaryColor)
                    ProgressChip(label = "Simulasi", active = isSim, color = Color(0xFF03A9F4))
                    ProgressChip(label = "Kuis", active = isQuiz, color = Color(0xFFE91E63))
                }

                // Arrow Action
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pelajari",
                        color = sensor.primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Mulai belajar",
                        tint = sensor.primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressChip(label: String, active: Boolean, color: Color) {
    Surface(
        color = if (active) color.copy(alpha = 0.2f) else Color(0x1AFFFFFF),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.5.dp, if (active) color else Color.Gray.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (active) color else Color.Gray,
                modifier = Modifier.size(10.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (active) Color.White else Color.Gray
            )
        }
    }
}
