package com.example.homework_3

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homework_3.ui.screen.CountryDetailScreen
import com.example.homework_3.ui.screen.CountryListScreen
import com.example.homework_3.ui.viewmodel.CountriesViewModel

sealed class CountriesRoute(val route: String) {
    object List : CountriesRoute("list")
    object Detail : CountriesRoute("detail/{countryId}") {
        const val ARG_COUNTRY_ID = "countryId"
        fun createRoute(countryId: String): String = "detail/$countryId"
    }
}

@Composable
fun CountriesApp() {
    val navController = rememberNavController()
    val viewModel: CountriesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = CountriesRoute.List.route
    ) {
        composable(CountriesRoute.List.route) {
            CountryListScreen(
                state = viewModel.uiState,
                countries = viewModel.visibleCountries,
                onSearchChange = viewModel::onQueryChange,
                onFilterChange = viewModel::onFilterChange,
                onToggleFavourite = viewModel::onToggleFavourite,
                onCountryClick = { countryId ->
                    navController.navigate(
                        CountriesRoute.Detail.createRoute(countryId)
                    )
                },
                onRetry = {
                    // Добавляем обработчик повторной попытки
                    viewModel.retryLoad()
                }
            )
        }

        composable(
            route = CountriesRoute.Detail.route,
            arguments = listOf(
                navArgument(CountriesRoute.Detail.ARG_COUNTRY_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val countryId = backStackEntry.arguments?.getString(
                CountriesRoute.Detail.ARG_COUNTRY_ID
            ) ?: return@composable

            val country = viewModel.visibleCountries.find { it.id == countryId }

            if (country != null) {
                CountryDetailScreen(
                    country = country,
                    isFavourite = country.id in viewModel.uiState.favourites,
                    onToggleFavourite = { viewModel.onToggleFavourite(country.id) },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}