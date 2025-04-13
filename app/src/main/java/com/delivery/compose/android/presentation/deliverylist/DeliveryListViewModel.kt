package com.delivery.compose.android.presentation.deliverylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.compose.android.domain.usecase.FetchDeliveriesUseCase
import com.delivery.compose.android.domain.usecase.GetDeliveriesUseCase
import com.delivery.compose.android.domain.usecase.ToggleFavoriteUseCase
import com.delivery.compose.android.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the delivery list screen
 */
@HiltViewModel
class DeliveryListViewModel @Inject constructor(
    private val fetchDeliveriesUseCase: FetchDeliveriesUseCase,
    private val getDeliveriesUseCase: GetDeliveriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _state = MutableStateFlow<DeliveryListState>(DeliveryListState.Loading)
    val state: StateFlow<DeliveryListState> = _state.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    init {
        loadDeliveries()
    }

    fun loadDeliveries() = viewModelScope.launch {
        _state.update { DeliveryListState.Loading }

        try {
            // Reset pagination
            currentPage = 1

            // Fetch from network and store locally
            fetchDeliveriesUseCase.invoke(currentPage, pageSize)

            // Observe local database changes
            getDeliveriesUseCase.invoke().collect { deliveries ->
                _state.update {
                    DeliveryListState.Success(
                        deliveries = deliveries,
                        hasNetworkError = !networkUtils.isNetworkAvailable()
                    )
                }
            }
        } catch (e: Exception) {
            _state.update {
                DeliveryListState.Error(
                    message = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun loadMoreDeliveries() {
        val currentState = state.value
        if (currentState !is DeliveryListState.Success || currentState.isLoadingMore) return

        viewModelScope.launch {
            try {
                // Update state to show loading more
                _state.update { currentState.copy(isLoadingMore = true) }

                // Load next page
                currentPage++
                fetchDeliveriesUseCase.invoke(currentPage, pageSize)

                // Get updated list
                getDeliveriesUseCase.invoke().collect { deliveries ->
                    _state.update {
                        DeliveryListState.Success(
                            deliveries = deliveries, isLoadingMore = false, hasNetworkError = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    DeliveryListState.Error(
                        message = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun toggleFavorite(deliveryId: String) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(deliveryId)
            } catch (e: Exception) {
                // Show error but keep current state
                val currentState = state.value
                if (currentState is DeliveryListState.Success) {
                    _state.update {
                        currentState.copy(
                            hasNetworkError = !networkUtils.isNetworkAvailable()
                        )
                    }
                }
            }
        }
    }

    fun dismissNetworkError() {
        val currentState = state.value
        if (currentState is DeliveryListState.Success) {
            _state.update { currentState.copy(hasNetworkError = false) }
        }
    }
}