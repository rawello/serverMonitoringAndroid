package com.example.servermonitoring.network

import com.example.servermonitoring.model.Container
import com.example.servermonitoring.model.SystemInfo
import retrofit2.http.*

interface ServerApiService {

    @GET("/docker/containers")
    suspend fun getContainers(): List<String>

    @POST("/docker/{containerId}/start")
    suspend fun startContainer(@Path("containerId") containerId: String)

    @POST("/docker/{containerId}/stop")
    suspend fun stopContainer(@Path("containerId") containerId: String)

    @POST("/docker/{containerId}/restart")
    suspend fun restartContainer(@Path("containerId") containerId: String)

    @GET("/system/uptime")
    suspend fun getUptime(): String

    @GET("/system/cpu-load")
    suspend fun getCpuLoad(): String

    @GET("/system/memory")
    suspend fun getMemoryUsage(): String

    @GET("/system/disk")
    suspend fun getDiskUsage(): String

    @GET("/docker/{containerId}/logs")
    suspend fun getContainerLogs(@Path("containerId") containerId: String): String
}