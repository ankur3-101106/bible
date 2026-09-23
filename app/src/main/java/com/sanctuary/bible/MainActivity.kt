package com.sanctuary.bible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanctuary.bible.data.backup.BackupManager
import com.sanctuary.bible.ui.components.SanctuaryBottomBar
import com.sanctuary.bible.ui.components.SanctuaryNavRail
import com.sanctuary.bible.ui.components.SanctuaryTopBar
import com.sanctuary.bible.ui.home.HomeViewModel
import com.sanctuary.bible.ui.more.MoreViewModel
import com.sanctuary.bible.ui.navigation.SanctuaryNavGraph
import com.sanctuary.bible.ui.navigation.Screen
import com.sanctuary.bible.ui.onboarding.OnboardingViewModel
import com.sanctuary.bible.ui.plan.PlanViewModel
import com.sanctuary.bible.ui.progress.ProgressViewModel
import com.sanctuary.bible.ui.reader.ReaderViewModel
import com.sanctuary.bible.ui.settings.SettingsViewModel
import com.sanctuary.bible.ui.theme.SanctuaryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SanctuaryApplication
        val backupManager = BackupManager(this, app.database)

        val homeViewModelFactory = HomeViewModel.Factory(app.planRepository, app.bibleRepository)
        val readerViewModelFactory = ReaderViewModel.Factory(app.bibleRepository, app.planRepository)
        val planViewModelFactory = PlanViewModel.Factory(app.planRepository)
        val progressViewModelFactory = ProgressViewModel.Factory(app.planRepository)
        val moreViewModelFactory = MoreViewModel.Factory(app.bibleRepository)
        val onboardingViewModelFactory = OnboardingViewModel.Factory(app.planRepository)
        val settingsViewModelFactory = SettingsViewModel.Factory(app.planRepository, backupManager)

        val navigateToTarget = intent?.getStringExtra("navigate_to")

        setContent {
            SanctuaryTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

                LaunchedEffect(navigateToTarget) {
                    if (navigateToTarget == "read") {
                        navController.navigate(Screen.Read.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                }

                val configuration = LocalConfiguration.current
                val isExpandedScreen = configuration.screenWidthDp >= 600

                val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
                val readerViewModel: ReaderViewModel = viewModel(factory = readerViewModelFactory)
                val planViewModel: PlanViewModel = viewModel(factory = planViewModelFactory)
                val progressViewModel: ProgressViewModel = viewModel(factory = progressViewModelFactory)
                val moreViewModel: MoreViewModel = viewModel(factory = moreViewModelFactory)
                val onboardingViewModel: OnboardingViewModel = viewModel(factory = onboardingViewModelFactory)
                val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)

                val currentScreenTitle = when (currentRoute) {
                    Screen.Home.route -> "Home"
                    Screen.Read.route -> "Read"
                    Screen.Plan.route -> "Plan"
                    Screen.Progress.route -> "Progress"
                    Screen.More.route -> "More"
                    "settings" -> "Settings"
                    else -> "Sanctuary"
                }

                if (isExpandedScreen) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        SanctuaryNavRail(
                            currentRoute = currentRoute,
                            onNavigate = { screen ->
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )

                        Scaffold(
                            topBar = {
                                SanctuaryTopBar(
                                    title = currentScreenTitle
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                SanctuaryNavGraph(
                                    navController = navController,
                                    homeViewModel = homeViewModel,
                                    readerViewModel = readerViewModel,
                                    planViewModel = planViewModel,
                                    progressViewModel = progressViewModel,
                                    moreViewModel = moreViewModel,
                                    onboardingViewModel = onboardingViewModel,
                                    settingsViewModel = settingsViewModel
                                )
                            }
                        }
                    }
                } else {
                    Scaffold(
                        topBar = {
                            SanctuaryTopBar(
                                title = currentScreenTitle
                            )
                        },
                        bottomBar = {
                            SanctuaryBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { screen ->
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            SanctuaryNavGraph(
                                navController = navController,
                                homeViewModel = homeViewModel,
                                readerViewModel = readerViewModel,
                                planViewModel = planViewModel,
                                progressViewModel = progressViewModel,
                                moreViewModel = moreViewModel,
                                onboardingViewModel = onboardingViewModel,
                                settingsViewModel = settingsViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
