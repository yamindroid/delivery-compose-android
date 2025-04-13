package com.delivery.compose.android.domain.usecase

import com.delivery.compose.android.domain.repository.DeliveryRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(deliveryId: String?) =
        deliveryId?.let {
            repository.toggleFavorite(deliveryId)
        }
} 