package com.example.servermonitoring.model

data class SystemInfo(
    val uptime: Long? = null,
    val cpu_load: Double? = null,
    val used_memory: Long? = null,
    val total_memory: Long? = null,
    val disk_usage: Double? = null
)