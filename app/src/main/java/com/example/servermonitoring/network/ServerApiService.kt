package com.example.servermonitoring.network

import com.example.servermonitoring.model.Container
import com.example.servermonitoring.model.SystemInfo
import retrofit2.http.*

interface ServerApiService {

    @GET("/docker/containers")
    suspend fun getContainers(): List<Container>

    @POST("/docker/start/{id}")
    suspend fun startContainer(@Path("id") containerId: String): DockerResponse

    @POST("/docker/stop/{id}")
    suspend fun stopContainer(@Path("id") containerId: String): DockerResponse

    @POST("/docker/restart/{id}")
    suspend fun restartContainer(@Path("id") containerId: String): DockerResponse

    @GET("/system/uptime")
    suspend fun getUptime(): SystemInfo

    @GET("/system/cpu-load")
    suspend fun getCpuLoad(): SystemInfo

    @GET("/system/memory")
    suspend fun getMemoryUsage(): SystemInfo

    @GET("/docker/logs/{id}")
    suspend fun getContainerLogs(@Path("id") containerId: String): String
}

data class DockerResponse(
    val success: Boolean,
    val message: String
)