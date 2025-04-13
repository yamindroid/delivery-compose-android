package com.delivery.compose.android.data.mapper

import com.delivery.compose.android.data.api.model.DeliveryDto
import com.delivery.compose.android.data.db.entity.DeliveryEntity
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.model.Route
import com.delivery.compose.android.domain.model.Sender

fun DeliveryEntity.toDomain(): Delivery {
    return Delivery(
        id = id,
        goodsPicture = goodsPicture,
        remarks = remarks,
        deliveryFee = deliveryFee,
        surcharge = surcharge,
        route = route,
        sender = sender,
        pickupTime = pickupTime,
        isFavorite = isFavorite
    )
}

fun DeliveryDto.toEntity(): DeliveryEntity {
    return DeliveryEntity(
        id = id,
        goodsPicture = goodsPicture,
        remarks = remarks,
        deliveryFee = deliveryFee,
        surcharge = surcharge,
        route = Route(
            start = route.start,
            end = route.end
        ),
        sender = Sender(
            phone = sender.phone,
            name = sender.name,
            email = sender.email
        ),
        pickupTime = pickupTime,
        isFavorite = false
    )
}