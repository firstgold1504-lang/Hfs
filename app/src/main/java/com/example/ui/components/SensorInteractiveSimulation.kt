package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SensorModel
import com.example.ui.MainViewModel
import kotlin.math.sin

@Composable
fun SensorSimulationContainer(
    sensor: SensorModel,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onCompleted: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Laboratorium Virtual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = sensor.primaryColor
                    )
                    Text(
                        text = "Simulasi Interaktif & Wiring",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = sensor.primaryColor.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = when (sensor.iconName) {
                            "wb_sunny" -> Icons.Default.WbSunny
                            "settings_input_antenna" -> Icons.Default.SettingsInputAntenna
                            "directions_run" -> Icons.Default.DirectionsRun
                            else -> Icons.Default.Thermostat
                        },
                        contentDescription = "Simulasi Icon",
                        tint = sensor.primaryColor,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulation Window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C0E1A)) // Dark tech terminal background
                    .border(2.dp, sensor.primaryColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                when (sensor.id) {
                    "ldr" -> LdrSimulationPanel(viewModel = viewModel, sensor = sensor)
                    "ultrasonic" -> UltrasonicSimulationPanel(viewModel = viewModel, sensor = sensor)
                    "pir" -> PirSimulationPanel(viewModel = viewModel, sensor = sensor)
                    "dht11" -> DhtSimulationPanel(viewModel = viewModel, sensor = sensor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wiring Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16192B)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "🔌 Koneksi Pin & Komponen",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    sensor.pinDefinitions.forEach { pin ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${pin.name} ➔ ${pin.arduinoConnection}",
                                color = sensor.primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "(${pin.type})",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action completes verification
            Button(
                onClick = {
                    viewModel.markSimulationCompleted(sensor.id)
                    onCompleted()
                },
                colors = ButtonDefaults.buttonColors(containerColor = sensor.primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complete_simulation_button")
            ) {
                Icon(Icons.Default.TaskAlt, contentDescription = "Selesai")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Konfirmasi Eksperimen Selesai", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LdrSimulationPanel(viewModel: MainViewModel, sensor: SensorModel) {
    val brightnessPercent = viewModel.ldrBrightness
    // LDR resistance decreases as brightness increases
    val resistanceKOhms = ((100f - brightnessPercent) * 50f / 100f + 0.1f) // 0.1k Ohm to 50k Ohm
    // Voltage measured (Vout) at A0 = Vin * R_pulldown / (R_ldr + R_pulldown) with R_pulldown = 10k Ohm, Vin = 5V
    val valA0 = 5.0f * 10f / (resistanceKOhms + 10f)
    val adcValue = (valA0 / 5.0f * 1023f).toInt()
    
    val thresholdAdc = 400
    val isLedActive = adcValue < thresholdAdc // Dark triggers LED

    val ledGlowColor by animateColorAsState(
        targetValue = if (isLedActive) Color(0xFFFFEB3B) else Color(0x33555555),
        animationSpec = spring()
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Output Graphic Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw virtual Arduino Uno box
                val arduinoColor = Color(0xFF005C8A)
                drawRoundRect(
                    color = arduinoColor,
                    size = Size(100.dp.toPx(), 70.dp.toPx()),
                    topLeft = Offset(20.dp.toPx(), 20.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                )

                // Text labeled inside arduino
                // Draw LDR on panel
                val ldrX = width - 130.dp.toPx()
                val ldrY = 40.dp.toPx()
                drawCircle(
                    color = Color(0xFFC62828),
                    radius = 16.dp.toPx(),
                    center = Offset(ldrX, ldrY)
                )
                // Draw zigzag inner cadmium lines
                drawCircle(
                    color = Color(0xFFFFB300),
                    radius = 11.dp.toPx(),
                    center = Offset(ldrX, ldrY)
                )

                // Draw LED
                val ledX = width - 40.dp.toPx()
                val ledY = 90.dp.toPx()
                drawCircle(
                    color = ledGlowColor,
                    radius = 14.dp.toPx(),
                    center = Offset(ledX, ledY)
                )
                
                // Wiring paths
                // LDR pin A0 wire
                drawLine(
                    color = Color.Green,
                    start = Offset(40.dp.toPx(), 50.dp.toPx()), // Arduino Pin A0 representation
                    end = Offset(ldrX - 16.dp.toPx(), ldrY),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                // LED D13 wire
                drawLine(
                    color = if (isLedActive) Color.Yellow else Color.Gray,
                    start = Offset(50.dp.toPx(), 60.dp.toPx()), // Arduino D13 Pin
                    end = Offset(ledX, ledY - 14.dp.toPx()),
                    strokeWidth = 3f
                )

                // Draw Sunbeams depending on brightness
                val sunbeamsCount = 8
                val radiusSun = 26.dp.toPx() + (brightnessPercent / 5f)
                val beamColor = Color(0x33FFB300).copy(alpha = brightnessPercent / 100f)
                if (brightnessPercent > 10f) {
                    for (i in 0 until sunbeamsCount) {
                        val angle = (i * 360f / sunbeamsCount) * (Math.PI / 180f)
                        val endX = ldrX + (radiusSun * kotlin.math.cos(angle)).toFloat()
                        val endY = ldrY + (radiusSun * sin(angle)).toFloat()
                        drawLine(
                            color = beamColor,
                            start = Offset(ldrX, ldrY),
                            end = Offset(endX, endY),
                            strokeWidth = 4f
                        )
                    }
                }
            }

            // Absolute overlays for labels
            Text(
                text = "ARDUINO",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 28.dp, top = 25.dp)
            )

            Text(
                text = "LDR",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 120.dp, top = 65.dp)
            )

            Text(
                text = "LED",
                color = if (isLedActive) Color.Yellow else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 26.dp, top = 115.dp)
            )

            // Live status terminal overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color(0xE605050A), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "SYS_LOGGER: LDR Active Monitor",
                        color = Color.Cyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Cahaya: $brightnessPercent% | R_LDR: ${String.format("%.2f", resistanceKOhms)}kΩ",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "ADC: $adcValue/1023 (A0) | LED: ${if (isLedActive) "ON" else "OFF"}",
                        color = if (isLedActive) Color.Green else Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Action Sliders for simulation interactions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LightMode, contentDescription = "Terang", tint = Color.Yellow)
            Spacer(modifier = Modifier.width(6.dp))
            Slider(
                value = brightnessPercent,
                onValueChange = { viewModel.ldrBrightness = it },
                valueRange = 0f..100f,
                modifier = Modifier
                    .weight(1f)
                    .testTag("ldr_brightness_slider"),
                colors = SliderDefaults.colors(
                    activeTrackColor = sensor.primaryColor,
                    thumbColor = sensor.primaryColor
                )
            )
            Text(
                text = "${brightnessPercent.toInt()}%",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(45.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun UltrasonicSimulationPanel(viewModel: MainViewModel, sensor: SensorModel) {
    val distance = viewModel.ultrasonicDistance
    val isAlert = distance < 20f

    val pulseAnim = rememberInfiniteTransition()
    val pulseRadius by pulseAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val alertGlowColor by animateColorAsState(
        targetValue = if (isAlert) Color.Red else Color.Green,
        animationSpec = spring()
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw HC-SR04 Modules (two eyes: left T, right R)
                val sensorColor = Color(0xFF003F8A)
                drawRoundRect(
                    color = sensorColor,
                    size = Size(80.dp.toPx(), 40.dp.toPx()),
                    topLeft = Offset(10.dp.toPx(), height / 2 - 20.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )

                // Transmitter (T) Circle
                drawCircle(
                    color = Color(0xFFC0C0C0),
                    radius = 14.dp.toPx(),
                    center = Offset(25.dp.toPx(), height / 2)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 10.dp.toPx(),
                    center = Offset(25.dp.toPx(), height / 2)
                )

                // Receiver (R) Circle
                drawCircle(
                    color = Color(0xFFC0C0C0),
                    radius = 14.dp.toPx(),
                    center = Offset(75.dp.toPx(), height / 2)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 10.dp.toPx(),
                    center = Offset(75.dp.toPx(), height / 2)
                )

                // Draw obstacle block based on distance
                val obstacleX = 110.dp.toPx() + (distance / 200f) * (width - 150.dp.toPx() - 110.dp.toPx())
                drawRoundRect(
                    color = if (isAlert) Color(0xFFC62828) else Color(0xFF424242),
                    size = Size(20.dp.toPx(), 65.dp.toPx()),
                    topLeft = Offset(obstacleX, height / 2 - 32.5.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )

                // Draw Sound Waves moving
                val startWaveX = 95.dp.toPx()
                val waveLength = obstacleX - startWaveX
                
                // Emitted Waves
                val pulseX = startWaveX + waveLength * pulseRadius
                if (pulseX < obstacleX) {
                    drawArc(
                        color = Color.Cyan.copy(alpha = 1f - pulseRadius),
                        startAngle = -45f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(pulseX - 10.dp.toPx(), height / 2 - 15.dp.toPx()),
                        size = Size(30.dp.toPx(), 30.dp.toPx()),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                }

                // Echo reflected waves (red alert)
                if (isAlert) {
                    val echoRadius = pulseRadius
                    val echoX = obstacleX - waveLength * echoRadius
                    if (echoX > startWaveX) {
                        drawArc(
                            color = Color.Red.copy(alpha = 1f - echoRadius),
                            startAngle = 135f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(echoX - 10.dp.toPx(), height / 2 - 15.dp.toPx()),
                            size = Size(30.dp.toPx(), 30.dp.toPx()),
                            style = Stroke(width = 3f, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // Labels overlay
            Text(
                text = "TX",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 22.dp, top = 113.dp)
            )
            Text(
                text = "RX",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 72.dp, top = 113.dp)
            )

            Text(
                text = "HALANGAN",
                color = if (isAlert) Color.Red else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 5.dp)
            )

            // Alert sound buzzer/LED indicator visualizer
            Surface(
                color = alertGlowColor.copy(alpha = 0.2f),
                border = BorderStroke(1.5.dp, alertGlowColor),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(alertGlowColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAlert) "AWAS: DEKAT!" else "AMAN",
                        color = alertGlowColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Telemetry Terminal Log
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color(0xE605050A), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "SYS_LOGGER: sonar_hcsr04_node",
                        color = Color.Cyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Jarak Terukur: ${String.format("%.1f", distance)} cm",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kondisi: ${if (isAlert) "Buzzer Bunyi & Alarm LED Aktif" else "Menunggu rintangan..."}",
                        color = if (isAlert) Color.Red else Color.LightGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Control Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.SettingsInputAntenna, contentDescription = "Distance Slider", tint = sensor.primaryColor)
            Spacer(modifier = Modifier.width(6.dp))
            Slider(
                value = distance,
                onValueChange = { viewModel.ultrasonicDistance = it },
                valueRange = 2f..200f,
                modifier = Modifier
                    .weight(1f)
                    .testTag("ultrasonic_distance_slider"),
                colors = SliderDefaults.colors(
                    activeTrackColor = sensor.primaryColor,
                    thumbColor = sensor.primaryColor
                )
            )
            Text(
                text = "${distance.toInt()}cm",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun PirSimulationPanel(viewModel: MainViewModel, sensor: SensorModel) {
    val motion = viewModel.pirMovementDetected
    val timer = viewModel.pirTimerRemaining

    val rippleAnim = rememberInfiniteTransition()
    val waveScale by rippleAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw PIR Cube
                val sensorBaseColor = Color(0xFFA1887F)
                drawRoundRect(
                    color = sensorBaseColor,
                    size = Size(65.dp.toPx(), 45.dp.toPx()),
                    topLeft = Offset(15.dp.toPx(), height / 2 - 22.5.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )

                // White Dome (Fresnel Lens)
                drawCircle(
                    color = Color.White.copy(alpha = 0.95f),
                    radius = 20.dp.toPx(),
                    center = Offset(50.dp.toPx(), height / 2)
                )
                // Draw Fresnel ridges
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    radius = 12.dp.toPx(),
                    center = Offset(50.dp.toPx(), height / 2),
                    style = Stroke(width = 2f)
                )

                // Relay Switch depiction
                val relayX = width - 110.dp.toPx()
                val relayY = 30.dp.toPx()
                drawRoundRect(
                    color = Color(0xFF0D47A1), // Blue relay box
                    size = Size(40.dp.toPx(), 30.dp.toPx()),
                    topLeft = Offset(relayX, relayY),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )

                // Relay switch contacts drawing
                val switchStateClose = motion
                drawLine(
                    color = Color.White,
                    start = Offset(relayX + 15.dp.toPx(), relayY + 15.dp.toPx()),
                    end = if (switchStateClose) Offset(relayX + 35.dp.toPx(), relayY + 25.dp.toPx()) else Offset(relayX + 35.dp.toPx(), relayY + 5.dp.toPx()),
                    strokeWidth = 3f
                )

                // Red/Gray Lamp Bulb
                val lampX = width - 40.dp.toPx()
                val lampY = height / 2
                drawCircle(
                    color = if (motion) Color(0xFFFFB300) else Color(0xFF333333),
                    radius = 18.dp.toPx(),
                    center = Offset(lampX, lampY)
                )
                // Socket
                drawRect(
                    color = Color.Gray,
                    size = Size(14.dp.toPx(), 8.dp.toPx()),
                    topLeft = Offset(lampX - 7.dp.toPx(), lampY + 18.dp.toPx())
                )

                // PIR Infra Output Waves (fading out)
                if (motion) {
                    drawCircle(
                        color = Color.Red.copy(alpha = 1f - waveScale),
                        radius = 25.dp.toPx() + waveScale * 80f,
                        center = Offset(50.dp.toPx(), height / 2),
                        style = Stroke(width = 4f)
                    )
                }
            }

            // Absolute Labels
            Text(
                text = "PIR SENSOR",
                color = Color.Black,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            )

            Text(
                text = "RELAY",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 80.dp, top = 38.dp)
            )

            Text(
                text = "LAMPU 220V",
                color = if (motion) Color(0xFFFFB300) else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp, top = 50.dp)
            )

            // Logging and Telemetry box
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color(0xE605050A), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "SYS_LOGGER: alarm_pir_daemon",
                        color = Color.Cyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Status: ${if (motion) "🚨 RINTANGAN DETEKSI" else "💤 STANDBY"}",
                        color = if (motion) Color.Red else Color.Green,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    if (motion) {
                        Text(
                            text = "Lampu Aktif: $timer dtk ...",
                            color = Color.Yellow,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Text(
                            text = "Tekan tombol di bawah",
                            color = Color.LightGray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Action Trigger Button
        Button(
            onClick = { viewModel.triggerPirMotion() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (motion) Color.Red else sensor.primaryColor
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("pir_trigger_motion_button"),
            enabled = !motion
        ) {
            Icon(Icons.Default.DirectionsRun, contentDescription = "Berjalan")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (motion) "Sedang Mendeteksi Gerakan ($timer dtk)" else "Simulasi Gerakan Lewat",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DhtSimulationPanel(viewModel: MainViewModel, sensor: SensorModel) {
    val temp = viewModel.dhtTemperature
    val hum = viewModel.dhtHumidity

    val fanAnim = rememberInfiniteTransition()
    val isFanSpinning = temp > 30f
    
    // Animate fan rotation if temp is high
    val fanRotations by fanAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (temp > 40f) 500 else 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw DHT11 Blue Block Grid
                val dhtColor = Color(0xFF00388F)
                drawRoundRect(
                    color = dhtColor,
                    size = Size(40.dp.toPx(), 65.dp.toPx()),
                    topLeft = Offset(15.dp.toPx(), height / 2 - 32.5.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )
                // Draw ventilation slits on DHT
                for (i in 0 until 5) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.4f),
                        size = Size(20.dp.toPx(), 4.dp.toPx()),
                        topLeft = Offset(25.dp.toPx(), height / 2 - 22.dp.toPx() + (i * 10f).dp.toPx())
                    )
                }

                // Draw Fan body or outline on the right
                val fanX = width - 50.dp.toPx()
                val fanY = height / 2
                drawCircle(
                    color = Color.DarkGray,
                    radius = 24.dp.toPx(),
                    center = Offset(fanX, fanY),
                    style = Stroke(width = 3f)
                )
            }

            // Dynamic LCD simulation screen in center
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A1E)), // Vintage green LCD
                border = BorderStroke(1.5.dp, Color(0xFF4CAF50)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(135.dp)
                    .height(65.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TEMP: ${temp.toInt()} \u00B0C ${if (temp > 35f) "🥵" else if (temp < 18f) "🥶" else "😌"}",
                        color = Color(0xFF5EFE5E),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "HUMI: ${hum.toInt()} %RH 💧",
                        color = Color(0xFF5EFE5E),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "FAN STATUS: ${if (isFanSpinning) "AC ACTIVE" else "STANDBY"}",
                        color = if (isFanSpinning) Color.Yellow else Color(0xFF5EFE5E),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }

            // Labels overlay
            Text(
                text = "DHT11",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 18.dp, top = 25.dp)
            )

            // Animated Rotating Fan Propeller
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 26.dp)
                    .size(48.dp)
                    .rotate(if (isFanSpinning) fanRotations else 0f)
            ) {
                Icon(
                    imageVector = Icons.Default.Cyclone,
                    contentDescription = "Spining fan",
                    tint = if (isFanSpinning) Color.Cyan else Color.Gray,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = "KIPAS 5V",
                color = if (isFanSpinning) Color.Cyan else Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp, top = 56.dp)
            )
        }

        // Sliders for temperature and humidity adjustments
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131526))
                .padding(6.dp)
        ) {
            // Temperature row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🌡️ Suhu", color = Color.White, fontSize = 10.sp, modifier = Modifier.width(48.dp))
                Slider(
                    value = temp,
                    onValueChange = { viewModel.dhtTemperature = it },
                    valueRange = 10f..50f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dht_temp_slider"),
                    colors = SliderDefaults.colors(
                        activeTrackColor = sensor.primaryColor,
                        thumbColor = sensor.primaryColor
                    )
                )
                Text(
                    text = "${temp.toInt()}\u00B0C",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.width(35.dp),
                    textAlign = TextAlign.End
                )
            }

            // Humidity row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💧 Humi", color = Color.White, fontSize = 10.sp, modifier = Modifier.width(48.dp))
                Slider(
                    value = hum,
                    onValueChange = { viewModel.dhtHumidity = it },
                    valueRange = 20f..100f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dht_humi_slider"),
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Cyan,
                        thumbColor = Color.Cyan
                    )
                )
                Text(
                    text = "${hum.toInt()}%",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.width(35.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
