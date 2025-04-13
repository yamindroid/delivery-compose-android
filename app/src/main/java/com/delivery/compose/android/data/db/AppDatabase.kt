package com.delivery.compose.android.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.delivery.compose.android.data.db.dao.DeliveryDao
import com.delivery.compose.android.data.db.entity.DeliveryEntity

@Database(
    entities = [DeliveryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao
}