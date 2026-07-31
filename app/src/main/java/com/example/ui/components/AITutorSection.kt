package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MedColleagueViewModel
import kotlinx.coroutines.launch
import java.util.Locale

data class TutorMessage(
    val sender: String, // "tutor" or "student"
    val text: String,
    val timestamp: String = "Just now"
)

@Composable
fun AITutorSection(
    viewModel: MedColleagueViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var allowAIToTeach by remember { mutableStateOf(true) }
    var selectedMode by remember { mutableStateOf("Video") } // "Video" or "Audio"
    var documentInputText by remember { mutableStateOf("") }
    var uploadedFileName by remember { mutableStateOf<String?>(null) }

    var isTeachingActive by remember { mutableStateOf(false) }
    var isGeneratingTutoring by remember { mutableStateOf(false) }
    var isPlayingAudioVideo by remember { mutableStateOf(false) }

    var studentFollowUpInput by remember { mutableStateOf("") }
    var tutorDialogueHistory by remember {
        mutableStateOf(
            listOf(
                TutorMessage(
                    sender = "tutor",
                    text = "Hello! I am Dr. Ese, your AI Medical Educator. Upload a PDF, case note, or exam question below and allow me to teach you step-by-step!"
                )
            )
        )
    }

    // Android TextToSpeech setup for real voice tutoring
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsInitialized by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isTtsInitialized = true
            }
        }
        ttsEngine = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Animated HUD Radar Scan Pulse Effect
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .testTag("ai_tutor_section_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "AI Tutor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "🎓 AI Interactive Video & Audio Tutor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Dr. Ese • Senior Medical AI Board Educator",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Allow AI to Teach Me Toggle Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (allowAIToTeach) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Teach Toggle",
                        tint = if (allowAIToTeach) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Allow AI Tutor to Teach Me",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (allowAIToTeach) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Generates interactive video & audio walkthroughs upon uploading questions/PDFs",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = allowAIToTeach,
                        onCheckedChange = { allowAIToTeach = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("allow_ai_teach_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PDF & Question Upload Section
            Text(
                text = "1. Upload PDF Document or Enter Exam Question:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Pre-loaded Sample PDFs
            Text(
                text = "Sample Medical PDFs (Tap to Load):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val samples = listOf(
                    "📄 ECG_Arrhythmias_Case.pdf" to "A 64-year-old male presents with sudden palpitations and dizziness. ECG shows irregular narrow-complex tachycardia without distinct p waves at a rate of 140 bpm. Blood pressure 110/70. What is the management?",
                    "📄 Acute_Renal_AcidBase.pdf" to "A 52-year-old female with long-standing diabetes presents with confusion. Arterial blood gas: pH 7.22, PaCO2 24 mmHg, HCO3 10 mEq/L. Serum sodium 138, chloride 100, potassium 5.2. Calculate anion gap and diagnose.",
                    "📄 Heart_Failure_GDMT.pdf" to "A 58-year-old male with HFrEF (LVEF 30%) remains symptomatic on Lisinopril and Metoprolol Succinate. Serum potassium 4.2, creatinine 1.1. What is the best next guideline-directed medication addition?"
                )
                items(samples) { (fileName, textSnippet) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.clickable {
                            uploadedFileName = fileName
                            documentInputText = textSnippet
                        }
                    ) {
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = documentInputText,
                onValueChange = { documentInputText = it },
                placeholder = { Text("Paste medical case, PDF contents, or question text here...", fontSize = 12.sp) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .testTag("tutor_document_input")
            )

            uploadedFileName?.let { fname ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Uploaded",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Loaded Document: $fname",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tutor Mode Selector (Video vs Audio)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedMode == "Video") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMode = "Video" }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Mode",
                            tint = if (selectedMode == "Video") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎥 AI Video Tutor",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMode == "Video") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedMode == "Audio") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMode = "Audio" }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Audio Mode",
                            tint = if (selectedMode == "Audio") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎙️ AI Audio Tutor",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMode == "Audio") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Start Teaching Button
            Button(
                onClick = {
                    if (documentInputText.isBlank()) return@Button
                    isGeneratingTutoring = true
                    coroutineScope.launch {
                        val explanation = viewModel.getAITutorResponse(documentInputText)

                        tutorDialogueHistory = tutorDialogueHistory + TutorMessage(
                            sender = "tutor",
                            text = explanation
                        )
                        isGeneratingTutoring = false
                        isTeachingActive = true
                        isPlayingAudioVideo = true

                        // Trigger TextToSpeech voice reading if enabled
                        if (isTtsInitialized && ttsEngine != null) {
                            ttsEngine?.speak("Welcome! Let's break down this case together.", TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                },
                enabled = documentInputText.isNotBlank() && !isGeneratingTutoring,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_ai_tutoring_button")
            ) {
                if (isGeneratingTutoring) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dr. Maya is Analyzing PDF & Preparing Walkthrough...")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start AI Interactive Walkthrough", fontWeight = FontWeight.Bold)
                }
            }

            // Interactive Video / Audio Tutor Player Area
            AnimatedVisibility(visible = isTeachingActive) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    // Video Avatar Display Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F172A))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                            .scale(if (isPlayingAudioVideo) pulseScale else 1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ai_tutor_avatar),
                            contentDescription = "AI Tutor Dr. Maya",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        // Futuristic Overlay HUD Elements
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlayingAudioVideo) Color(0xFF00E676) else Color.Yellow)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPlayingAudioVideo) "LIVE TUTORING • 1080p HD" else "PAUSED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Audio Wave Spectrum Bar Overlay
                        if (isPlayingAudioVideo) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                listOf(14, 24, 18, 28, 20, 12, 26, 16).forEach { heightDp ->
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(heightDp.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }

                        // Play/Pause Overlay Controls
                        IconButton(
                            onClick = {
                                isPlayingAudioVideo = !isPlayingAudioVideo
                                if (!isPlayingAudioVideo) {
                                    ttsEngine?.stop()
                                } else if (isTtsInitialized) {
                                    ttsEngine?.speak("Continuing clinical explanation.", TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(56.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudioVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Pause",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive Chat Dialogue Thread with Dr. Maya
                    Text(
                        text = "💬 Interactive Question & Answer Section:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        tutorDialogueHistory.forEach { msg ->
                            val isTutor = msg.sender == "tutor"
                            Row(
                                horizontalArrangement = if (isTutor) Arrangement.Start else Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isTutor) 2.dp else 12.dp,
                                        bottomEnd = if (isTutor) 12.dp else 2.dp
                                    ),
                                    color = if (isTutor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isTutor) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Student Quick Question Chips
                    Text(
                        text = "Suggested Questions for Dr. Maya:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val quickQuestions = listOf(
                            "Why is option B incorrect?",
                            "Can you explain the mechanism in simpler terms?",
                            "What is the first-line diagnostic test?",
                            "Give me a high-yield memory trick for this concept."
                        )
                        items(quickQuestions) { qText ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                modifier = Modifier.clickable {
                                    studentFollowUpInput = qText
                                }
                            ) {
                                Text(
                                    text = "💡 $qText",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Follow-Up Q&A Input Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = studentFollowUpInput,
                            onValueChange = { studentFollowUpInput = it },
                            placeholder = { Text("Ask Dr. Maya a question...", fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tutor_student_qa_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (studentFollowUpInput.isBlank()) return@IconButton
                                val studentText = studentFollowUpInput
                                studentFollowUpInput = ""

                                tutorDialogueHistory = tutorDialogueHistory + TutorMessage(
                                    sender = "student",
                                    text = studentText
                                )

                                coroutineScope.launch {
                                    val tutorAns = viewModel.getAITutorResponse(documentInputText, studentText)

                                    tutorDialogueHistory = tutorDialogueHistory + TutorMessage(
                                        sender = "tutor",
                                        text = tutorAns
                                    )

                                    if (isTtsInitialized) {
                                        ttsEngine?.speak("Great question! Let me explain.", TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Question",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
