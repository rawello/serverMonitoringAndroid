package com.example.servermonitoring.model

import com.google.gson.annotations.SerializedName

data class SystemInfo(
    @SerializedName("uptime") val uptimeRaw: String? = null,
    @SerializedName("cpu_load") val cpuLoad: Double? = null,
    @SerializedName("memory_usage") val memoryUsage: Double? = null,
    @SerializedName("disk_usage") val diskUsage: Double? = null
) {
    val uptimeSeconds: Long
        get() = uptimeRaw?.toLongOrNull() ?: 0L
}