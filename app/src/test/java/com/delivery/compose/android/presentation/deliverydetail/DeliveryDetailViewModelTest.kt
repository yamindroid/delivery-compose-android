package com.delivery.compose.android.presentation.deliverydetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.model.Route
import com.delivery.compose.android.domain.model.Sender
import com.delivery.compose.android.domain.usecase.GetDeliveryByIdUseCase
import com.delivery.compose.android.domain.usecase.ToggleFavoriteUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryDetailViewModelTest {

    @MockK
    private lateinit var getDeliveryByIdUseCase: GetDeliveryByIdUseCase

    @MockK
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: DeliveryDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

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
        Dispatchers.setMain(testDispatcher)

        // Default mock behaviors
        coEvery {
            checkNotNull(savedStateHandle["deliveryId"]) {
                "deliveryId parameter is required"
            }
        } returns "1"

        viewModel = DeliveryDetailViewModel(
            getDeliveryByIdUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleFavorite updates delivery favorite status`() = runTest {
        // Given
        val deliveryId = "1"

        coEvery { getDeliveryByIdUseCase.invoke(deliveryId) } returns mockDelivery
        coEvery { toggleFavoriteUseCase.invoke(deliveryId) } returns Unit

        // Ensure the state is set to Success before calling toggleFavorite
        viewModel.loadDelivery()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { toggleFavoriteUseCase.invoke(deliveryId) }

        // Verify that the getDeliveryByIdUseCase.invoke(deliveryId) method is called exactly three times:
        coVerify(exactly = 3) { getDeliveryByIdUseCase.invoke(deliveryId) }
    }


    @Test
    fun `loadDelivery handles network error gracefully`() = runTest {
        // Given
        val deliveryId = "1"
        val errorMessage = "Network error"
        coEvery { getDeliveryByIdUseCase.invoke(deliveryId) } throws Exception(errorMessage)

        // When
        viewModel.loadDelivery()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat((state as DeliveryDetailState.Error).message).isEqualTo(errorMessage)
        }
    }
}