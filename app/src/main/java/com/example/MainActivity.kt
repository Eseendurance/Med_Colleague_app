package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MedColleagueViewModel
import com.example.ui.components.ReferenceDrawerBottomSheet
import com.example.ui.components.RoleSelectorToggle
import com.example.ui.screens.ExamPrepScreen
import com.example.ui.screens.PearlsScreen
import com.example.ui.screens.ReferencesScreen
import com.example.ui.screens.RoundsScreen
import com.example.ui.screens.VignettesScreen
import com.example.ui.theme.MedColleagueTheme
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Group
import com.example.ui.screens.AcademicWriterScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.StudyGroupScreen
import com.example.ui.screens.VisionDiagnosticScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Rounds : Screen("rounds", "Rounds", Icons.Default.MedicalServices)
    object ExamPrep : Screen("examprep", "Exam Studio", Icons.Default.Quiz)
    object Pearls : Screen("pearls", "Pearls & SM-2", Icons.Default.Lightbulb)
    object Vision : Screen("vision", "Vision Diagnostics", Icons.Default.CameraAlt)
    object StudyGroup : Screen("studygroup", "Virtual Study Room", Icons.Default.Group)
    object Calculators : Screen("calculators", "Calculators & Vercel Sync", Icons.Default.Calculate)
    object AcademicWriter : Screen("academic", "Thesis Writer", Icons.Default.Edit)
    object Vignettes : Screen("vignettes", "Vignettes", Icons.Default.Psychology)
    object References : Screen("references", "References", Icons.Default.MenuBook)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MedColleagueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MedColleagueTheme(darkTheme = isDarkMode) {
                MedColleagueApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedColleagueApp(viewModel: MedColleagueViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Rounds.route

    var showReferenceBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val userRole by viewModel.userRole.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val screens = listOf(
        Screen.Rounds,
        Screen.ExamPrep,
        Screen.Pearls,
        Screen.Vision,
        Screen.StudyGroup,
        Screen.Calculators,
        Screen.AcademicWriter,
        Screen.Vignettes,
        Screen.References
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .testTag("side_navigation_drawer")
            ) {
                // Drawer Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_medical_hero_1785396681969),
                        contentDescription = "Drawer Header Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "MedColleague AI",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "MedColleague Senior Educator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Clinical Rounds & Multi-Style Exam Prep",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Role Perspective Selector
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Perspective Mode",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RoleSelectorToggle(
                        currentRole = userRole,
                        onRoleSelected = { viewModel.setUserRole(it) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dark Mode / Night Study Theme Switcher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme Toggle",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDarkMode) "Night Study Dark Mode" else "Medical Light Mode",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("theme_mode_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "CLINICAL NAVIGATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                screens.forEach { screen ->
                    val selected = (currentRoute == screen.route)
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                        selected = selected,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .testTag("drawer_item_${screen.route}")
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = "Vercel Web Sync",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🚀 Vercel & GitHub Web Deployment",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connected GitHub → Automatic Vercel Edge Cloud Deployment & PWA",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    screens.forEach { screen ->
                        val selected = (currentRoute == screen.route)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Rounds.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Rounds.route) {
                    RoundsScreen(
                        viewModel = viewModel,
                        onOpenReferences = { showReferenceBottomSheet = true },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.ExamPrep.route) {
                    ExamPrepScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Pearls.route) {
                    PearlsScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Vision.route) {
                    VisionDiagnosticScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.StudyGroup.route) {
                    StudyGroupScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Calculators.route) {
                    CalculatorsScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.AcademicWriter.route) {
                    AcademicWriterScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Vignettes.route) {
                    VignettesScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.References.route) {
                    ReferencesScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
            }

            if (showReferenceBottomSheet) {
                ReferenceDrawerBottomSheet(
                    sheetState = sheetState,
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showReferenceBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}
