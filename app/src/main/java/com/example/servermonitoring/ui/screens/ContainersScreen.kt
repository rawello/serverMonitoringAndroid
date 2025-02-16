package com.example.servermonitoring.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.servermonitoring.ui.theme.ServerMonitoringTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.rotate
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.ui.viewmodel.ServerViewModel

@Composable
fun ContainersScreen(
    onNavigateToContainerLogs: (Container) -> Unit,
    viewModel: ServerViewModel
) {
    val containers by viewModel.containers.collectAsState()
    val postgresContainers by viewModel.postgresContainers.collectAsState()

    var showOnlyPostgres by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedContainer by remember { mutableStateOf<Container?>(null) }

    val filteredContainers = if (showOnlyPostgres) {
        postgresContainers
    } else {
        containers
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Containers",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut(),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOnlyPostgres = !showOnlyPostgres }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = showOnlyPostgres,
                            onCheckedChange = { showOnlyPostgres = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFBB86FC),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Show only PostgreSQL containers",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.White
                        )
                    }
                }
            }

            items(filteredContainers) { container ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut()
                ) {
                    ContainerItem(
                        container = container,
                        onClick = {
                                onNavigateToContainerLogs(container)
                        },
                        onStartClick = {
                            selectedContainer = container
                            showConfirmationDialog = true
                        },
                        onStopClick = {
                            selectedContainer = container
                            showConfirmationDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showConfirmationDialog && selectedContainer != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmationDialog = false
                selectedContainer = null
            },
            title = {
                Text(
                    text = "Confirm Action",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to ${if (selectedContainer?.State == "running") "stop" else "start"} the container?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedContainer?.State == "running") {
                            viewModel.stopContainer(selectedContainer!!.Id)
                        } else {
                            viewModel.startContainer(selectedContainer!!.Id)
                        }
                        showConfirmationDialog = false
                        selectedContainer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showConfirmationDialog = false
                        selectedContainer = null
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
}

@Composable
fun ContainerItem(
    container: Container,
    onClick: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (container.State == "running") Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = "Status",
                    tint = if (container.State == "running") Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ID: ${shortenContainerId(container.Id)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(180f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Image: ${container.Image}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            Text(
                text = "Status: ${container.Status}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onStartClick,
                    modifier = Modifier.weight(1f),
                    enabled = container.State != "running",
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4CAF50),
                    ),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onStopClick,
                    modifier = Modifier.weight(1f),
                    enabled = container.State == "running",
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336),
                    ),
                    border = BorderStroke(1.dp, Color(0xFFF44336))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Stop")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContainersScreenPreview() {
    ServerMonitoringTheme {
        Surface(color = Color(0xFF121212)) {
            ContainersScreen(
                onNavigateToContainerLogs = {},
                viewModel = ServerViewModel()
            )
        }
    }
}