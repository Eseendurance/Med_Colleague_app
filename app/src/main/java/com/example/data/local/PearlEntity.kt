package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_yield_pearls")
data class PearlEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val specialty: String, // e.g. Cardiology, Pulmonology, Neurology
    val concept: String, // Short mechanism or key diagnosis
    val highYieldPearl: String, // The critical exam/clinical takeaway
    val moaOrGuideline: String, // Mechanism of action or 1st-line recommendation
    val isSaved: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val nextReviewDate: Long = System.currentTimeMillis(),
    val repetitionIntervalDays: Int = 1,
    val easeFactor: Float = 2.5f,
    val reviewCount: Int = 0,
    val lastReviewedDate: Long = 0L
)

