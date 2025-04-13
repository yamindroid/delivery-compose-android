package com.delivery.compose.android.domain.repository

import com.delivery.compose.android.domain.model.Delivery
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {
    suspend fun getDeliveries(): Flow<List<Delivery>>
    suspend fun refreshDeliveries(page: Int = 1, limit: Int = 20)
    suspend fun loadMoreDeliveries(page: Int, limit: Int = 20)
    suspend fun toggleFavorite(id: String)
    suspend fun getDeliveryById(id: String): Delivery?
}