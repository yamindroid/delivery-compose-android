package com.delivery.compose.android.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.delivery.compose.android.domain.model.Route
import com.delivery.compose.android.domain.model.Sender

@Entity(tableName = "deliveries")
data class DeliveryEntity(
    @PrimaryKey
    val id: String,
    val goodsPicture: String,
    val remarks: String,
    val deliveryFee: String,
    val surcharge: String,
    @Embedded(prefix = "route_") val route: Route,
    @Embedded(prefix = "sender_") val sender: Sender,
    val pickupTime: String,
    val isFavorite: Boolean = false
)