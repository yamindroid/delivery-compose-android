package com.delivery.compose.android.data.db.dao

import androidx.room.*
import com.delivery.compose.android.data.db.entity.DeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {
    @Query("SELECT * FROM deliveries")
    fun getDeliveries(): Flow<List<DeliveryEntity>>

    @Query("SELECT * FROM deliveries WHERE id = :id")
    suspend fun getDeliveryById(id: String): DeliveryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveries(deliveries: List<DeliveryEntity>)

    @Update
    suspend fun updateDelivery(delivery: DeliveryEntity)

    @Query("DELETE FROM deliveries")
    suspend fun clearDeliveries()
} 