package com.delivery.compose.android.presentation.navigation

sealed class Screen(val route: String) {
    object DeliveryList : Screen("delivery_list")
    object DeliveryDetail : Screen("delivery_detail/{${NavigationConstants.DELIVERY_ID_ARG}}") {
        fun createRoute(deliveryId: String) = "delivery_detail/$deliveryId"
    }
}