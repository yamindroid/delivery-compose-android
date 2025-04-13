package com.delivery.compose.android.presentation.deliverylist

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.domain.model.Route
import com.delivery.compose.android.domain.model.Sender
import com.delivery.compose.android.domain.usecase.FetchDeliveriesUseCase
import com.delivery.compose.android.domain.usecase.GetDeliveriesUseCase
import com.delivery.compose.android.domain.usecase.ToggleFavoriteUseCase
import com.delivery.compose.android.utils.NetworkUtils
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryListViewModelTest {

    @MockK
    private lateinit var fetchDeliveriesUseCase: FetchDeliveriesUseCase

    @MockK
    private lateinit var getDeliveriesUseCase: GetDeliveriesUseCase

    @MockK
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @MockK
    private lateinit var networkUtils: NetworkUtils

    private lateinit var viewModel: DeliveryListViewModel

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
        coEvery { networkUtils.isNetworkAvailable() } returns true
        coEvery { getDeliveriesUseCase.invoke() } returns flowOf(emptyList())
        coEvery { fetchDeliveriesUseCase.invoke(any()) } returns Unit

        viewModel = DeliveryListViewModel(
            fetchDeliveriesUseCase,
            getDeliveriesUseCase,
            toggleFavoriteUseCase,
            networkUtils
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDeliveries with network error shows error state`() = runTest {
        // Given
        coEvery { networkUtils.isNetworkAvailable() } returns false

        // When
        viewModel.loadDeliveries()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat((state as DeliveryListState.Success).hasNetworkError).isTrue()
        }
    }

    @Test
    fun `loadMoreDeliveries adds more deliveries to state`() = runTest {
        // Given
        val initialDeliveries = listOf(mockDelivery)
        val additionalDelivery = mockDelivery.copy(id = "2")
        val moreDeliveries = listOf(additionalDelivery)

        coEvery { getDeliveriesUseCase.invoke() } returns flowOf(initialDeliveries + moreDeliveries)
        coEvery { fetchDeliveriesUseCase.invoke(1) } returns Unit

        // When
        viewModel.loadMoreDeliveries()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat((state as DeliveryListState.Success).deliveries).hasSize(2)
            assertThat(state.isLoadingMore).isFalse()
        }
    }

    @Test
    fun `toggleFavorite updates delivery favorite status`() = runTest {
        // Given
        val deliveryId = "1"
        coEvery { toggleFavoriteUseCase.invoke(deliveryId) } returns Unit
        coEvery { getDeliveriesUseCase.invoke() } returns flowOf(listOf(mockDelivery))

        // When
        viewModel.toggleFavorite(deliveryId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { toggleFavoriteUseCase.invoke(deliveryId) }
    }

    @Test
    fun `dismissNetworkError updates state`() = runTest {
        // Given
        coEvery { networkUtils.isNetworkAvailable() } returns false

        viewModel.loadDeliveries()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dismissNetworkError()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat((state as DeliveryListState.Success).hasNetworkError).isFalse()
        }
    }
}