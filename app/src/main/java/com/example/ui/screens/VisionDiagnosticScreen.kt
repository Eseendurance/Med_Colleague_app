package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.MedColleagueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionDiagnosticScreen(
    viewModel: MedColleagueViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnalyzing by viewModel.isAnalyzingVision.collectAsState()
    val visionResult by viewModel.visionResult.collectAsState()

    var selectedSampleType by remember { mutableStateOf("ECG Rhythm Strip") }
    var userPrompt by remember { mutableStateOf("Analyze this 12-Lead ECG. Identify acute ST elevation, vascular territory, and immediate management.") }
    var isCustomPhotoAttached by remember { mutableStateOf(false) }

    val sampleImages = listOf(
        "ECG Rhythm Strip" to "🫀 12-Lead ECG (Inferior STEMI)",
        "Chest X-Ray" to "🫁 Chest Radiograph (Lobar Pneumonia)",
        "Skin Rash" to "🩺 Skin Lesion (Erythema Migrans / Lyme)",
        "Lab ABG Report" to "🧪 Arterial Blood Gas (ABG Panel)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Gemini AI Vision Diagnostic Studio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Instant Clinical Image & Diagram Analysis",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("vision_drawer_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearVisionResult() },
                        modifier = Modifier.testTag("clear_vision_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear Analysis")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier.testTag("vision_diagnostic_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Camera / Image Attachment Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("vision_input_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Capture or Select Clinical Image",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Use camera to photograph ECGs, X-Rays, Derm lesions, or lab reports",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sample Clinical Image Presets
                        Text(
                            text = "Select Clinical Image Case Preset (1-Click Test):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sampleImages.size) { index ->
                                val (typeKey, label) = sampleImages[index]
                                val selected = (selectedSampleType == typeKey && !isCustomPhotoAttached)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        selectedSampleType = typeKey
                                        isCustomPhotoAttached = false
                                        userPrompt = when (typeKey) {
                                            "ECG Rhythm Strip" -> "Analyze this 12-Lead ECG. Identify acute ST elevation, vascular territory, and immediate management."
                                            "Chest X-Ray" -> "Analyze this Chest Radiograph. Identify opacification, air bronchograms, and pneumonia severity."
                                            "Skin Rash" -> "Analyze this dermatological skin lesion. Provide differential diagnosis and antibiotic protocol."
                                            else -> "Analyze this ABG lab panel. Calculate anion gap, primary disorder, and compensation."
                                        }
                                    }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Camera capture button simulation
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { isCustomPhotoAttached = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCustomPhotoAttached) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Camera Capture")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isCustomPhotoAttached) "Photo Captured ✓" else "Take Photo (Camera)", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = userPrompt,
                            onValueChange = { userPrompt = it },
                            label = { Text("Clinical Analysis Prompt") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("vision_prompt_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                // Dummy base64 placeholder or real vision trigger
                                val dummyBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
                                viewModel.analyzeVisionImage(dummyBase64, userPrompt, selectedSampleType)
                            },
                            enabled = !isAnalyzing,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("analyze_vision_button")
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing Image via Gemini Multimodal Vision...")
                            } else {
                                Icon(Icons.Default.Psychology, contentDescription = "Analyze")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyze Image with Gemini Vision AI", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Results Display Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                visionResult?.let { resultText ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("vision_result_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "AI DIAGNOSTIC REPORT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini 1.5 Multimodal Analysis",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = resultText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
