package com.example.servermonitoring.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.ui.viewmodel.ServerViewModel

@Composable
fun ContainerLogsScreen(
    container: Container,
    onBack: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onRestartClick: () -> Unit,
    viewModel: ServerViewModel
) {
    val logs by viewModel.containerLogs.collectAsState()
    val containerLogs = logs[container.Id] ?: ""
    val logLines = containerLogs.split("\n")
    var currentContainer by remember { mutableStateOf(container) }

    var showStartConfirmation by remember { mutableStateOf(false) }
    var showStopConfirmation by remember { mutableStateOf(false) }
    var showRestartConfirmation by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var autoScroll by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(currentContainer.Id) {
        viewModel.startPeriodicLogsUpdate(currentContainer.Id)
    }

    LaunchedEffect(viewModel.containers) {
        val updatedContainer = viewModel.containers.value.find { it.Id == currentContainer.Id }
        if (updatedContainer != null) {
            currentContainer = updatedContainer
        }
    }

    LaunchedEffect(logLines.size) {
        if (autoScroll) {
            coroutineScope.launch {
                scrollState.scrollToItem(logLines.size)
            }
        }
    }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            autoScroll = false
        }
    }

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
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Logs for ${shortenContainerId(currentContainer.Id)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.clearLogs(currentContainer.Id) }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Logs", tint = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(logLines) { logLine ->
                        Text(
                            text = logLine,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .fillMaxWidth(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }

                if (!autoScroll) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    scrollState.scrollToItem(logLines.size)
                                    autoScroll = true
                                }
                            },
                            containerColor = Color(0xFFBB86FC),
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Scroll to Bottom")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { showStartConfirmation = true },
                        modifier = Modifier.weight(1f),
                        enabled = currentContainer.State != "running",
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50)),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start", modifier = Modifier.widthIn(min = 60.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { showStopConfirmation = true },
                        modifier = Modifier.weight(1f),
                        enabled = currentContainer.State == "running",
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                        border = BorderStroke(1.dp, Color(0xFFF44336))
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Stop")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stop", modifier = Modifier.widthIn(min = 60.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showRestartConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentContainer.State == "running",
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBB86FC)),
                    border = BorderStroke(1.dp, Color(0xFFBB86FC))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restart", modifier = Modifier.widthIn(min = 60.dp))
                }
            }
        }
    }

    if (showStartConfirmation) {
        AlertDialog(
            onDismissRequest = { showStartConfirmation = false },
            title = { Text("Confirm Start") },
            text = { Text("Are you sure you want to start the container?") },
            confirmButton = {
                Button(
                    onClick = {
                        onStartClick()
                        showStartConfirmation = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStartConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStopConfirmation) {
        AlertDialog(
            onDismissRequest = { showStopConfirmation = false },
            title = { Text("Confirm Stop") },
            text = { Text("Are you sure you want to stop the container?") },
            confirmButton = {
                Button(
                    onClick = {
                        onStopClick()
                        showStopConfirmation = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStopConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRestartConfirmation) {
        AlertDialog(
            onDismissRequest = { showRestartConfirmation = false },
            title = { Text("Confirm Restart") },
            text = { Text("Are you sure you want to restart the container?") },
            confirmButton = {
                Button(
                    onClick = {
                        onRestartClick()
                        showRestartConfirmation = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRestartConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}