package com.example.servermonitoring.network

import com.example.servermonitoring.model.Container
import retrofit2.http.*

interface ServerApiService {

    @GET("/docker/containers")
    suspend fun getContainers(): List<Container>

    @POST("/docker/containers/{containerId}/start")
    suspend fun startContainer(@Path("containerId") containerId: String)

    @POST("/docker/containers/{containerId}/stop")
    suspend fun stopContainer(@Path("containerId") containerId: String)

    @GET("/docker/containers/postgres")
    suspend fun getPostgresContainers(): List<Container>

    @GET("/system/uptime")
    suspend fun getUptime(): Long

    @GET("/system/cpu-load")
    suspend fun getCpuLoad(): Double

    @GET("/system/memory")
    suspend fun getMemoryUsage(): String

    @GET("/system/disk-usage")
    suspend fun getDiskUsage(): Double

    @GET("/docker/containers/{containerId}/logs")
    suspend fun getContainerLogs(@Path("containerId") containerId: String): String

    @GET("/docker/containers/{containerId}/databases")
    suspend fun getDatabases(@Path("containerId") containerId: String): List<String>

    @GET("/docker/containers/{containerId}/tables")
    suspend fun getTables(
        @Path("containerId") containerId: String,
        @Query("dbName") dbName: String
    ): List<String>

    @GET("/docker/containers/{containerId}/tables/{tableName}/columns")
    suspend fun getTableColumns(
        @Path("containerId") containerId: String,
        @Query("dbName") dbName: String,
        @Path("tableName") tableName: String
    ): List<String>

    @POST("/docker/containers/{containerId}/query")
    suspend fun executeQuery(
        @Path("containerId") containerId: String,
        @Query("dbName") dbName: String,
        @Body query: String
    ): List<Map<String, Any>>

    @POST("/docker/containers/{containerId}/complex-query")
    suspend fun executeComplexQuery(
        @Path("containerId") containerId: String,
        @Query("dbName") dbName: String,
        @Body query: String
    ): List<Map<String, Any>>
}