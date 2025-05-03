package com.example.servermonitoring.model

import android.util.Log

data class SystemInfo(
    val uptime: Long? = null,
    val cpuLoad: Double? = null,
    val usedMemory: Long? = null,
    val totalMemory: Long? = null,
    val diskUsage: Double? = null
) {
    companion object {
        fun parseUptime(uptimeString: String): SystemInfo {
            return try {
                SystemInfo(uptime = uptimeString.toLongOrNull())
            } catch (e: Exception) {
                SystemInfo()
            }
        }

        fun parseCpuLoad(cpuLoadString: String): SystemInfo {
            return try {
                SystemInfo(cpuLoad = cpuLoadString.toDoubleOrNull())
            } catch (e: Exception) {
                SystemInfo()
            }
        }

        fun parseDiskLoad(diskLoadString: String): SystemInfo {
            return try {
                SystemInfo(diskUsage = diskLoadString.toDoubleOrNull())
            } catch (e: Exception) {
                SystemInfo()
            }
        }

        fun parseMemoryUsage(memoryString: String): SystemInfo {
            return try {
                val parts = memoryString.split("/")
                val usedMemory = parts.getOrNull(0)?.trim()?.toDoubleOrNull()?.toLong()
                val totalMemory = parts.getOrNull(1)?.trim()?.toDoubleOrNull()?.toLong()
                SystemInfo(usedMemory = usedMemory, totalMemory = totalMemory)
            } catch (e: Exception) {
                SystemInfo()
            }
        }
    }
}