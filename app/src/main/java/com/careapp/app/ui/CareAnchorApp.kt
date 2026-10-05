package com.careapp.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

enum class CareAnchorScreen {
    Home,
    Medication,
    Family,
    Routine,
    CaregiverNotes,
    Emergency,
    Lost,
    CalmMode,
    CareSnapshot,
    MedicationHistory,
    SafetyReadiness,
    Settings,
    DoctorSummary,
    QRCode
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CareAnchorApp() {
    val context = LocalContext.current
    var isDarkMode by remember { mutableStateOf(false) }
    var isCaregiverMode by remember { mutableStateOf(false) }

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
                        Button(
                            onClick = { isCaregiverMode = !isCaregiverMode },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isCaregiverMode) SecondaryCoral else Color(0xFFE0E0E0)),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(if (isCaregiverMode) "👩‍⚕️" else "👴", fontSize = 16.sp)
                        }
                        Button(
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
                startDestination = CareAnchorScreen.Home.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(CareAnchorScreen.Home.name) {
                    HomeScreen(navController, dashboardViewModel)
                }
                composable(CareAnchorScreen.Medication.name) {
                    MedicationScreen(viewModel = medViewModel)
                }
                composable(CareAnchorScreen.Family.name) {
                    MemoryCardsScreen(viewModel = memoryViewModel)
                }
                composable(CareAnchorScreen.Routine.name) {
                    RoutineScreen(viewModel = routineViewModel)
                }
                composable(CareAnchorScreen.CaregiverNotes.name) {
                    CaregiverNotesScreen(viewModel = caregiverViewModel)
                }
                composable(CareAnchorScreen.Emergency.name) {
                    EmergencyScreen(viewModel = emergencyViewModel, navController = navController)
                }
                composable(CareAnchorScreen.Lost.name) {
                    LostModeScreen(viewModel = locationViewModel, navController = navController)
                }
                composable(CareAnchorScreen.CalmMode.name) {
                    CalmModeScreen(navController = navController, viewModel = memoryViewModel)
                }
                composable(CareAnchorScreen.CareSnapshot.name) {
                    CareSnapshotScreen()
                }
                composable(CareAnchorScreen.MedicationHistory.name) {
                    MedicationHistoryScreen(viewModel = medViewModel)
                }
                composable(CareAnchorScreen.SafetyReadiness.name) {
                    SafetyReadinessScreen()
                }
                composable(CareAnchorScreen.Settings.name) {
                    SettingsScreen(viewModel = accessibilityViewModel)
                }
                composable(CareAnchorScreen.DoctorSummary.name) {
                    DoctorSummaryScreen()
                }
                composable(CareAnchorScreen.QRCode.name) {
                    QRCodeScreen()
                }
            }
        }
    }
}

data class NavBarItem(val screen: CareAnchorScreen, val emoji: String, val label: String)

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
        val screens = if (isCaregiverMode) {
            listOf(
                NavBarItem(CareAnchorScreen.Routine, "📅", "Routine"),
                NavBarItem(CareAnchorScreen.Medication, "💊", "Medicine"),
                NavBarItem(CareAnchorScreen.CaregiverNotes, "📝", "Notes"),
                NavBarItem(CareAnchorScreen.DoctorSummary, "📋", "Report"),
                NavBarItem(CareAnchorScreen.Settings, "⚙️", "Settings")
            )
        } else {
            listOf(
                NavBarItem(CareAnchorScreen.Home, "🏠", "Home"),
                NavBarItem(CareAnchorScreen.Medication, "💊", "Medicine"),
                NavBarItem(CareAnchorScreen.Family, "👨‍👩‍👧", "Family"),
                NavBarItem(CareAnchorScreen.Emergency, "🆘", "Emergency"),
                NavBarItem(CareAnchorScreen.Lost, "📍", "Lost")
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
