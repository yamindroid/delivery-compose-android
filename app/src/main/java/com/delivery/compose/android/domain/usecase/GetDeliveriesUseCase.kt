package com.delivery.compose.android.domain.usecase

import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeliveriesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend fun invoke(): Flow<List<Delivery>> = repository.getDeliveries()
} 