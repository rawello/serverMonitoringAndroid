package com.example.servermonitoring.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.servermonitoring.ui.theme.ServerMonitoringTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.ui.viewmodel.ServerViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ServerMonitoringScreen(
    onNavigateToContainers: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ServerViewModel
) {
    val uptime by viewModel.uptime.collectAsState()
    val cpuLoad by viewModel.cpuLoad.collectAsState()
    val memoryUsage by viewModel.memoryUsage.collectAsState()
    val diskUsage by viewModel.diskUsage.collectAsState()

    Log.d("ServerMonitoringScreen", "CPU Load: $cpuLoad")
    Log.d("ServerMonitoringScreen", "Memory Usage: $memoryUsage")
    Log.d("ServerMonitoringScreen", "Uptime: $uptime")
    Log.d("ServerMonitoringScreen", "Storage: $diskUsage")

    val memoryParts = memoryUsage.split("/")
    val usedMemory = if (memoryParts.isNotEmpty() && memoryParts[0].trim().isNotEmpty()) {
        memoryParts[0].trim().toDouble()
    } else {
        0.0
    }
    val totalMemory = if (memoryParts.size > 1 && memoryParts[1].trim().isNotEmpty()) {
        memoryParts[1].trim().toDouble()
    } else {
        1.0
    }
    val memoryUsagePercentage = (usedMemory / totalMemory) * 100

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Server Load",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressBar(
                                percentage = cpuLoad * 100,
                                color = Color(0xFFBB86FC)
                            )
                            Text(
                                text = "CPU",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressBar(
                                percentage = memoryUsagePercentage,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "RAM",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressBar(
                                percentage = diskUsage,
                                color = Color(0xFFF44336)
                            )
                            Text(
                                text = "Storage",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = formatUptime(uptime),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Uptime",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Containers Preview",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.White
            )

            viewModel.containers.value.take(3).forEach { container ->
                ContainerPreviewItem(container)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToContainers,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
            ) {
                Text("View Containers")
            }
        }
    }
}

@Composable
fun CircularProgressBar(percentage: Double, color: Color) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.toFloat(),
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8f)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = ((animatedPercentage / 100) * 360f),
                useCenter = false,
                style = Stroke(width = 8f)
            )
        }
        Text(
            text = "${animatedPercentage.toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@SuppressLint("DefaultLocale")
fun formatUptime(seconds: Long): String {
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    val formattedDays = String.format("%02d", days)
    val formattedHours = String.format("%02d", hours)
    val formattedMinutes = String.format("%02d", minutes)
    val formattedSeconds = String.format("%02d", secs)

    return "$formattedDays:$formattedHours:$formattedMinutes:$formattedSeconds"
}

fun shortenContainerId(containerId: String): String {
    return if (containerId.length > 20) {
        containerId.take(20) + "..."
    } else {
        containerId
    }
}

@Composable
fun ContainerPreviewItem(container: Container) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (container.State == "running") Color(0xFF4CAF50) else Color(0xFFF44336),
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "ID: ${shortenContainerId(container.Id)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Image: ${container.Image}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Text(
                    text = "Status: ${container.Status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServerMonitoringScreenPreview() {
    ServerMonitoringTheme {
        Surface(color = Color(0xFF121212)) {
            ServerMonitoringScreen(
                onNavigateToContainers = {},
                onNavigateToSettings = {},
                viewModel = ServerViewModel()
            )
        }
    }
}