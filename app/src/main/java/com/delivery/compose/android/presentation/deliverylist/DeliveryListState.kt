package com.delivery.compose.android.presentation.deliverylist

import com.delivery.compose.android.domain.model.Delivery

/**
 * Represents the UI state for the delivery list screen
 */
sealed class DeliveryListState {
    /**
     * Initial loading state when fetching the first page
     */
    data object Loading : DeliveryListState()

    /**
     * Success state containing the list of deliveries
     * @property deliveries List of deliveries to display
     * @property isLoadingMore Whether more items are being loaded
     * @property hasNetworkError Whether there is a network connectivity issue
     */
    data class Success(
        val deliveries: List<Delivery>,
        val isLoadingMore: Boolean = false,
        val hasNetworkError: Boolean = false
    ) : DeliveryListState()

    /**
     * Error state when loading fails
     * @property message The error message to display
     */
    data class Error(val message: String) : DeliveryListState()
}