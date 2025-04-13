package com.delivery.compose.android.presentation.deliverylist

import com.delivery.compose.android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.presentation.components.CancelableAlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.delivery.compose.android.presentation.components.EmptyView
import com.delivery.compose.android.presentation.deliverylist.components.DeliveryItem
import com.delivery.compose.android.presentation.deliverylist.components.DeliveryListTopBar
import com.delivery.compose.android.presentation.components.ErrorView
import com.delivery.compose.android.presentation.components.LoadingView

@Composable
fun DeliveryListScreen(
    onDeliveryClick: (String) -> Unit,
    viewModel: DeliveryListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItem =
                    layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@collect
                val totalItemsCount = layoutInfo.totalItemsCount
                // Trigger when within last 3 items
                val threshold = 3
                val isLoadingMore = (state as DeliveryListState.Success).isLoadingMore
                if (lastVisibleItem >= totalItemsCount - threshold && !isLoadingMore) {
                    viewModel.loadMoreDeliveries()
                }
            }
    }

    Scaffold(
        topBar = {
            DeliveryListTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is DeliveryListState.Loading -> {
                    LoadingView()
                }

                is DeliveryListState.Success -> {
                    val deliveries = (state as DeliveryListState.Success).deliveries
                    if (deliveries.isEmpty()) {
                        EmptyView(
                            message = stringResource(R.string.error_empty_deliveries),
                            onRetry = viewModel::loadDeliveries
                        )
                    } else {
                        DeliveryList(
                            deliveries = deliveries,
                            onDeliveryClick = onDeliveryClick,
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            isLoadingMore = (state as DeliveryListState.Success).isLoadingMore,
                            showNetworkError = (state as DeliveryListState.Success).hasNetworkError,
                            onDismissNetworkError = { viewModel.dismissNetworkError() },
                            listState = listState
                        )
                    }
                }

                is DeliveryListState.Error -> {
                    ErrorView(
                        message = (state as DeliveryListState.Error).message,
                        onRetry = viewModel::loadDeliveries
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryList(
    deliveries: List<Delivery>,
    onDeliveryClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onDismissNetworkError: () -> Unit,
    isLoadingMore: Boolean,
    showNetworkError: Boolean,
    listState: LazyListState
) {
    if (showNetworkError) {
        CancelableAlertDialog(
            title = stringResource(R.string.dialog_title_network_error),
            message = stringResource(R.string.dialog_description_network_error),
            onDismiss = onDismissNetworkError
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(dimensionResource(R.dimen.spacing_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        items(
            items = deliveries,
            key = { it.id }
        ) { delivery ->
            DeliveryItem(
                delivery = delivery,
                onClick = { onDeliveryClick(delivery.id) },
                onFavoriteClick = { onFavoriteClick(delivery.id) }
            )
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.spacing_medium)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.spacing_large)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}