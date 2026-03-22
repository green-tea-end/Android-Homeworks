package com.example.homework_3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homework_3.ui.screen.CharacterDetailScreen
import com.example.homework_3.ui.screen.CharacterListScreen
import com.example.homework_3.ui.viewmodel.CharactersViewModel

sealed class CharactersRoute(val route: String) {
    object List : CharactersRoute("list")
    object Detail : CharactersRoute("detail/{characterId}") {
        const val ARG_ID = "characterId"
        fun createRoute(id: String): String = "detail/$id"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: CharactersViewModel = hiltViewModel()
    val state = viewModel.uiState

    NavHost(
        navController = navController,
        startDestination = CharactersRoute.List.route
    ) {
        composable(CharactersRoute.List.route) {
            CharacterListScreen(
                state = state,
                characters = viewModel.visibleCharacters,
                onSearchChange = viewModel::onQueryChange,
                onFilterChange = viewModel::onFilterChange,
                onToggleFavourite = viewModel::onToggleFavourite,
                onRefresh = { viewModel.loadCharacters() },
                onCharacterClick = { characterId ->
                    navController.navigate(CharactersRoute.Detail.createRoute(characterId))
                }
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
                character = state.selectedCharacter,
                isFavourite = characterId in state.favourites,
                isLoading = state.isLoadingDetail,
                errorMessage = state.errorDetail,
                onToggleFavourite = { viewModel.onToggleFavourite(characterId) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}