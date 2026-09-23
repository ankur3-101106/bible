package com.sanctuary.bible.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sanctuary.bible.ui.home.HomeScreen
import com.sanctuary.bible.ui.home.HomeViewModel
import com.sanctuary.bible.ui.more.BookmarksNotesScreen
import com.sanctuary.bible.ui.more.MoreScreen
import com.sanctuary.bible.ui.more.MoreViewModel
import com.sanctuary.bible.ui.more.SearchScreen
import com.sanctuary.bible.ui.onboarding.OnboardingScreen
import com.sanctuary.bible.ui.onboarding.OnboardingViewModel
import com.sanctuary.bible.ui.plan.PlanScreen
import com.sanctuary.bible.ui.plan.PlanViewModel
import com.sanctuary.bible.ui.progress.ProgressScreen
import com.sanctuary.bible.ui.progress.ProgressViewModel
import com.sanctuary.bible.ui.reader.ReaderScreen
import com.sanctuary.bible.ui.reader.ReaderViewModel
import com.sanctuary.bible.ui.settings.SettingsScreen
import com.sanctuary.bible.ui.settings.SettingsViewModel

@Composable
fun SanctuaryNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    readerViewModel: ReaderViewModel,
    planViewModel: PlanViewModel,
    progressViewModel: ProgressViewModel,
    moreViewModel: MoreViewModel,
    onboardingViewModel: OnboardingViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToRead = { navController.navigate(Screen.Read.route) }
            )
        }
        composable(Screen.Read.route) {
            ReaderScreen(
                viewModel = readerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Plan.route) {
            PlanScreen(
                viewModel = planViewModel
            )
        }
        composable(Screen.Progress.route) {
            ProgressScreen(
                viewModel = progressViewModel
            )
        }
        composable(Screen.More.route) {
            MoreScreen(
                viewModel = moreViewModel,
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToLibrary = { navController.navigate(Screen.Read.route) },
                onNavigateToBookmarks = { navController.navigate("bookmarks") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("search") {
            SearchScreen(
                viewModel = moreViewModel,
                onBack = { navController.popBackStack() },
                onSelectVerse = { book, chap ->
                    readerViewModel.loadChapter(book, chap)
                    navController.navigate(Screen.Read.route)
                }
            )
        }
        composable("bookmarks") {
            BookmarksNotesScreen(
                viewModel = moreViewModel,
                onBack = { navController.popBackStack() },
                onSelectBookmark = { book, chap ->
                    readerViewModel.loadChapter(book, chap)
                    navController.navigate(Screen.Read.route)
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onPlanCreated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
    }
}
