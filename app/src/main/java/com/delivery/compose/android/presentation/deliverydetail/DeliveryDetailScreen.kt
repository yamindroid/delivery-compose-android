package com.delivery.compose.android.presentation.deliverydetail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.delivery.compose.android.R
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.presentation.components.ErrorView
import com.delivery.compose.android.presentation.components.RouteVisualization
import com.delivery.compose.android.presentation.deliverydetail.components.*
import com.delivery.compose.android.ui.theme.DarkGrayBackground
import com.delivery.compose.android.ui.theme.LightGrayBackground
import com.delivery.compose.android.ui.theme.WhiteColor

/**
 * Main screen for displaying delivery details
 *
 * @param onNavigateBack Callback for handling back navigation
 * @param viewModel ViewModel instance for this screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: DeliveryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DeliveryTopBar(
                onNavigateBack = onNavigateBack,
                delivery = (state as? DeliveryDetailState.Success)?.delivery,
                onFavoriteClick = viewModel::toggleFavorite,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (state is DeliveryDetailState.Success) {
                BottomFavoriteBar(
                    delivery = (state as DeliveryDetailState.Success).delivery,
                    onFavoriteClick = viewModel::toggleFavorite
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is DeliveryDetailState.Loading -> LoadingIndicator()
                is DeliveryDetailState.Error -> ErrorView(
                    message = (state as DeliveryDetailState.Error).message,
                    onRetry = viewModel::loadDelivery
                )

                is DeliveryDetailState.Success -> DeliveryContent(
                    delivery = (state as DeliveryDetailState.Success).delivery
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryTopBar(
    onNavigateBack: () -> Unit,
    delivery: Delivery?,
    onFavoriteClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.screen_delivery_detail),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.content_description_navigation_back)
                )
            }
        },
        actions = {
            delivery?.let {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large))
                ) {
                    Icon(
                        imageVector = if (it.isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(if (it.isFavorite) R.string.action_remove_from_favorite else R.string.action_add_to_favorite),
                        tint = if (it.isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            Color(0xFF757575),
                        modifier = Modifier.size(dimensionResource(R.dimen.spacing_large))
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isSystemInDarkTheme()) DarkGrayBackground else WhiteColor,
            scrolledContainerColor = if (isSystemInDarkTheme()) DarkGrayBackground else WhiteColor,
            navigationIconContentColor = if (isSystemInDarkTheme()) LightGrayBackground else DarkGrayBackground,
            titleContentColor = if (isSystemInDarkTheme()) LightGrayBackground else DarkGrayBackground,
            actionIconContentColor = if (isSystemInDarkTheme()) LightGrayBackground else DarkGrayBackground
        ),
        modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.elevation_medium)),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun DeliveryContent(
    delivery: Delivery,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensionResource(R.dimen.spacing_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxsmall)))

        RouteInfoSection(delivery)
        GoodsInfoSection(delivery)
        PricingSection(delivery)

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxsmall)))
    }
}

@Composable
private fun RouteInfoSection(delivery: Delivery) {
    DeliveryCard(
        title = stringResource(R.string.section_route_info)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            RouteVisualization(
                modifier = Modifier.width(dimensionResource(R.dimen.icon_size_medium))
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xxlarge))
            ) {
                LabeledText(
                    label = stringResource(R.string.label_pickup_location),
                    text = delivery.route.start
                )
                LabeledText(
                    label = stringResource(R.string.label_dropoff_location),
                    text = delivery.route.end
                )
            }
        }
    }
}

@Composable
private fun GoodsInfoSection(delivery: Delivery) {
    DeliveryCard(
        title = stringResource(R.string.section_goods_info)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            DeliveryImage(imageUrl = delivery.goodsPicture)

            LabeledText(
                label = stringResource(R.string.label_remarks),
                text = delivery.remarks
            )
        }
    }
}

@Composable
private fun PricingSection(delivery: Delivery) {
    DeliveryCard(
        title = stringResource(R.string.section_pricing)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            PriceRow(
                label = stringResource(R.string.label_delivery_fee),
                amount = delivery.deliveryFee
            )

            PriceRow(
                label = stringResource(R.string.label_surcharge),
                amount = delivery.surcharge
            )

            Divider(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                thickness = dimensionResource(R.dimen.divider_height)
            )

            PriceRow(
                label = stringResource(R.string.label_total),
                amount = delivery.getFormattedPrice(),
                isTotal = true
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun BottomFavoriteBar(
    delivery: Delivery,
    onFavoriteClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isSystemInDarkTheme()) DarkGrayBackground else WhiteColor,
        shadowElevation = dimensionResource(R.dimen.elevation_xlarge),
        shape = RoundedCornerShape(
            topStart = dimensionResource(R.dimen.card_corner_radius_medium),
            topEnd = dimensionResource(R.dimen.card_corner_radius_medium)
        )
    ) {
        AddToFavoriteButton(
            isFavorite = delivery.isFavorite,
            onFavoriteClick = onFavoriteClick,
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        )
    }
}