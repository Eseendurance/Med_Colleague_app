package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Quiz
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MedColleagueViewModel
import com.example.ui.components.AITutorSection
import com.example.ui.components.CaseVignetteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPrepScreen(
    viewModel: MedColleagueViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vignettes by viewModel.caseVignettes.collectAsState()
    val isGenerating by viewModel.isGeneratingQuestions.collectAsState()

    var selectedExamStyle by remember { mutableStateOf("USMLE Step 1") }
    var selectedTopic by remember { mutableStateOf("Cardiology") }
    var customTopicInput by remember { mutableStateOf("") }

    val examStyles = listOf(
        "USMLE Step 1",
        "USMLE Step 2 CK",
        "NCLEX-RN",
        "MRCP",
        "MCAT",
        "Shelf Exam",
        "Short Answer / Essay"
    )

    val popularTopics = listOf(
        "Cardiology",
        "Pulmonology",
        "Neurology",
        "Gastroenterology",
        "Endocrinology",
        "Nephrology",
        "Infectious Disease",
        "Emergency Medicine"
    )

    // Persistent Board Exam Timer Widget State
    var secondsRemaining by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, secondsRemaining) {
        if (isTimerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        }
    }
    val completedCases = vignettes.filter { it.isCompleted }
    val correctCases = completedCases.filter { it.userSelectedIndex == it.correctIndex }
    val scorePercentage = if (completedCases.isNotEmpty()) {
        (correctCases.size * 100) / completedCases.size
    } else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Interactive Exam Studio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Targeted Vignette Generation & Automatic Marking", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("exam_prep_drawer_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Drawer")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearVignettes() },
                        modifier = Modifier.testTag("clear_vignettes_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Questions")
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
        modifier = modifier.testTag("exam_prep_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Persistent Floating Timer Widget (Board Exam Time Constraint Simulation)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (secondsRemaining <= 10 && isTimerRunning) Color(0xFFFFF0F0) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("floating_timer_widget")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Exam Timer",
                            tint = if (secondsRemaining <= 10 && isTimerRunning) Color.Red else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⏱️ BOARD EXAM TIME PRESSURE WIDGET",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (secondsRemaining <= 10 && isTimerRunning) Color.Red else MaterialTheme.colorScheme.primary
                            )
                            val minutes = secondsRemaining / 60
                            val secs = secondsRemaining % 60
                            val timeStr = String.format("%02d:%02d", minutes, secs)
                            Text(
                                text = "Time Remaining: $timeStr",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (secondsRemaining <= 10 && isTimerRunning) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Play/Pause button
                        IconButton(
                            onClick = { isTimerRunning = !isTimerRunning },
                            modifier = Modifier.testTag("timer_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTimerRunning) "Pause Timer" else "Start Timer",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Reset button (+60s)
                        IconButton(
                            onClick = {
                                secondsRemaining = 60
                                isTimerRunning = false
                            },
                            modifier = Modifier.testTag("timer_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Reset Timer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Interactive AI Video & Audio Tutor Section (Dr. Maya)
            item {
                AITutorSection(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Exam Style Configuration Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("exam_config_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = "Exam Configuration",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EXAM STYLE & SUBJECT GENERATOR",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "1. Select Exam Format / Board Target:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Exam Styles Selector Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(examStyles) { style ->
                                val selected = (selectedExamStyle == style)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.clickable { selectedExamStyle = style }
                                ) {
                                    Text(
                                        text = style,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "2. Select Clinical Specialty or Custom Topic:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Popular Topics Selector
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(popularTopics) { topic ->
                                val selected = (selectedTopic == topic && customTopicInput.isBlank())
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.clickable {
                                        selectedTopic = topic
                                        customTopicInput = ""
                                    }
                                ) {
                                    Text(
                                        text = topic,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customTopicInput,
                            onValueChange = { customTopicInput = it },
                            placeholder = { Text("Or enter custom topic (e.g. Acid-Base Disorders, Shock)...", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_topic_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val activeTopic = if (customTopicInput.isNotBlank()) customTopicInput else selectedTopic

                        Button(
                            onClick = {
                                viewModel.generateCustomExamQuestions(
                                    examStyle = selectedExamStyle,
                                    topic = activeTopic
                                )
                            },
                            enabled = !isGenerating,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_exam_button")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating $selectedExamStyle Set...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Generate")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generate $selectedExamStyle Questions on '$activeTopic'",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Performance Trends & Mastery Visualizer Card
            if (completedCases.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("performance_trends_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = "Mastery",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Exam Performance & Topic Mastery Trends",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Score Progress Bar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Overall Accuracy: $scorePercentage%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${correctCases.size} of ${completedCases.size} Questions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Custom Visual Progress Bar
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    val progressWeight = (scorePercentage / 100f).coerceIn(0.01f, 1f)
                                    Spacer(
                                        modifier = Modifier
                                            .weight(progressWeight)
                                            .fillMaxSize()
                                            .background(
                                                if (scorePercentage >= 70) Color(0xFF2E7D32) else Color(0xFFE65100)
                                            )
                                    )
                                    if (progressWeight < 1f) {
                                        Spacer(modifier = Modifier.weight(1f - progressWeight))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Confidence vs. Accuracy Calibration Section
                            Text(
                                text = "Confidence vs. Accuracy Calibration:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val overconfidentGaps = completedCases.count {
                                it.confidenceLevel == "High" && it.userSelectedIndex != it.correctIndex
                            }
                            val masteredConcepts = completedCases.count {
                                it.confidenceLevel == "High" && it.userSelectedIndex == it.correctIndex
                            }
                            val hesitantStrengths = completedCases.count {
                                (it.confidenceLevel == "Low" || it.confidenceLevel == "Moderate") && it.userSelectedIndex == it.correctIndex
                            }
                            val unmasteredConcepts = completedCases.count {
                                (it.confidenceLevel == "Low" || it.confidenceLevel == "Moderate") && it.userSelectedIndex != it.correctIndex
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (overconfidentGaps > 0) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "⚠️ Overconfident Gaps", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                        Text(text = "$overconfidentGaps Qs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                        Text(text = "High Confidence / Wrong", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "🏆 Solid Mastery", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        Text(text = "$masteredConcepts Qs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        Text(text = "High Confidence / Right", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFF3E0),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "⚡ Hesitant Strengths", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                        Text(text = "$hesitantStrengths Qs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                        Text(text = "Low-Mod Confidence / Right", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "📖 Review Required", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "$unmasteredConcepts Qs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "Low-Mod Confidence / Wrong", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Specialty Mastery Breakdown:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Group by specialty
                            val specialtyGroup = completedCases.groupBy { it.specialty }
                            specialtyGroup.forEach { (specialty, cases) ->
                                val specCorrect = cases.count { it.userSelectedIndex == it.correctIndex }
                                val specAccuracy = (specCorrect * 100) / cases.size
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = specialty,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.width(120.dp)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                    ) {
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            val w = (specAccuracy / 100f).coerceIn(0.01f, 1f)
                                            Spacer(
                                                modifier = Modifier
                                                    .weight(w)
                                                    .fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                            if (w < 1f) Spacer(modifier = Modifier.weight(1f - w))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$specAccuracy%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Vignettes List
            if (vignettes.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = "Exam Studio",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Active Questions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select an exam format (e.g. USMLE Step 1, NCLEX) and topic above, then tap 'Generate' to create a customized practice question set with automatic marking and step-by-step pathophysiologic rationales!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(vignettes) { case ->
                    CaseVignetteCard(
                        vignette = case,
                        onOptionSelected = { selectedIdx, confidence ->
                            viewModel.selectVignetteOption(case, selectedIdx, confidence)
                        }
                    )
                }
            }
        }
    }
}
