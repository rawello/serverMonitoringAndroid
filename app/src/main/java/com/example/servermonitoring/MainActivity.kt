package com.example.servermonitoring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.ui.screens.ContainersScreen
import com.example.servermonitoring.ui.screens.ContainerLogsScreen
import com.example.servermonitoring.ui.screens.ServerMonitoringScreen
import com.example.servermonitoring.ui.screens.SettingsScreen
import com.example.servermonitoring.ui.theme.ServerMonitoringTheme
import com.example.servermonitoring.ui.viewmodel.ServerViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServerMonitoringTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ServerMonitoringApp()
                }
            }
        }
    }
}

@Composable
fun ServerMonitoringApp() {
    val navController = rememberNavController()
    val viewModel: ServerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "serverLoad") {
        composable("serverLoad") {
            ServerMonitoringScreen(
                onNavigateToContainers = { navController.navigate("containers") },
                onNavigateToSettings = { navController.navigate("settings") },
                viewModel = viewModel
            )
        }
        composable("containers") {
            ContainersScreen(
                onNavigateToContainerLogs = { container ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("containerJson", container.toJson())
                    navController.navigate("containerLogs")
                },
                viewModel = viewModel
            )
        }
        composable("containerLogs") {
            val containerJson = navController.previousBackStackEntry?.savedStateHandle?.get<String>("containerJson")
            val container = containerJson?.toContainer() ?: Container("1", "nginx:latest", "Stopped", "exited")

            ContainerLogsScreen(
                container = container,
                onBack = { navController.popBackStack() },
                onStartClick = { viewModel.startContainer(container.Id) },
                onStopClick = { viewModel.stopContainer(container.Id) },
                onRestartClick = { viewModel.restartContainer(container.Id) },
                viewModel = viewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSaveSettings = { settings ->
                    viewModel.updateRefreshInterval(settings.refreshInterval)
                },
                viewModel = viewModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServerMonitoringAppPreview() {
    ServerMonitoringTheme {
        ServerMonitoringApp()
    }
}

fun Container.toJson(): String {
    return Gson().toJson(this)
}

fun String.toContainer(): Container {
    return Gson().fromJson(this, Container::class.java)
}