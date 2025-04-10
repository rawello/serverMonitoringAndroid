package com.example.servermonitoring.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servermonitoring.model.Container
import com.example.servermonitoring.network.RetrofitInstance
import kotlinx.coroutines.async
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

    private val _memoryUsage = MutableStateFlow(0.0)
    val memoryUsage: StateFlow<Double> get() = _memoryUsage

    private val _diskUsage = MutableStateFlow(0.0)
    val diskUsage: StateFlow<Double> get() = _diskUsage

    private val _refreshInterval = MutableStateFlow(500L)
    val refreshInterval: StateFlow<Long> get() = _refreshInterval

    private val _containerLogs = MutableStateFlow<Map<String, String>>(mutableMapOf())
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
            } catch (e: Exception) {
                Log.e("FetchContainers", "Error fetching containers", e)
            }
        }
    }

    private fun startPeriodicSystemInfoUpdate() {
        viewModelScope.launch {
            while (true) {
                val startTime = System.currentTimeMillis()
                try {
                    val uptimeDeferred = async { RetrofitInstance.api.getUptime() }
                    val cpuDeferred = async { RetrofitInstance.api.getCpuLoad() }
                    val memoryDeferred = async { RetrofitInstance.api.getMemoryUsage() }

                    val uptimeResponse = uptimeDeferred.await()
                    val cpuResponse = cpuDeferred.await()
                    val memoryResponse = memoryDeferred.await()

                    val systemInfo = mapOf(
                        "uptime" to (uptimeResponse.uptimeSeconds as? Long ?: 0L),
                        "cpu_load" to (cpuResponse.cpuLoad ?: 0.0),
                        "memory_usage" to (memoryResponse.memoryUsage ?: 0),
                        "disk_usage" to (memoryResponse.diskUsage ?: 0.0)
                    )

                    _uptime.value = systemInfo["uptime"] as Long
                    _cpuLoad.value = systemInfo["cpu_load"] as Double
                    _memoryUsage.value = systemInfo["memory_usage"] as Double
                    _diskUsage.value = systemInfo["disk_usage"] as Double

                } catch (e: Exception) {
                    Log.e("SystemInfo", "Update error: ${e.message}")
                }

                val elapsed = System.currentTimeMillis() - startTime
                delay(maxOf(0, refreshInterval.value - elapsed))
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
                val response = RetrofitInstance.api.startContainer(containerId)
                if (response.success) {
                    fetchContainers()
                } else {
                    Log.e("StartContainer", "Server error: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("StartContainer", "Error: ${e.message}")
            }
        }
    }

    fun stopContainer(containerId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.stopContainer(containerId)
                if (response.success) {
                    fetchContainers()
                }
            } catch (e: Exception) {
                Log.e("StopContainer", "Error: ${e.message}")
            }
        }
    }

    fun restartContainer(containerId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.restartContainer(containerId)
                if (response.success) {
                    fetchContainers()
                }
            } catch (e: Exception) {
                Log.e("RestartContainer", "Error: ${e.message}")
            }
        }
    }

    private fun fetchContainerLogs(containerId: String) {
        viewModelScope.launch {
            try {
                val newLogs = RetrofitInstance.api.getContainerLogs(containerId)
                _containerLogs.value = _containerLogs.value.toMutableMap().apply {
                    this[containerId] = this[containerId]?.let { existingLogs ->
                        existingLogs + extractNewLogs(existingLogs, newLogs)
                    } ?: newLogs
                }
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
        _containerLogs.value = _containerLogs.value.toMutableMap().apply {
            this[containerId] = ""
        }
    }
}