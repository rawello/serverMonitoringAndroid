package com.example.servermonitoring.model

data class Container(
    val id: String,
    val names: List<String>,
    val image: String,
    val status: String,
    val state: String
)