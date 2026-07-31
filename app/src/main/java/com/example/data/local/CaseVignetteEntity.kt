package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_vignettes")
data class CaseVignetteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val specialty: String,
    val vignetteText: String,
    val optionsPipeSeparated: String, // e.g. "A. Option 1|B. Option 2|C. Option 3|D. Option 4"
    val correctIndex: Int,
    val rationale: String,
    val highYieldPearl: String,
    val isCompleted: Boolean = false,
    val userSelectedIndex: Int = -1,
    val confidenceLevel: String = "Unassessed" // "High", "Moderate", "Low", "Unassessed"
)
