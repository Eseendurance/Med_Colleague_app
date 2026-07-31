package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.local.ChatMessageEntity
import com.example.ui.theme.HighYieldAmberContainer
import com.example.ui.theme.HighYieldAmberOnContainer

import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import com.example.ui.utils.ExportUtils

@Composable
fun MedicalResponseCard(
    message: ChatMessageEntity,
    onSavePearl: (title: String, pearlText: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pearlSaved by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("medical_response_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Senior Attending Physician Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "MedColleague",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MedColleague",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Senior Attending Clinical Educator",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (message.userRole == "STUDENT") "🎓 Student Mode" else "🩺 Clinician Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Content with formatted sections
            val contentLines = message.content.split("\n")
            var inPearlSection = false
            var pearlTitle = "High-Yield Medical Pearl"
            var pearlBody = ""

            contentLines.forEach { line ->
                when {
                    line.startsWith("### Key Takeaway") || line.startsWith("### High-Yield Pearl") || line.contains("High-Yield Pearl") -> {
                        inPearlSection = true
                        pearlTitle = line.replace("#", "").trim()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(HighYieldAmberContainer)
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Pearl",
                                tint = HighYieldAmberOnContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pearlTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = HighYieldAmberOnContainer
                            )
                        }
                    }
                    line.startsWith("###") -> {
                        inPearlSection = false
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = line.replace("###", "").trim(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    }
                    line.startsWith("- ") || line.startsWith("* ") -> {
                        if (inPearlSection) pearlBody += "$line\n"
                        Row(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
                            Text(
                                text = "• ",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = line.substring(2),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    line.isBlank() -> {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    else -> {
                        if (inPearlSection) pearlBody += "$line\n"
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Medical Terminology & Abbreviation Dictionary Chips
            val foundTerms = remember(message.content) {
                com.example.ui.utils.MedicalAbbreviationDictionary.findTermsInText(message.content)
            }
            var activeTermDialog by remember { mutableStateOf<com.example.ui.utils.MedicalTermDefinition?>(null) }

            if (foundTerms.isNotEmpty()) {
                Text(
                    text = "📖 Detected Medical Terms (Tap for Definition):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    items(foundTerms.size) { idx ->
                        val termDef = foundTerms[idx]
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier
                                .clickable { activeTermDialog = termDef }
                                .testTag("term_chip_${termDef.term.lowercase()}")
                        ) {
                            Text(
                                text = "💡 ${termDef.term}: ${termDef.expansion}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Definition Modal Dialog
            activeTermDialog?.let { termDef ->
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { activeTermDialog = null },
                    title = {
                        Column {
                            Text(
                                text = "📖 ${termDef.term}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = termDef.expansion,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    text = {
                        Column {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "Category: ${termDef.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = termDef.definition,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HighYieldAmberContainer
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "⚡ High-Yield Clinical Pearl:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = HighYieldAmberOnContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = termDef.highYieldPearl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HighYieldAmberOnContainer
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        androidx.compose.material3.TextButton(
                            onClick = { activeTermDialog = null }
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Action Row to save pearl and export explanation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Save or Export Explanation",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        ExportUtils.shareAndExportText(
                            context = context,
                            title = "MedColleague Clinical Explanation",
                            content = message.content
                        )
                    },
                    modifier = Modifier.testTag("export_explanation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export Explanation",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        if (!pearlSaved) {
                            val savedTitle = if (pearlTitle.isNotBlank()) pearlTitle else "Clinical High-Yield Pearl"
                            val savedContent = if (pearlBody.isNotBlank()) pearlBody else message.content.take(200)
                            onSavePearl(savedTitle, savedContent)
                            pearlSaved = true
                        }
                    },
                    modifier = Modifier.testTag("save_pearl_button")
                ) {
                    Icon(
                        imageVector = if (pearlSaved) Icons.Default.Check else Icons.Default.BookmarkAdd,
                        contentDescription = "Save Pearl",
                        tint = if (pearlSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
