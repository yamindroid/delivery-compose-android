package com.delivery.compose.android.presentation.deliverydetail

import com.delivery.compose.android.domain.model.Delivery

/**
 * Represents the UI state for the delivery detail screen
 */
sealed class DeliveryDetailState {
    /**
     * Initial loading state
     */
    data object Loading : DeliveryDetailState()

    /**
     * Success state containing the delivery details
     * @property delivery The delivery details to display
     */
    data class Success(val delivery: Delivery) : DeliveryDetailState()

    /**
     * Error state when loading fails
     * @property message The error message to display
     */
    data class Error(val message: String) : DeliveryDetailState()
}