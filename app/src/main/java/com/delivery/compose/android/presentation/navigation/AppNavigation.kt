package com.delivery.compose.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.delivery.compose.android.presentation.deliverydetail.DeliveryDetailScreen
import com.delivery.compose.android.presentation.deliverylist.DeliveryListScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.DeliveryList.route
) {
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(
            route = Screen.DeliveryList.route
        ) {
            DeliveryListScreen(
                onDeliveryClick = { deliveryId ->
                    navigationActions.navigateToDeliveryDetail(deliveryId)
                })
        }

        composable(
            route = Screen.DeliveryDetail.route, arguments = listOf(
                navArgument(NavigationConstants.DELIVERY_ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                })
        ) {
            DeliveryDetailScreen(
                onNavigateBack = navigationActions::navigateBack
            )
        }
    }
}