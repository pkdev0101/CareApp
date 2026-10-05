package com.careapp.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.ui.screens.*
import com.careapp.app.ui.theme.CareAppTheme
import com.careapp.app.ui.theme.*
import com.careapp.app.viewmodel.*

enum class CareAppScreen {
    Home,
    Medication,
    Family,
    Routine,
    CaregiverNotes,
    Emergency,
    Lost,
    Help,
    CalmMode,
    CareSnapshot,
    MedicationHistory,
    SafetyReadiness,
    Settings,
    DoctorSummary,
    QRCode,
    DemoMode
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CareAppRoot() {
    val context = LocalContext.current
    var isDarkMode by remember { mutableStateOf(false) }
    val modeViewModel: CareModeViewModel = viewModel(factory = CareModeViewModelFactory(context))
    val careMode by modeViewModel.mode.collectAsState()
    val isCaregiverMode = careMode == CareMode.CAREGIVER

    CareAppTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        val repository = CareRepository.getInstance(context)
        LaunchedEffect(repository) { repository.seedIfNeeded() }
        val medViewModel: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(repository))
        val routineViewModel: RoutineViewModel = viewModel(factory = RoutineViewModelFactory(repository))
        val emergencyViewModel: EmergencyViewModel = viewModel(factory = EmergencyViewModelFactory(repository))
        val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModelFactory(repository))
        val memoryViewModel: MemoryCardViewModel = viewModel(factory = MemoryCardViewModelFactory(repository))
        val caregiverViewModel: CaregiverNotesViewModel = viewModel(factory = CaregiverNotesViewModelFactory(repository))
        val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(repository))
        val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory())
        val accessibilityViewModel: AccessibilityViewModel = viewModel()
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "CareApp${if (isCaregiverMode) " (Caregiver)" else ""}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                val wasCaregiver = isCaregiverMode
                                modeViewModel.toggleMode()
                            },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(if (isCaregiverMode) "Senior Mode" else "Caregiver Mode", fontSize = 12.sp)
                        }
                        if (isCaregiverMode) Button(
                            onClick = { isDarkMode = !isDarkMode },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color.Gray else Color.White),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(if (isDarkMode) "☀️" else "🌙", fontSize = 16.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryTeal)
                )
            },
            bottomBar = {
                BottomNavBar(navController = navController, isCaregiverMode = isCaregiverMode)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = CareAppScreen.Home.name,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                composable(CareAppScreen.Home.name) {
                    HomeScreen(navController, dashboardViewModel)
                }
                composable(CareAppScreen.Medication.name) {
                    MedicationScreen(viewModel = medViewModel, navController = navController)
                }
                composable(CareAppScreen.Family.name) {
                    MemoryCardsScreen(viewModel = memoryViewModel)
                }
                composable(CareAppScreen.Routine.name) {
                    RoutineScreen(viewModel = routineViewModel)
                }
                composable(CareAppScreen.CaregiverNotes.name) {
                    CaregiverNotesScreen(viewModel = caregiverViewModel)
                }
                composable(CareAppScreen.Emergency.name) {
                    EmergencyScreen(viewModel = emergencyViewModel, navController = navController)
                }
                composable(CareAppScreen.Lost.name) {
                    LostModeScreen(viewModel = locationViewModel, navController = navController)
                }
                composable(CareAppScreen.Help.name) {
                    HelpScreen(navController = navController, emergencyViewModel = emergencyViewModel)
                }
                composable(CareAppScreen.CalmMode.name) {
                    CalmModeScreen(navController = navController, viewModel = memoryViewModel)
                }
                composable(CareAppScreen.CareSnapshot.name) {
                    CareSnapshotScreen(navController)
                }
                composable(CareAppScreen.MedicationHistory.name) {
                    MedicationHistoryScreen(viewModel = viewModel(factory = MedicationHistoryViewModelFactory(repository)))
                }
                composable(CareAppScreen.SafetyReadiness.name) {
                    SafetyReadinessScreen()
                }
                composable(CareAppScreen.Settings.name) {
                    SettingsScreen(viewModel = accessibilityViewModel, navController = navController, isCaregiverMode = isCaregiverMode)
                }
                composable(CareAppScreen.DoctorSummary.name) {
                    DoctorSummaryScreen(navController)
                }
                composable(CareAppScreen.QRCode.name) {
                    QRCodeScreen()
                }
                composable(CareAppScreen.DemoMode.name) {
                    DemoModeScreen(navController)
                }
            }
        }
    }
}

data class NavBarItem(val screen: CareAppScreen, val emoji: String, val label: String)

@Composable
fun BottomNavBar(navController: NavHostController, isCaregiverMode: Boolean = false) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        containerColor = PrimaryTeal
    ) {
        val legacyScreens = if (isCaregiverMode) {
            listOf(
                NavBarItem(CareAppScreen.Routine, "📅", "Routine"),
                NavBarItem(CareAppScreen.Medication, "💊", "Medicine"),
                NavBarItem(CareAppScreen.CaregiverNotes, "📝", "Notes"),
                NavBarItem(CareAppScreen.DoctorSummary, "📋", "Report"),
                NavBarItem(CareAppScreen.Settings, "⚙️", "Settings")
            )
        } else {
            listOf(
                NavBarItem(CareAppScreen.Home, "🏠", "Home"),
                NavBarItem(CareAppScreen.Medication, "💊", "Medicine"),
                NavBarItem(CareAppScreen.Family, "👨‍👩‍👧", "People"),
                NavBarItem(CareAppScreen.CalmMode, "🫶", "I Forgot"),
                NavBarItem(CareAppScreen.Emergency, "🆘", "Emergency"),
                NavBarItem(CareAppScreen.Lost, "📍", "Lost")
            )
        }

        val screens = if (isCaregiverMode) {
            legacyScreens
        } else {
            listOf(
                NavBarItem(CareAppScreen.Home, "⌂", "Home"),
                NavBarItem(CareAppScreen.Medication, "M", "Medicine"),
                NavBarItem(CareAppScreen.Family, "P", "People"),
                NavBarItem(CareAppScreen.CalmMode, "?", "I Forgot"),
                NavBarItem(CareAppScreen.Help, "!", "Help")
            )
        }

        screens.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.screen.name
            } ?: false

            NavigationBarItem(
                icon = {
                    Text(
                        text = item.emoji,
                        fontSize = 24.sp
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.screen.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = PrimaryTealVeryLight,
                    unselectedTextColor = PrimaryTealVeryLight,
                    indicatorColor = PrimaryTeal
                )
            )
        }
    }
}
