package com.delivery.compose.android.presentation.deliverydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delivery.compose.android.domain.usecase.GetDeliveryByIdUseCase
import com.delivery.compose.android.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the delivery detail screen
 */
@HiltViewModel
class DeliveryDetailViewModel @Inject constructor(
    private val getDeliveryByIdUseCase: GetDeliveryByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deliveryId: String = checkNotNull(savedStateHandle["deliveryId"]) {
        "deliveryId parameter is required"
    }

    private val _state = MutableStateFlow<DeliveryDetailState>(DeliveryDetailState.Loading)
    val state: StateFlow<DeliveryDetailState> = _state.asStateFlow()

    init {
        loadDelivery()
    }

    fun loadDelivery() = viewModelScope.launch {
        try {
            getDeliveryByIdUseCase.invoke(deliveryId)?.let { delivery ->
                _state.update { DeliveryDetailState.Success(delivery) }
            }
        } catch (e: Exception) {
            _state.update {
                DeliveryDetailState.Error(
                    e.message ?: "Failed to load delivery details"
                )
            }
        }
    }

    fun toggleFavorite() {
        val currentState = state.value
        if (currentState !is DeliveryDetailState.Success) return

        viewModelScope.launch {
            try {
                toggleFavoriteUseCase.invoke(currentState.delivery.id)
                // Reload to get updated state
                loadDelivery()
            } catch (e: Exception) {
                _state.update {
                    DeliveryDetailState.Error(
                        e.message ?: "Failed to toggle favorite"
                    )
                }
            }
        }
    }
}