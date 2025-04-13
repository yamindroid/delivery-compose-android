package com.delivery.compose.android.domain.model

import android.annotation.SuppressLint

data class Delivery(
    val id: String,
    val remarks: String,
    val pickupTime: String,
    val goodsPicture: String,
    val deliveryFee: String,
    val surcharge: String,
    val route: Route,
    val sender: Sender,
    val isFavorite: Boolean = false
) {
    @SuppressLint("DefaultLocale")
    fun getFormattedPrice(): String {
        val fee = deliveryFee.parseCurrencyAmount()
        val charge = surcharge.parseCurrencyAmount()
        return String.format("$%.2f", fee + charge)
    }
}

data class Route(
    val start: String,
    val end: String
)

data class Sender(
    val phone: String,
    val name: String,
    val email: String
)

private fun String.parseCurrencyAmount(): Double {
    return try {
        this.replace("$", "").trim().toDouble()
    } catch (_: Exception) {
        0.0
    }
}