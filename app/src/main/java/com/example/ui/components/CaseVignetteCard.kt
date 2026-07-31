package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CaseVignetteEntity
import com.example.ui.theme.HighYieldAmberContainer
import com.example.ui.theme.HighYieldAmberOnContainer

import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import com.example.ui.utils.ExportUtils

/**
 * Reusable UI component to display clinical case vignettes
 * with distinct sections for:
 * 1. Patient Presentation
 * 2. Pathophysiology & Diagnostic Reasoning
 * 3. Takeaway Pearls & Guidelines
 */
@Composable
fun CaseVignetteCard(
    vignette: CaseVignetteEntity,
    onOptionSelected: (optionIndex: Int, confidenceLevel: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val options = vignette.optionsPipeSeparated.split("|")
    var selectedConfidence by remember { mutableStateOf("High") }
    var pendingOptionIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("case_vignette_card_${vignette.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Specialty Tag & Result Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "📋 ${vignette.specialty}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        val exportText = """
                            CLINICAL CASE VIGNETTE: ${vignette.title}
                            Specialty: ${vignette.specialty}
                            Confidence Level: ${vignette.confidenceLevel}
                            
                            PATIENT PRESENTATION:
                            ${vignette.vignetteText}
                            
                            OPTIONS:
                            ${options.joinToString("\n")}
                            
                            SENIOR ATTENDING RATIONALE:
                            ${vignette.rationale}
                            
                            BOARD PEARL & GUIDELINES:
                            ${vignette.highYieldPearl}
                        """.trimIndent()
                        ExportUtils.shareAndExportText(context, vignette.title, exportText)
                    },
                    modifier = Modifier.size(32.dp).testTag("export_vignette_button_${vignette.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export Vignette",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (vignette.isCompleted) {
                    val isCorrect = (vignette.userSelectedIndex == vignette.correctIndex)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = "Grading Result",
                                tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCorrect) "Correct (+100%)" else "Incorrect",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vignette Title
            Text(
                text = vignette.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SECTION 1: Patient Presentation Stem
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "Presentation",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PATIENT PRESENTATION & CLINICAL STEM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = vignette.vignetteText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confidence Level Selector BEFORE submitting
            if (!vignette.isCompleted) {
                Text(
                    text = "Self-Assess Your Confidence Level First:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("High", "Moderate", "Low").forEach { level ->
                        val selected = (selectedConfidence == level)
                        val badgeColor = when (level) {
                            "High" -> Color(0xFF2E7D32)
                            "Moderate" -> Color(0xFFE65100)
                            else -> Color(0xFFC62828)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) badgeColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedConfidence = level }
                                .testTag("confidence_chip_${level.lowercase()}")
                        ) {
                            Text(
                                text = when (level) {
                                    "High" -> "🟢 High"
                                    "Moderate" -> "🟡 Moderate"
                                    else -> "🔴 Low"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // Display Self-Assessed Confidence Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Your Self-Assessed Confidence: ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (vignette.confidenceLevel) {
                            "High" -> Color(0xFFE8F5E9)
                            "Moderate" -> Color(0xFFFFF3E0)
                            else -> Color(0xFFFFEBEE)
                        }
                    ) {
                        Text(
                            text = when (vignette.confidenceLevel) {
                                "High" -> "🟢 High Confidence"
                                "Moderate" -> "🟡 Moderate Confidence"
                                else -> "🔴 Low Confidence"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (vignette.confidenceLevel) {
                                "High" -> Color(0xFF2E7D32)
                                "Moderate" -> Color(0xFFE65100)
                                else -> Color(0xFFC62828)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Multiple Choice Options / User Input
            Text(
                text = if (vignette.isCompleted) "Submitted Answer & Answer Key:" else "Select Best Next Step or Diagnosis:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            options.forEachIndexed { index, optionText ->
                val isSelected = (vignette.userSelectedIndex == index)
                val isCorrectAnswer = (index == vignette.correctIndex)

                val borderThickness = if (isSelected || (vignette.isCompleted && isCorrectAnswer)) 2.dp else 1.dp
                val borderColor = when {
                    vignette.isCompleted && isCorrectAnswer -> Color(0xFF2E7D32)
                    vignette.isCompleted && isSelected && !isCorrectAnswer -> Color(0xFFC62828)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                }

                val containerBg = when {
                    vignette.isCompleted && isCorrectAnswer -> Color(0xFFE8F5E9)
                    vignette.isCompleted && isSelected && !isCorrectAnswer -> Color(0xFFFFEBEE)
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(containerBg)
                        .border(borderThickness, borderColor, RoundedCornerShape(12.dp))
                        .clickable(enabled = !vignette.isCompleted) {
                            onOptionSelected(index, selectedConfidence)
                        }
                        .padding(12.dp)
                        .testTag("vignette_option_${vignette.id}_$index")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || (vignette.isCompleted && isCorrectAnswer)) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (vignette.isCompleted && isCorrectAnswer) {
                            Text(
                                text = "✓ Correct",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        } else if (vignette.isCompleted && isSelected && !isCorrectAnswer) {
                            Text(
                                text = "✗ Selected Choice",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            // SECTION 2 & SECTION 3: Rationale & Takeaway Pearls (Visible after completion)
            AnimatedVisibility(visible = vignette.isCompleted) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // SECTION 2: Pathophysiology & Diagnostic Reasoning
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Pathophysiology",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PATHOPHYSIOLOGY & DIAGNOSTIC REASONING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = vignette.rationale,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // SECTION 3: Takeaway Pearls & Guidelines
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HighYieldAmberContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Takeaway Pearl",
                                    tint = HighYieldAmberOnContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TAKEAWAY PEARL & CLINICAL GUIDELINES",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = HighYieldAmberOnContainer,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = vignette.highYieldPearl,
                                style = MaterialTheme.typography.bodyMedium,
                                color = HighYieldAmberOnContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
