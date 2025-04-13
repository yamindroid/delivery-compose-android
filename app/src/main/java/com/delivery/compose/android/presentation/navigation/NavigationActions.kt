package com.delivery.compose.android.presentation.navigation

import androidx.navigation.NavHostController

class NavigationActions(private val navController: NavHostController) {

    fun navigateToDeliveryDetail(deliveryId: String) =
        navController.navigate(Screen.DeliveryDetail.createRoute(deliveryId))

    fun navigateBack() = navController.popBackStack()
}