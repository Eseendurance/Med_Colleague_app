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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MedColleagueViewModel
import com.example.ui.theme.HighYieldAmberContainer
import com.example.ui.theme.HighYieldAmberOnContainer

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.material.icons.filled.Menu

import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility

import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.ui.platform.LocalContext
import com.example.ui.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PearlsScreen(
    viewModel: MedColleagueViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pearls by viewModel.pearls.collectAsState()
    val duePearls by viewModel.duePearls.collectAsState()
    val selectedFilter by viewModel.selectedSpecialtyFilter.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: All Pearls, 1: Spaced Repetition Queue
    var activeReviewIndex by remember { mutableStateOf(0) }
    var isAnswerRevealed by remember { mutableStateOf(false) }

    val specialties = listOf("All", "Cardiology", "Pulmonology", "Neurology", "Endocrinology", "Gastroenterology", "Infectious Disease")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "High-Yield Medical Pearls", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = if (selectedTab == 0) "Clinical Recalls & Board Concepts" else "Daily Spaced Repetition Queue (${duePearls.size} Due)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("pearls_drawer_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Drawer")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { ExportUtils.exportPearlsToAnki(context, pearls) },
                        modifier = Modifier.testTag("pearls_topbar_anki_export_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export Deck to Anki", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_pearl_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Pearl")
                }
            }
        },
        modifier = modifier.testTag("pearls_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Mode Switcher Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Library",
                            tint = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "All Pearls (${pearls.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedTab = 1
                            activeReviewIndex = 0
                            isAnswerRevealed = false
                        }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Review Queue",
                            tint = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Spaced Review (${duePearls.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (selectedTab == 1) {
                // DAILY SPACED REPETITION QUEUE VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (duePearls.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Complete",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Daily Queue Cleared!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "You have reviewed all due high-yield pearls for today according to the SM-2 algorithm. Check back tomorrow for your next memory retention queue!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val currentPearl = duePearls.getOrNull(activeReviewIndex.coerceIn(0, duePearls.size - 1))
                        if (currentPearl != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Card ${activeReviewIndex + 1} of ${duePearls.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HighYieldAmberContainer
                                ) {
                                    Text(
                                        text = currentPearl.specialty,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = HighYieldAmberOnContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Flashcard Box
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .testTag("flashcard_review_box")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = currentPearl.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Core Concept: ${currentPearl.concept}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (!isAnswerRevealed) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .clickable { isAnswerRevealed = true }
                                                .padding(20.dp)
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.Visibility,
                                                    contentDescription = "Reveal Answer",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(36.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "Tap to Reveal Recall & Guideline",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = HighYieldAmberContainer,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(14.dp)) {
                                                    Text(
                                                        text = "🔥 HIGH-YIELD CLINICAL RECALL:",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = HighYieldAmberOnContainer
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = currentPearl.highYieldPearl,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = HighYieldAmberOnContainer
                                                    )
                                                }
                                            }

                                            if (currentPearl.moaOrGuideline.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(14.dp)) {
                                                        Text(
                                                            text = "MECHANISM / FIRST-LINE GUIDELINE:",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = currentPearl.moaOrGuideline,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Grading SM-2 Rating Buttons
                            if (isAnswerRevealed) {
                                Text(
                                    text = "Rate Recall Quality (SM-2 Spaced Algorithm):",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Rating 0: Again
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFFEBEE),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.reviewPearl(currentPearl, 0)
                                                isAnswerRevealed = false
                                            }
                                            .testTag("rate_again_button")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text("🔄 Again", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 12.sp)
                                            Text("<1 Day", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC62828).copy(alpha = 0.8f), fontSize = 10.sp)
                                        }
                                    }

                                    // Rating 1: Hard
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFFF8E1),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.reviewPearl(currentPearl, 1)
                                                isAnswerRevealed = false
                                            }
                                            .testTag("rate_hard_button")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text("⚡ Hard", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17), fontSize = 12.sp)
                                            Text("1.2x", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF57F17).copy(alpha = 0.8f), fontSize = 10.sp)
                                        }
                                    }

                                    // Rating 2: Good
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFE8F5E9),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.reviewPearl(currentPearl, 2)
                                                isAnswerRevealed = false
                                            }
                                            .testTag("rate_good_button")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text("👍 Good", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                                            Text("Normal", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32).copy(alpha = 0.8f), fontSize = 10.sp)
                                        }
                                    }

                                    // Rating 3: Easy
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFE0F7FA),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.reviewPearl(currentPearl, 3)
                                                isAnswerRevealed = false
                                            }
                                            .testTag("rate_easy_button")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text("🌟 Easy", fontWeight = FontWeight.Bold, color = Color(0xFF00838F), fontSize = 12.sp)
                                            Text("Bonus", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00838F).copy(alpha = 0.8f), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
            // ALL PEARLS LIBRARY VIEW
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search pearls, MoA, or guidelines...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("pearls_search_input")
                )

                // Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(specialties) { spec ->
                        val selected = (selectedFilter == spec)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.setSpecialtyFilter(spec) }
                        ) {
                            Text(
                                text = spec,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

            val filteredPearls = pearls.filter { p ->
                (selectedFilter == "All" || p.specialty.equals(selectedFilter, ignoreCase = true)) &&
                (searchQuery.isBlank() ||
                 p.title.contains(searchQuery, ignoreCase = true) ||
                 p.concept.contains(searchQuery, ignoreCase = true) ||
                 p.highYieldPearl.contains(searchQuery, ignoreCase = true) ||
                 p.moaOrGuideline.contains(searchQuery, ignoreCase = true))
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Anki Spaced-Repetition Export Banner Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("anki_export_banner")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Anki Export",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎴 Spaced-Repetition Anki Export",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Export saved medical pearls directly into Anki Desktop & AnkiDroid flashcard format (.txt / .csv)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { ExportUtils.exportPearlsToAnki(context, pearls) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("export_anki_button")
                            ) {
                                Text("Export Anki", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (filteredPearls.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = "Empty", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "No High-Yield Pearls Found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = "Tap the '+' button to save a custom pearl or query MedColleague on Rounds!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                items(filteredPearls) { pearl ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("pearl_card_${pearl.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HighYieldAmberContainer
                                ) {
                                    Text(
                                        text = pearl.specialty,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = HighYieldAmberOnContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { ExportUtils.exportPearlsToAnki(context, listOf(pearl)) }) {
                                    Icon(Icons.Default.Share, contentDescription = "Export Card to Anki", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deletePearl(pearl.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Pearl", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = pearl.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Concept: ${pearl.concept}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)

                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "🔥 High-Yield Exam / Clinical Recall:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = pearl.highYieldPearl, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }

                            if (pearl.moaOrGuideline.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Mechanism / Guideline: ${pearl.moaOrGuideline}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // Dialog to add custom pearl
    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newSpecialty by remember { mutableStateOf("Cardiology") }
        var newConcept by remember { mutableStateOf("") }
        var newPearl by remember { mutableStateOf("") }
        var newMoa by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add High-Yield Pearl") },
            text = {
                Column {
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newSpecialty, onValueChange = { newSpecialty = it }, label = { Text("Specialty") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newConcept, onValueChange = { newConcept = it }, label = { Text("Core Concept") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newPearl, onValueChange = { newPearl = it }, label = { Text("High-Yield Pearl") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newMoa, onValueChange = { newMoa = it }, label = { Text("MoA / First-Line Guideline") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitle.isNotBlank() && newPearl.isNotBlank()) {
                            viewModel.addCustomPearl(newTitle, newSpecialty, newConcept, newPearl, newMoa)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_pearl_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
}
