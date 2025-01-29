package com.example.servermonitoring.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ServerViewModel : ViewModel() {

    private val _containers = MutableStateFlow<List<Container>>(emptyList())
    val containers: StateFlow<List<Container>> get() = _containers

    private val _postgresContainers = MutableStateFlow<List<Container>>(emptyList())
    val postgresContainers: StateFlow<List<Container>> get() = _postgresContainers

    private val _uptime = MutableStateFlow(0L)
    val uptime: StateFlow<Long> get() = _uptime

    private val _cpuLoad = MutableStateFlow(0.0)
    val cpuLoad: StateFlow<Double> get() = _cpuLoad

    private val _memoryUsage = MutableStateFlow("")
    val memoryUsage: StateFlow<String> get() = _memoryUsage

    private val _diskUsage = MutableStateFlow(0.0)
    val diskUsage: StateFlow<Double> get() = _diskUsage

    private val _refreshInterval = MutableStateFlow(250L)
    val refreshInterval: StateFlow<Long> get() = _refreshInterval

    private val _containerLogs = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())
    val containerLogs: StateFlow<Map<String, String>> get() = _containerLogs

    fun updateRefreshInterval(interval: Long) {
        _refreshInterval.value = interval
    }

    init {
        fetchContainers()
        startPeriodicSystemInfoUpdate()
        startPeriodicContainersUpdate()
    }

    private fun fetchContainers() {
        viewModelScope.launch {
            try {
                _containers.value = RetrofitInstance.api.getContainers()
                _postgresContainers.value = RetrofitInstance.api.getPostgresContainers()
            } catch (e: Exception) {
                Log.e("FetchContainers", "Error fetching containers", e)
            }
        }
    }

    private fun startPeriodicSystemInfoUpdate() {
        viewModelScope.launch {
            while (true) {
                try {
                    // Получаем системную информацию
                    _uptime.value = RetrofitInstance.api.getUptime()
                    _cpuLoad.value = RetrofitInstance.api.getCpuLoad()
                    val memoryUsageString = RetrofitInstance.api.getMemoryUsage()
                    val memoryParts = memoryUsageString.split(" ")
                    if (memoryParts.size == 2) {
                        val usedMemory = memoryParts[0].toDouble()
                        val totalMemory = memoryParts[1].toDouble()
                        _memoryUsage.value = "$usedMemory / $totalMemory"
                    }
                    _diskUsage.value = RetrofitInstance.api.getDiskUsage()
                } catch (e: Exception) {
                    Log.e("FetchSystemInfo", "Error fetching system info", e)
                }

                delay(_refreshInterval.value)
            }
        }
    }

    private fun startPeriodicContainersUpdate() {
        viewModelScope.launch {
            while (true) {
                fetchContainers()
                delay(_refreshInterval.value)
            }
        }
    }

    fun startContainer(containerId: String) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.startContainer(containerId)
                fetchContainers()
            } catch (e: Exception) {
                Log.e("StartContainer", "Error starting container", e)
            }
        }
    }

    fun stopContainer(containerId: String) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.stopContainer(containerId)
                fetchContainers()
            } catch (e: Exception) {
                Log.e("StopContainer", "Error stopping container", e)
            }
        }
    }

    fun restartContainer(containerId: String) {
        viewModelScope.launch {
            try {
                stopContainer(containerId)
                delay(1000)
                startContainer(containerId)
                fetchContainers()
            } catch (e: Exception) {
                Log.e("RestartContainer", "Error restarting container", e)
            }
        }
    }

    private fun fetchContainerLogs(containerId: String) {
        viewModelScope.launch {
            try {
                val newLogs = RetrofitInstance.api.getContainerLogs(containerId)
                _containerLogs.value[containerId] = _containerLogs.value[containerId]?.let { existingLogs ->
                    existingLogs + extractNewLogs(existingLogs, newLogs)
                } ?: newLogs
            } catch (e: Exception) {
                Log.e("FetchContainerLogs", "Error fetching container logs", e)
            }
        }
    }

    private fun extractNewLogs(currentLogs: String, newLogs: String): String {
        return if (currentLogs.isEmpty()) {
            newLogs
        } else {
            val lastIndex = newLogs.indexOf(currentLogs)
            if (lastIndex == -1) {
                newLogs
            } else {
                newLogs.substring(lastIndex + currentLogs.length)
            }
        }
    }

    fun startPeriodicLogsUpdate(containerId: String) {
        viewModelScope.launch {
            while (true) {
                fetchContainerLogs(containerId)
                delay(_refreshInterval.value)
            }
        }
    }

    fun clearLogs(containerId: String) {
        _containerLogs.value[containerId] = ""
    }
}