package com.delivery.compose.android.data.api.model

import com.google.gson.annotations.SerializedName

data class DeliveryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("goodsPicture")
    val goodsPicture: String,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("deliveryFee")
    val deliveryFee: String,
    @SerializedName("surcharge")
    val surcharge: String,
    @SerializedName("route")
    val route: RouteDto,
    @SerializedName("sender")
    val sender: SenderDto,
    @SerializedName("pickupTime")
    val pickupTime: String
)

data class RouteDto(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String
)

data class SenderDto(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)