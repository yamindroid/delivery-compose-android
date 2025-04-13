package com.delivery.compose.android.data.repository

import com.delivery.compose.android.data.api.DeliveryApi
import com.delivery.compose.android.data.db.dao.DeliveryDao
import com.delivery.compose.android.data.mapper.toDomain
import com.delivery.compose.android.data.mapper.toEntity
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.repository.DeliveryRepository
import com.delivery.compose.android.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val api: DeliveryApi,
    private val dao: DeliveryDao,
    private val networkUtils: NetworkUtils
) : DeliveryRepository {

    override suspend fun getDeliveries(): Flow<List<Delivery>> =
        dao.getDeliveries().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun refreshDeliveries(page: Int, limit: Int) {
        if (!networkUtils.isNetworkAvailable()) {
            return // Avoid no host exception
        }

        try {
            val deliveries = api.getDeliveries(page, limit)
            if (page == 1) {
                // Clear existing data only when loading first page
                dao.clearDeliveries()
            }
            dao.insertDeliveries(deliveries.map { it.toEntity() })

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun loadMoreDeliveries(page: Int, limit: Int) {
        if (!networkUtils.isNetworkAvailable()) {
            return // Avoid no host exception
        }

        try {
            // Only load more if network is available
            val deliveries = api.getDeliveries(page, limit)
            dao.insertDeliveries(deliveries.map { it.toEntity() })
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun toggleFavorite(id: String) {
        dao.getDeliveryById(id)?.let {
            dao.updateDelivery(it.copy(isFavorite = !it.isFavorite))
        }
    }

    override suspend fun getDeliveryById(id: String): Delivery? =
        dao.getDeliveryById(id)?.toDomain()
}