package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = "default_session",
    val sender: String, // "user" or "medcolleague"
    val content: String,
    val isEmergencyAlert: Boolean = false,
    val userRole: String = "STUDENT", // "STUDENT" or "CLINICIAN"
    val timestamp: Long = System.currentTimeMillis()
)
