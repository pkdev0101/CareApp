package com.careapp.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.CompositionLocalProvider
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
import com.careapp.app.util.CaregiverPinStore
import com.careapp.app.util.CaregiverAuditStore
import com.careapp.app.util.MedicationReminderPreferences
import com.careapp.app.util.NotificationHelper

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
    ,CareSetup, ManageMedications, ManagePeople, ManageRoutines, ManagePlaces, EmergencyProfile
}

private fun getTopBarTitle(route: String?, isCaregiverMode: Boolean): String? = when (route) {
    CareAppScreen.Home.name -> if (isCaregiverMode) "Dashboard" else "CareApp"
    CareAppScreen.Medication.name -> if (isCaregiverMode) "Medication Setup" else "Medicine"
    CareAppScreen.Family.name -> "People"
    CareAppScreen.Routine.name -> "Routines"
    CareAppScreen.CaregiverNotes.name -> "Care Notes"
    CareAppScreen.DoctorSummary.name -> "Reports"
    CareAppScreen.Settings.name -> "Settings"
    CareAppScreen.CalmMode.name -> "I Forgot"
    CareAppScreen.Help.name -> "Help"
    CareAppScreen.Emergency.name -> "Emergency Help"
    CareAppScreen.Lost.name -> "I May Be Lost"
    CareAppScreen.MedicationHistory.name -> "Medication History"
    CareAppScreen.CareSnapshot.name -> "Care Snapshot"
    CareAppScreen.DemoMode.name -> "Judge Demo"
    CareAppScreen.CareSetup.name -> "Care Setup"
    CareAppScreen.ManageMedications.name -> "Manage Medications"
    CareAppScreen.ManagePeople.name -> "Manage People"
    CareAppScreen.ManageRoutines.name -> "Manage Routines"
    CareAppScreen.ManagePlaces.name -> "Manage Places"
    CareAppScreen.EmergencyProfile.name -> "Emergency Profile"
    CareAppScreen.QRCode.name -> "Emergency QR"
    else -> null
}

private fun getTopBarIcon(route: String?): String = when (route) {
    CareAppScreen.Home.name -> "⌂"
    CareAppScreen.Medication.name, CareAppScreen.MedicationHistory.name -> "✚"
    CareAppScreen.Family.name -> "◉"
    CareAppScreen.Routine.name -> "▦"
    CareAppScreen.CaregiverNotes.name, CareAppScreen.DoctorSummary.name -> "▤"
    CareAppScreen.Emergency.name -> "!"
    CareAppScreen.Lost.name -> "⌖"
    CareAppScreen.Help.name, CareAppScreen.CalmMode.name -> "?"
    CareAppScreen.QRCode.name -> "▣"
    CareAppScreen.Settings.name -> "⚙"
    CareAppScreen.CareSetup.name -> "◆"
    else -> "•"
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CareAppRoot() {
    val context = LocalContext.current
    val modeViewModel: CareModeViewModel = viewModel(factory = CareModeViewModelFactory(context))
    val careMode by modeViewModel.mode.collectAsState()
    val isCaregiverMode = careMode == CareMode.CAREGIVER
    val accessibilityViewModel: AccessibilityViewModel = viewModel(factory = AccessibilityViewModelFactory(context))
    val textSizeScale by accessibilityViewModel.textSizeScale.collectAsState()
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(context))
    val themeMode by themeViewModel.themeMode.collectAsState()
    val caregiverPinStore = remember { CaregiverPinStore(context) }
    val caregiverAuditStore = remember { CaregiverAuditStore(context) }
    var showCaregiverPinDialog by remember { mutableStateOf(false) }
    var caregiverPin by remember { mutableStateOf("") }
    var caregiverPinError by remember { mutableStateOf("") }
    var caregiverPinAttempts by remember { mutableIntStateOf(0) }
    var showSupervisorReset by remember { mutableStateOf(false) }
    var supervisorPin by remember { mutableStateOf("") }
    var replacementCaregiverPin by remember { mutableStateOf("") }
    var showCaregiverPin by remember { mutableStateOf(false) }
    var showSupervisorPin by remember { mutableStateOf(false) }
    var showReplacementPin by remember { mutableStateOf(false) }
    var careAppTitleTaps by remember { mutableIntStateOf(0) }

    CareAppTheme(
        darkTheme = when (themeMode) {
            ThemeViewModel.AppThemeMode.DARK -> true
            ThemeViewModel.AppThemeMode.LIGHT -> false
            ThemeViewModel.AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        },
        textScale = textSizeScale
    ) {
        val navController = rememberNavController()
        val repository = CareRepository.getInstance(context)
        val reminderPreferences = remember { MedicationReminderPreferences(context) }
        LaunchedEffect(repository) {
            repository.seedIfNeeded()
            repository.getAllMedicationsSync().forEach {
                if (reminderPreferences.isGloballyEnabled()) NotificationHelper.scheduleMedicationReminder(context, it)
            }
        }
        val medViewModel: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(repository))
        val routineViewModel: RoutineViewModel = viewModel(factory = RoutineViewModelFactory(repository))
        val emergencyViewModel: EmergencyViewModel = viewModel(factory = EmergencyViewModelFactory(repository))
        val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModelFactory(repository))
        val memoryViewModel: MemoryCardViewModel = viewModel(factory = MemoryCardViewModelFactory(repository))
        val caregiverViewModel: CaregiverNotesViewModel = viewModel(factory = CaregiverNotesViewModelFactory(repository))
        val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(repository))
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val detailRoute = currentRoute in setOf(
            CareAppScreen.MedicationHistory.name, CareAppScreen.CareSnapshot.name,
            CareAppScreen.DoctorSummary.name, CareAppScreen.DemoMode.name, CareAppScreen.QRCode.name,
            CareAppScreen.CareSetup.name, CareAppScreen.ManageMedications.name, CareAppScreen.ManagePeople.name,
            CareAppScreen.ManageRoutines.name, CareAppScreen.ManagePlaces.name
            ,CareAppScreen.EmergencyProfile.name
        )
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (detailRoute) IconButton(onClick = { navController.popBackStack() }) { Text("‹", fontSize = 36.sp) }
                    },
                    title = {
                        Column {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(30.dp),
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                        Text(
                                            when (currentRoute) {
                                                CareAppScreen.Help.name -> "!"
                                                CareAppScreen.CalmMode.name -> "?"
                                                else -> getTopBarIcon(currentRoute)
                                            },
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "CareApp",
                                    modifier = Modifier.clickable {
                                        if (isCaregiverMode) {
                                            careAppTitleTaps++
                                            if (careAppTitleTaps >= 10) {
                                                careAppTitleTaps = 0
                                                navController.navigate(CareAppScreen.DemoMode.name)
                                            }
                                        }
                                    },
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            val section = getTopBarTitle(currentRoute, isCaregiverMode)
                            if (section != null && section != "CareApp") {
                                Text(
                                    "$section · ${if (isCaregiverMode) "Caregiver mode" else "Senior mode"}",
                                    fontSize = 12.sp,
                                    color = PrimaryTealVeryLight
                                )
                            }
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                if (isCaregiverMode) {
                                    modeViewModel.toggleMode()
                                    navController.navigate(CareAppScreen.Home.name) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = false }
                                        launchSingleTop = true
                                    }
                                } else {
                                    caregiverPin = ""
                                    caregiverPinError = ""
                                    caregiverPinAttempts = 0
                                    showSupervisorReset = false
                                    supervisorPin = ""
                                    replacementCaregiverPin = ""
                                    showCaregiverPin = false
                                    showSupervisorPin = false
                                    showReplacementPin = false
                                    showCaregiverPinDialog = true
                                }
                            },
                            modifier = Modifier.padding(horizontal = 4.dp).heightIn(min = 48.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoralDark, contentColor = Color.White)
                        ) {
                            Text(if (isCaregiverMode) "Switch to Senior" else "Switch to Caregiver", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                startDestination = if (isCaregiverMode) CareAppScreen.CaregiverNotes.name else CareAppScreen.Home.name,
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
                    MedicationScreen(viewModel = medViewModel, navController = navController, isCaregiverMode = isCaregiverMode)
                }
                composable(CareAppScreen.Family.name) {
                    MemoryCardsScreen(viewModel = memoryViewModel)
                }
                composable(CareAppScreen.Routine.name) {
                    RoutineScreen(viewModel = routineViewModel, isCaregiverMode = isCaregiverMode, navController = navController)
                }
                composable(CareAppScreen.CaregiverNotes.name) {
                    CaregiverNotesScreen(viewModel = caregiverViewModel)
                }
                composable(CareAppScreen.Emergency.name) {
                    EmergencyScreen(viewModel = emergencyViewModel, navController = navController)
                }
                composable(CareAppScreen.Lost.name) {
                    LostModeScreen(viewModel = locationViewModel, navController = navController, repository = repository)
                }
                composable(CareAppScreen.Help.name) {
                    HelpScreen(navController = navController, emergencyViewModel = emergencyViewModel, locationViewModel = locationViewModel)
                }
                composable(CareAppScreen.CalmMode.name) {
                    CalmModeScreen(navController = navController, viewModel = memoryViewModel)
                }
                composable(CareAppScreen.CareSnapshot.name) {
                    CareSnapshotScreen(navController)
                }
                composable(CareAppScreen.MedicationHistory.name) {
                    MedicationHistoryScreen(navController = navController, viewModel = viewModel(factory = MedicationHistoryViewModelFactory(repository)))
                }
                composable(CareAppScreen.SafetyReadiness.name) {
                    SafetyReadinessScreen()
                }
                composable(CareAppScreen.Settings.name) {
                    SettingsScreen(viewModel = accessibilityViewModel, navController = navController, isCaregiverMode = isCaregiverMode, themeMode = themeMode, onThemeModeChange = themeViewModel::setThemeMode)
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
                composable(CareAppScreen.CareSetup.name) {
                    CareSetupScreen(repository = repository, navController = navController)
                }
                composable(CareAppScreen.ManageMedications.name) { CareManagementScreen(CareManagementType.MEDICATIONS, repository) }
                composable(CareAppScreen.ManagePeople.name) { CareManagementScreen(CareManagementType.PEOPLE, repository) }
                composable(CareAppScreen.ManageRoutines.name) { CareManagementScreen(CareManagementType.ROUTINES, repository) }
                composable(CareAppScreen.ManagePlaces.name) { CareManagementScreen(CareManagementType.PLACES, repository) }
                composable(CareAppScreen.EmergencyProfile.name) { EmergencyProfileScreen() }
            }
        }
        if (showCaregiverPinDialog) {
            val hasPin = caregiverPinStore.hasPin()
            AlertDialog(
                onDismissRequest = { showCaregiverPinDialog = false },
                title = { Text(if (showSupervisorReset) "Supervisor PIN Override" else if (hasPin) "Enter Caregiver PIN" else "Create Caregiver PIN") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (showSupervisorReset) "Use the supervisor PIN below to reset caregiver access."
                            else if (hasPin) "Enter the 4–6 digit PIN to open caregiver tools."
                            else "Create a 4–6 digit PIN. Caregiver Mode manages medication and safety information."
                        )
                        if (hasPin) {
                            Text("Temporary demo PIN display — hide or remove before production use.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Text("Supervisor PIN: ${caregiverPinStore.supervisorPin()}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            Text("Caregiver PIN: ${caregiverPinStore.caregiverPin()}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                        if (!showSupervisorReset) {
                            OutlinedTextField(
                                value = caregiverPin,
                                onValueChange = { value -> caregiverPin = value.filter(Char::isDigit).take(6); caregiverPinError = "" },
                                label = { Text("PIN") },
                                visualTransformation = if (showCaregiverPin) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = { TextButton(onClick = { showCaregiverPin = !showCaregiverPin }) { Text(if (showCaregiverPin) "Hide" else "Show") } },
                                singleLine = true
                            )
                        }
                        if (showSupervisorReset) {
                            Text("Three caregiver PIN attempts failed. Enter the supervisor PIN to set a new caregiver PIN.")
                            OutlinedTextField(
                                value = supervisorPin,
                                onValueChange = { value -> supervisorPin = value.filter(Char::isDigit).take(6); caregiverPinError = "" },
                                label = { Text("Supervisor PIN") },
                                visualTransformation = if (showSupervisorPin) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = { TextButton(onClick = { showSupervisorPin = !showSupervisorPin }) { Text(if (showSupervisorPin) "Hide" else "Show") } },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = replacementCaregiverPin,
                                onValueChange = { value -> replacementCaregiverPin = value.filter(Char::isDigit).take(6); caregiverPinError = "" },
                                label = { Text("New caregiver PIN") },
                                visualTransformation = if (showReplacementPin) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = { TextButton(onClick = { showReplacementPin = !showReplacementPin }) { Text(if (showReplacementPin) "Hide" else "Show") } },
                                singleLine = true
                            )
                        } else if (hasPin && caregiverPinAttempts > 0) {
                            Text("Attempts: $caregiverPinAttempts of 3", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                        if (caregiverPinError.isNotBlank()) Text(caregiverPinError, color = DangerRed, fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (showSupervisorReset) {
                            when {
                                !caregiverPinStore.matchesSupervisor(supervisorPin) -> caregiverPinError = "That supervisor PIN does not match."
                                replacementCaregiverPin.length !in 4..6 -> caregiverPinError = "New PIN must contain 4 to 6 digits."
                                else -> {
                                    caregiverPinStore.setCaregiverPin(replacementCaregiverPin)
                                    caregiverAuditStore.record("Caregiver PIN reset by supervisor override")
                                    showCaregiverPinDialog = false
                                }
                            }
                        } else if (caregiverPin.length !in 4..6) {
                            caregiverPinError = "PIN must contain 4 to 6 digits."
                        } else if (hasPin && !caregiverPinStore.matches(caregiverPin)) {
                            caregiverPinAttempts++
                            if (caregiverPinAttempts >= 3) {
                                showSupervisorReset = true
                                caregiverPinError = "Use the supervisor PIN to reset caregiver access."
                            } else {
                                caregiverPinError = "That PIN does not match."
                            }
                        } else {
                            if (!hasPin) caregiverPinStore.initializePin(caregiverPin)
                            showCaregiverPinDialog = false
                            modeViewModel.toggleMode()
                            navController.navigate(CareAppScreen.CaregiverNotes.name) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = false }
                                launchSingleTop = true
                            }
                        }
                    }) { Text(if (showSupervisorReset) "Reset Caregiver PIN" else if (hasPin) "Unlock Caregiver Mode" else "Save PIN and Continue") }
                },
                dismissButton = { TextButton(onClick = { showCaregiverPinDialog = false }) { Text("Cancel") } }
            )
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
            .height(82.dp),
        containerColor = PrimaryTeal
    ) {
        val legacyScreens = if (isCaregiverMode) {
            listOf(
                NavBarItem(CareAppScreen.Routine, "▦", "Routine"),
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
                        fontSize = 22.sp
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.height(80.dp),
                selected = isSelected,
                onClick = {
                    if (!navController.popBackStack(item.screen.name, false) && currentDestination?.route != item.screen.name) {
                        navController.navigate(item.screen.name) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
