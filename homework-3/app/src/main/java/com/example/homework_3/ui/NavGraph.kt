package com.example.homework_3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homework_3.ui.screen.CharacterDetailScreen
import com.example.homework_3.ui.screen.CharacterListScreen
import com.example.homework_3.ui.screen.RecentScreen
import com.example.homework_3.ui.screen.SettingsScreen
import com.example.homework_3.ui.viewmodel.CharactersViewModel
import com.example.homework_3.ui.viewmodel.RecentViewModel
import com.example.homework_3.ui.viewmodel.SettingsViewModel

sealed class CharactersRoute(val route: String) {
    object List : CharactersRoute("list")
    object Settings : CharactersRoute("settings")
    object Recent : CharactersRoute("recent")
    object Detail : CharactersRoute("detail/{characterId}") {
        const val ARG_ID = "characterId"
        fun createRoute(id: String): String = "detail/$id"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: CharactersViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = CharactersRoute.List.route
    ) {
        composable(CharactersRoute.List.route) {
            CharacterListScreen(
                viewModel = viewModel,
                onCharacterClick = { characterId ->
                    navController.navigate(CharactersRoute.Detail.createRoute(characterId))
                },
                onOpenSettings = { navController.navigate(CharactersRoute.Settings.route) },
                onOpenRecent = { navController.navigate(CharactersRoute.Recent.route) },
            )
        }

        composable(CharactersRoute.Settings.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(CharactersRoute.Recent.route) {
            val recentViewModel: RecentViewModel = hiltViewModel()
            RecentScreen(
                viewModel = recentViewModel,
                onOpenCharacter = { characterId ->
                    navController.navigate(CharactersRoute.Detail.createRoute(characterId))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = CharactersRoute.Detail.route,
            arguments = listOf(navArgument(CharactersRoute.Detail.ARG_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString(CharactersRoute.Detail.ARG_ID) ?: ""

            LaunchedEffect(characterId) {
                if (characterId.isNotBlank()) {
                    viewModel.loadCharacter(characterId)
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    viewModel.clearDetailState()
                }
            }

            CharacterDetailScreen(
                character = detailState.character,
                isFavourite = characterId in uiState.favourites,
                isLoading = detailState.isLoading,
                errorMessage = detailState.error,
                onToggleFavourite = { viewModel.onToggleFavourite(characterId) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}