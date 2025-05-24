package com.example.eventplanner.data

data class Event (
    val id: String = java.util.UUID.randomUUID().toString(),
    var title: String,
    var date: Long,
    var description: String
)