package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.MechatronicsRepository
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.sensor.SensorLearningScreen
import com.example.ui.stats.StatsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = MechatronicsRepository(database.sensorDao())
    val viewModel: MainViewModel = viewModel(factory = ViewModelFactory(repository))

    // Determine current dynamic themed signature color based on active learning mode/sensor
    val activeSensorId = when (val screen = viewModel.currentScreen) {
        is AppScreen.SensorDetail -> screen.sensorId
        else -> null
    }

    val dynamicPrimaryColor by animateColorAsState(
        targetValue = when {
            viewModel.currentScreen is AppScreen.Stats -> Color(0xFF4CAF50) // Emerald for analytics
            activeSensorId == "ldr" -> Color(0xFFFFB300) // Amber for light
            activeSensorId == "ultrasonic" -> Color(0xFF0288D1) // Blue for sound
            activeSensorId == "pir" -> Color(0xFFD81B60) // Magenta for Infrared Warmth
            activeSensorId == "dht11" -> Color(0xFF00897B) // Teal for climate homeostasis
            else -> Color(0xFF673AB7) // Royal Purple for Dashboard main
        },
        animationSpec = spring(),
        label = "dynamic_color_transition"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            // Polish-themed M3 bottom navigation bar
            NavigationBar(
                containerColor = Color(0xFF13121F),
                contentColor = Color.White,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // Tab 1: Dashboard Home
                NavigationBarItem(
                    selected = viewModel.currentScreen is AppScreen.Dashboard,
                    onClick = { viewModel.navigateTo(AppScreen.Dashboard) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Menu Beranda") },
                    label = { Text("Beranda", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = dynamicPrimaryColor,
                        indicatorColor = dynamicPrimaryColor,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_home")
                )

                // Tab 2: Materials Sensor Detail route shortcut (defaults to first LDR)
                val isMaterialsSelected = viewModel.currentScreen is AppScreen.SensorDetail
                NavigationBarItem(
                    selected = isMaterialsSelected,
                    onClick = {
                        val targetId = if (viewModel.currentScreen is AppScreen.SensorDetail) {
                            (viewModel.currentScreen as AppScreen.SensorDetail).sensorId
                        } else {
                            "ldr"
                        }
                        viewModel.navigateTo(AppScreen.SensorDetail(targetId))
                    },
                    icon = { Icon(Icons.Default.Router, contentDescription = "Menu Materi") },
                    label = { Text("Materi", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = dynamicPrimaryColor,
                        indicatorColor = dynamicPrimaryColor,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_materi")
                )

                // Tab 3: Evaluation Stats list
                NavigationBarItem(
                    selected = viewModel.currentScreen is AppScreen.Stats,
                    onClick = { viewModel.navigateTo(AppScreen.Stats) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Menu Evaluasi") },
                    label = { Text("Evaluasi", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = dynamicPrimaryColor,
                        indicatorColor = dynamicPrimaryColor,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_evaluasi")
                )
            }
        },
        containerColor = Color(0xFF0F0E17)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = viewModel.currentScreen,
                transitionSpec = {
                    slideInVertically(initialOffsetY = { 300 }) + fadeIn() togetherWith
                            slideOutVertically(targetOffsetY = { -300 }) + fadeOut()
                },
                label = "screen_routing_transition"
            ) { screen ->
                when (screen) {
                    is AppScreen.Dashboard -> {
                        DashboardScreen(viewModel = viewModel)
                    }
                    is AppScreen.SensorDetail -> {
                        SensorLearningScreen(sensorId = screen.sensorId, viewModel = viewModel)
                    }
                    is AppScreen.Stats -> {
                        StatsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
