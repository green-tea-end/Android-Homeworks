package com.example.homework_2

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homework_2.ui.screen.InputScreen
import com.example.homework_2.ui.screen.ResultScreen
import com.example.homework_2.ui.screen.WelcomeScreen
import com.example.homework_2.ui.viewmodel.SplitViewModel

sealed class SplitRoute(val route: String) {
    object Welcome : SplitRoute("welcome")
    object Input : SplitRoute("input")
    object Result : SplitRoute("result/{calcId}") {
        const val ARG_CALC_ID = "calcId"
        fun createRoute(calcId: String): String = "result/$calcId"
    }
}

@Composable
fun SplitMateApp() {
    val viewModel: SplitViewModel = viewModel()
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = SplitRoute.Welcome.route) {

        composable(route = SplitRoute.Welcome.route) {
            WelcomeScreen(
                onStartClick = { navController.navigate(SplitRoute.Input.route) }
            )
        }

        composable(route = SplitRoute.Input.route) {
            val state = viewModel.uiState

            InputScreen(
                state = state,
                onTotalChange = viewModel::onTotalChange,
                onPeopleChange = viewModel::onPeopleChange,
                onCalculate = {
                    val calculation = viewModel.createCalculation()

                    if (calculation != null) {
                        navController.navigate(SplitRoute.Result.createRoute(calculation.id))
                    }

                }
            )
        }

        composable(
            route = SplitRoute.Result.route,
            arguments = listOf(navArgument(SplitRoute.Result.ARG_CALC_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val calcId = backStackEntry.arguments?.getString(SplitRoute.Result.ARG_CALC_ID)
            val calculation = calcId?.let(viewModel::calculationById)

            if (calculation == null) {
                Text("Расчёт не найден")
            } else {
                ResultScreen(
                    calculation = calculation,
                    onBackToEdit = {
                        navController.popBackStack()
                    },
                    onNewCalculation = {
                        viewModel.resetForNewCalculation()
                        navController.navigate(SplitRoute.Input.route) {
                            popUpTo(SplitRoute.Input.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}