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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MedColleagueViewModel
import com.example.ui.theme.HighYieldAmberContainer
import com.example.ui.theme.HighYieldAmberOnContainer
import com.example.ui.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
    viewModel: MedColleagueViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pearls by viewModel.pearls.collectAsState()
    var selectedCalculator by remember { mutableStateOf(0) } // 0: CHA2DS2-VASc, 1: CURB-65, 2: Cockcroft-Gault CrCl, 3: Multi-Platform Sync

    // CHA2DS2-VASc State
    var chaAge75 by remember { mutableStateOf(false) }
    var chaAge65 by remember { mutableStateOf(false) }
    var chaFemale by remember { mutableStateOf(false) }
    var chaChf by remember { mutableStateOf(false) }
    var chaHt2 by remember { mutableStateOf(false) }
    var chaStroke by remember { mutableStateOf(false) }
    var chaVascular by remember { mutableStateOf(false) }
    var chaDiabetes by remember { mutableStateOf(false) }

    // CURB-65 State
    var curbConfusion by remember { mutableStateOf(false) }
    var curbUrea by remember { mutableStateOf(false) }
    var curbRr by remember { mutableStateOf(false) }
    var curbBp by remember { mutableStateOf(false) }
    var curbAge65 by remember { mutableStateOf(false) }

    // Renal CrCl State
    var scrInput by remember { mutableStateOf("1.1") }
    var ageInput by remember { mutableStateOf("68") }
    var weightInput by remember { mutableStateOf("72") }
    var isRenalFemale by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Bedside Clinical Calculators", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Evidence-Based Decision Support & Dosing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("calculators_drawer_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier.testTag("calculators_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Calculator Selector Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val tabs = listOf("CHA₂DS₂-VASc", "CURB-65", "CrCl Renal Dosing", "💻 Multi-Platform Sync")
                    items(tabs.size) { index ->
                        val selected = (selectedCalculator == index)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCalculator = index }
                        ) {
                            Text(
                                text = tabs[index],
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            when (selectedCalculator) {
                0 -> {
                    // CHA2DS2-VASc
                    item {
                        var score = 0
                        if (chaAge75) score += 2
                        else if (chaAge65) score += 1
                        if (chaFemale) score += 1
                        if (chaChf) score += 1
                        if (chaHt2) score += 1
                        if (chaStroke) score += 2
                        if (chaVascular) score += 1
                        if (chaDiabetes) score += 1

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().testTag("cha2ds2_vasc_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "CHA₂DS₂-VASc Score for AFib Stroke Risk",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Assesses stroke risk in non-valvular atrial fibrillation to guide oral anticoagulation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                CalculatorCheckboxItem("Congestive Heart Failure (+1)", chaChf) { chaChf = it }
                                CalculatorCheckboxItem("Hypertension (+1)", chaHt2) { chaHt2 = it }
                                CalculatorCheckboxItem("Age ≥ 75 (+2)", chaAge75) {
                                    chaAge75 = it
                                    if (it) chaAge65 = false
                                }
                                CalculatorCheckboxItem("Age 65-74 (+1)", chaAge65) {
                                    chaAge65 = it
                                    if (it) chaAge75 = false
                                }
                                CalculatorCheckboxItem("Diabetes Mellitus (+1)", chaDiabetes) { chaDiabetes = it }
                                CalculatorCheckboxItem("Stroke / TIA / Thromboembolism (+2)", chaStroke) { chaStroke = it }
                                CalculatorCheckboxItem("Vascular Disease (Prior MI, PAD, Aortic plaque) (+1)", chaVascular) { chaVascular = it }
                                CalculatorCheckboxItem("Female Sex Category (+1)", chaFemale) { chaFemale = it }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (score >= 2) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "CHA₂DS₂-VASc SCORE: $score",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = if (score >= 2) Color(0xFFC62828) else Color(0xFF2E7D32)
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (score >= 2) Color(0xFFC62828) else Color(0xFF2E7D32)
                                            ) {
                                                Text(
                                                    text = if (score >= 2) "HIGH STROKE RISK" else "LOW / MODERATE RISK",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        val (annualRisk, recommendation) = when {
                                            score == 0 -> "0.2% / year" to "No antithrombotic therapy required (or Aspirin alone)."
                                            score == 1 -> "0.6% / year" to "Consider Oral Anticoagulant (DOAC/NOAC) or Aspirin based on clinical judgment."
                                            score == 2 -> "2.2% / year" to "Oral Anticoagulant (DOAC e.g. Apixaban, Rivaroxaban) strongly recommended."
                                            score == 3 -> "3.2% / year" to "Oral Anticoagulant (DOAC or Warfarin with INR 2.0-3.0) indicated."
                                            else -> "${(score * 1.5 + 1).coerceAtMost(15.2)}% / year" to "High-priority Oral Anticoagulation indicated. Assess HAS-BLED bleeding risk."
                                        }

                                        Text(text = "Adjusted Annual Stroke Risk: $annualRisk", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Senior Recommendation: $recommendation", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // CURB-65
                    item {
                        var score = 0
                        if (curbConfusion) score += 1
                        if (curbUrea) score += 1
                        if (curbRr) score += 1
                        if (curbBp) score += 1
                        if (curbAge65) score += 1

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().testTag("curb65_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "CURB-65 Pneumonia Severity Score",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Predicts mortality in Community-Acquired Pneumonia (CAP) to determine inpatient vs ICU admission.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                CalculatorCheckboxItem("Confusion (Abnormal mental status) (+1)", curbConfusion) { curbConfusion = it }
                                CalculatorCheckboxItem("Urea > 19 mg/dL (> 7 mmol/L / BUN > 20) (+1)", curbUrea) { curbUrea = it }
                                CalculatorCheckboxItem("Respiratory Rate ≥ 30 breaths/min (+1)", curbRr) { curbRr = it }
                                CalculatorCheckboxItem("Blood Pressure (SBP < 90 mmHg or DBP ≤ 60 mmHg) (+1)", curbBp) { curbBp = it }
                                CalculatorCheckboxItem("Age ≥ 65 years (+1)", curbAge65) { curbAge65 = it }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (score >= 3) Color(0xFFFFEBEE) else if (score >= 2) Color(0xFFFFF8E1) else Color(0xFFE8F5E9),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "CURB-65 SCORE: $score / 5",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (score >= 3) Color(0xFFC62828) else if (score >= 2) Color(0xFFF57F17) else Color(0xFF2E7D32)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        val (mortality, disposition) = when (score) {
                                            0 -> "< 1%" to "Low risk. Consider outpatient oral antibiotic therapy (e.g. Amoxicillin or Macrolide)."
                                            1 -> "2.7%" to "Low/Moderate risk. Outpatient vs brief observation ward."
                                            2 -> "6.8%" to "Moderate risk. Short-stay inpatient hospital ward admission recommended."
                                            3 -> "14%" to "Severe Pneumonia. Inpatient hospital admission required. Consider ICU assessment."
                                            else -> "> 27%" to "CRITICAL. Severe Pneumonia with high mortality. Immediate ICU admission & IV Empiric Coverage."
                                        }

                                        Text(text = "30-Day Mortality Risk: $mortality", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Clinical Triage: $disposition", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Cockcroft-Gault Renal Dosing
                    item {
                        val scr = scrInput.toFloatOrNull() ?: 1.0f
                        val age = ageInput.toIntOrNull() ?: 65
                        val weight = weightInput.toFloatOrNull() ?: 70.0f

                        val crClBase = ((140 - age) * weight) / (72 * scr)
                        val crClFinal = if (isRenalFemale) crClBase * 0.85f else crClBase

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().testTag("crcl_renal_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Cockcroft-Gault Creatinine Clearance (CrCl)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Standard clinical equation used for antibiotic and renally-cleared drug dosage adjustments.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = scrInput,
                                    onValueChange = { scrInput = it },
                                    label = { Text("Serum Creatinine (mg/dL)") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = ageInput,
                                        onValueChange = { ageInput = it },
                                        label = { Text("Age (years)") },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = weightInput,
                                        onValueChange = { weightInput = it },
                                        label = { Text("Weight (kg)") },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                CalculatorCheckboxItem("Female Patient (multiply by 0.85)", isRenalFemale) { isRenalFemale = it }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = HighYieldAmberContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "ESTIMATED CrCl: ${String.format("%.1f", crClFinal)} mL/min",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = HighYieldAmberOnContainer
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        val dosingNote = when {
                                            crClFinal < 15f -> "End-Stage Renal Failure (CrCl < 15). Dialysis dosing required for Vancomycin, Aminoglycosides, Enoxaparin. DOACs contraindicated."
                                            crClFinal < 30f -> "Severe Renal Impairment (CrCl 15-30). Reduce DOAC dose (e.g. Apixaban 2.5mg BID). Avoid NSAIDs and Metformin."
                                            crClFinal < 50f -> "Moderate Renal Impairment (CrCl 30-50). Dose adjustment required for Cefepime, Piperacillin-Tazobactam, and LMWH."
                                            else -> "Normal Renal Clearance (CrCl > 50 mL/min). Standard adult dosing regimens applicable."
                                        }

                                        Text(
                                            text = "Dosing Adjustment Protocol: $dosingNote",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = HighYieldAmberOnContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Vercel & GitHub Free Cloud Deployment Hub
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().testTag("multi_platform_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Computer,
                                        contentDescription = "Vercel & GitHub Deployment",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "🚀 GitHub & Vercel Web Publishing Hub",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Free Automated Cloud Deployment & PWA Hosting",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "⚡ Instant Cloud Publishing with GitHub & Vercel",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Publish your app directly to GitHub and Vercel with zero deployment fees. Push code to your repository to trigger automatic worldwide PWA deployment with SSL, edge caching, and mobile responsiveness!",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com"))
                                            context.startActivity(intent)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        modifier = Modifier.weight(1f).testTag("open_github_button")
                                    ) {
                                        Text("1. Open Connected GitHub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://vercel.com/new"))
                                            context.startActivity(intent)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.weight(1f).testTag("deploy_vercel_button")
                                    ) {
                                        Text("2. Deploy on Vercel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.PhoneIphone, contentDescription = "Mobile", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Mobile Web & PWA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Vercel hosts responsive progressive web app accessible on iOS & Android.", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Computer, contentDescription = "PC Mac", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Desktop & Cloud Sync", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Automatic Git push triggers instant production deployment on Vercel edge CDN.", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val backupData = """
                                            MEDCOLLEAGUE CLINICAL BACKUP DECK & VERCEL WEB CONFIG
                                            Target Platform: Vercel Cloud Web Hosting + Connected GitHub
                                            Saved High-Yield Pearls: ${pearls.size} concepts
                                            
                                            VERCEL DEPLOYMENT INSTRUCTIONS:
                                            1. Push repository changes to GitHub.
                                            2. Go to https://vercel.com/new and import your GitHub repository.
                                            3. Set root directory to workspace and click 'Deploy'.
                                            
                                            STUDY DECK PEARLS:
                                            ${pearls.joinToString("\n\n") { p -> "[${p.specialty}] ${p.title}\nConcept: ${p.concept}\nPearl: ${p.highYieldPearl}" }}
                                        """.trimIndent()
                                        ExportUtils.shareAndExportText(context, "MedColleague_Vercel_GitHub_Config", backupData)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth().testTag("export_backup_button")
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = "Backup")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Export Complete Study Deck & Vercel Web Config", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorCheckboxItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
