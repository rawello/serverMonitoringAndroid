package com.example.servermonitoring.network

import com.example.servermonitoring.model.Container
import com.example.servermonitoring.model.SystemInfo
import retrofit2.http.*

interface ServerApiService {

    @GET("/docker/containers")
    suspend fun getContainers(): List<Container>

    @POST("/docker/{containerId}/start")
    suspend fun startContainer(@Path("containerId") containerId: String)

    @POST("/docker/{containerId}/stop")
    suspend fun stopContainer(@Path("containerId") containerId: String)

    @POST("/docker/{containerId}/restart")
    suspend fun restartContainer(@Path("containerId") containerId: String)

    @GET("/system/uptime")
    suspend fun getUptime(): SystemInfo

    @GET("/system/cpu-load")
    suspend fun getCpuLoad(): SystemInfo

    @GET("/system/memory")
    suspend fun getMemoryUsage(): SystemInfo

    @GET("/system/disk-usage")
    suspend fun getDiskUsage(): SystemInfo

    @GET("/docker/{containerId}/logs")
    suspend fun getContainerLogs(@Path("containerId") containerId: String): String
}