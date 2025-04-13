package com.delivery.compose.android.data.api

import com.delivery.compose.android.data.api.model.DeliveryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DeliveryApi {
    @GET("deliveries")
    suspend fun getDeliveries(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): List<DeliveryDto>
}