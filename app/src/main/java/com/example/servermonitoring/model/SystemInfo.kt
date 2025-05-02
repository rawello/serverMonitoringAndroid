package com.example.servermonitoring.model

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

        fun parseMemoryUsage(memoryString: String): SystemInfo {
            return try {
                val parts = memoryString.split("/")
                val usedMemory = parts.getOrNull(0)?.trim()?.toLongOrNull()
                val totalMemory = parts.getOrNull(1)?.trim()?.toLongOrNull()
                SystemInfo(usedMemory = usedMemory, totalMemory = totalMemory)
            } catch (e: Exception) {
                SystemInfo()
            }
        }
    }
}