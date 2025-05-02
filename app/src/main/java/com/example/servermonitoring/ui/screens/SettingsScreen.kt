package com.example.servermonitoring.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.servermonitoring.ui.theme.ServerMonitoringTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Slider
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.servermonitoring.ui.viewmodel.ServerViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSaveSettings: (Settings) -> Unit,
    viewModel: ServerViewModel
) {
    var serverIp by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var refreshInterval by remember { mutableStateOf(1000f) }

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
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = serverIp,
            onValueChange = { serverIp = it },
            label = { Text("Server IP") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text("Server Port") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Refresh Interval: ${refreshInterval.toInt()} ms",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Slider(
            value = refreshInterval,
            onValueChange = { refreshInterval = it },
            valueRange = 0f..2000f,
            steps = 200,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val settings = Settings(
                    serverIp = serverIp,
                    serverPort = serverPort,
                    username = username,
                    password = password,
                    refreshInterval = refreshInterval.toLong()
                )
                onSaveSettings(settings)
                viewModel.updateRefreshInterval(refreshInterval.toLong())
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Save Settings")
        }
    }
}

data class Settings(
    val serverIp: String,
    val serverPort: String,
    val username: String,
    val password: String,
    val refreshInterval: Long
)

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ServerMonitoringTheme {
        SettingsScreen(onBack = {}, onSaveSettings = {},
            viewModel = TODO()
        )
    }
}