package com.delivery.compose.android.domain.usecase

import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.repository.DeliveryRepository
import javax.inject.Inject

class GetDeliveryByIdUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend fun invoke(deliveryId: String): Delivery? = repository.getDeliveryById(deliveryId)
} 