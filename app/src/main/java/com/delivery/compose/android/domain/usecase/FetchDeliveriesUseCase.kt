package com.delivery.compose.android.domain.usecase

import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FetchDeliveriesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    private val _deliveries = MutableStateFlow<List<Delivery>>(emptyList())
    val deliveries: Flow<List<Delivery>> = _deliveries.asStateFlow()

    suspend fun invoke(page: Int, limit: Int = 20) =
        try {
            if (page == 1) {
                repository.refreshDeliveries(page, limit)
            } else {
                repository.loadMoreDeliveries(page, limit)
            }

            // Get a single snapshot of the current deliveries
            _deliveries.value = repository.getDeliveries().first()
        } catch (e: Exception) {
            throw e
        }
}