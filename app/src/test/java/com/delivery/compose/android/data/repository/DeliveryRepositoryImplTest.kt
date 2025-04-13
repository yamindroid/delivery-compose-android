package com.delivery.compose.android.data.repository

import com.delivery.compose.android.data.api.DeliveryApi
import com.delivery.compose.android.data.api.model.DeliveryDto
import com.delivery.compose.android.data.api.model.RouteDto
import com.delivery.compose.android.data.api.model.SenderDto
import com.delivery.compose.android.data.db.dao.DeliveryDao
import com.delivery.compose.android.data.db.entity.DeliveryEntity
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.model.Route
import com.delivery.compose.android.domain.model.Sender
import com.delivery.compose.android.utils.NetworkUtils
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryRepositoryImplTest {

    @MockK
    private lateinit var api: DeliveryApi

    @MockK
    private lateinit var dao: DeliveryDao

    @MockK
    private lateinit var networkUtils: NetworkUtils

    private lateinit var repository: DeliveryRepositoryImpl

    private val mockDeliveryDto = DeliveryDto(
        id = "1",
        route = RouteDto("Start A", "End A"),
        deliveryFee = "100.00",
        goodsPicture = "url",
        pickupTime = "2024-02-29T10:00:00Z",
        remarks = "Test remarks",
        surcharge = "40.00",
        sender = SenderDto("1234567890", "John Doe", "test@example.com")
    )

    private val mockDeliveryEntity = DeliveryEntity(
        id = "1",
        route = Route("Start A", "End A"),
        deliveryFee = "100.00",
        goodsPicture = "url",
        pickupTime = "2024-02-29T10:00:00Z",
        remarks = "Test remarks",
        surcharge = "40.00",
        sender = Sender("1234567890", "John Doe", "test@example.com"),
        isFavorite = false
    )

    private val mockDelivery = Delivery(
        id = "1",
        route = Route("Start A", "End A"),
        deliveryFee = "100.00",
        goodsPicture = "url",
        pickupTime = "2024-02-29T10:00:00Z",
        isFavorite = false,
        remarks = "Test remarks",
        surcharge = "40.00",
        sender = Sender("1234567890", "John Doe", "test@example.com")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        repository = DeliveryRepositoryImpl(api, dao, networkUtils)

        // Default mock behaviors
        coEvery { networkUtils.isNetworkAvailable() } returns true
        coEvery { api.getDeliveries(any()) } returns listOf(mockDeliveryDto)
        coEvery { dao.getDeliveries() } returns flowOf(listOf(mockDeliveryEntity))
        coEvery { dao.insertDeliveries(any()) } just runs
        coEvery { dao.clearDeliveries() } just runs
        coEvery { dao.getDeliveryById(any()) } returns mockDeliveryEntity
        coEvery { dao.updateDelivery(any()) } just runs
    }

    @Test
    fun `refreshDeliveries success saves to database`() = runTest {
        // Given
        val page = 1
        coEvery { api.getDeliveries(page) } returns listOf(mockDeliveryDto)

        // When
        repository.refreshDeliveries(page)

        // Then
        coVerify { dao.insertDeliveries(any()) }
    }

    @Test
    fun `getDeliveries returns mapped deliveries from database`() = runTest {
        // Given
        coEvery { dao.getDeliveries() } returns flowOf(listOf(mockDeliveryEntity))

        // When
        val result = repository.getDeliveries().first()

        // Then
        Truth.assertThat(result).hasSize(1)
        Truth.assertThat(result.first().id).isEqualTo(mockDelivery.id)
        Truth.assertThat(result.first().route.start).isEqualTo(mockDelivery.route.start)
        Truth.assertThat(result.first().route.end).isEqualTo(mockDelivery.route.end)
    }

    @Test
    fun `getDeliveryById returns correct delivery`() = runTest {
        // Given
        val deliveryId = "1"
        coEvery { dao.getDeliveryById(deliveryId) } returns mockDeliveryEntity

        // When
        val result = repository.getDeliveryById(deliveryId)

        // Then
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.id).isEqualTo(deliveryId)
    }

    @Test
    fun `getDeliveryById returns null for non-existent delivery`() = runTest {
        // Given
        val nonExistentId = "999"
        coEvery { dao.getDeliveryById(nonExistentId) } returns null

        // When
        val result = repository.getDeliveryById(nonExistentId)

        // Then
        Truth.assertThat(result).isNull()
    }

    @Test
    fun `toggleFavorite updates delivery favorite status`() = runTest {
        // Given
        val deliveryId = "1"
        coEvery { dao.getDeliveryById(deliveryId) } returns mockDeliveryEntity
        coEvery { dao.updateDelivery(any()) } just runs

        // When
        repository.toggleFavorite(deliveryId)

        // Then
        coVerify { dao.updateDelivery(any()) }
    }

    @Test
    fun `refreshDeliveries handles empty response`() = runTest {
        // Given
        coEvery { api.getDeliveries(any()) } returns emptyList()

        // When
        repository.refreshDeliveries(1)

        // Then
        coVerify(exactly = 1) { dao.insertDeliveries(any()) }
    }

    @Test
    fun `refreshDeliveries handles network error`() = runTest {
        // Given
        coEvery { api.getDeliveries(any()) } throws Exception("Network error")

        // When & Then
        try {
            repository.refreshDeliveries(1)
        } catch (e: Exception) {
            Truth.assertThat(e.message).isEqualTo("Network error")
        }
    }
}