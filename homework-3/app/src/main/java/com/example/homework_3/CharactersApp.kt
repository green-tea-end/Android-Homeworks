package com.example.homework_3

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homework_3.ui.screen.CharacterListScreen
import com.example.homework_3.ui.screen.CharacterDetailScreen
import com.example.homework_3.ui.viewmodel.CharactersViewModel

sealed class CharactersRoute(val route: String) {
    object List : CharactersRoute("list")
    object Detail : CharactersRoute("detail/{characterId}") {
        const val ARG_ID = "characterId"
        fun createRoute(id: String): String = "detail/$id"
    }
}

@Composable
fun CharactersApp() {
    val navController = rememberNavController()
    val viewModel: CharactersViewModel = viewModel()
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

            val character = viewModel.visibleCharacters.find { it.id == characterId }
            val isFavourite = characterId in state.favourites

            CharacterDetailScreen(
                character = character,
                isFavourite = isFavourite,
                isLoading = false,
                errorMessage = null,
                onToggleFavourite = { viewModel.onToggleFavourite(characterId) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
