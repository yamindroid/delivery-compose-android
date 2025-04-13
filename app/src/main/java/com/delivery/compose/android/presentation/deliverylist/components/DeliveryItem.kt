package com.delivery.compose.android.presentation.deliverylist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.delivery.compose.android.R
import com.delivery.compose.android.domain.model.Delivery
import com.delivery.compose.android.ui.theme.DarkGrayBackground
import com.delivery.compose.android.ui.theme.DeepOrange40
import com.delivery.compose.android.ui.theme.GreenColor
import com.delivery.compose.android.ui.theme.WhiteColor
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A card displaying delivery information in the list
 *
 * @param delivery The delivery to display
 * @param onClick Callback when the card is clicked
 * @param onFavoriteClick Callback when the favorite button is clicked
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun DeliveryItem(
    delivery: Delivery,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius_small)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.elevation_medium)
        ),
        colors = CardDefaults.cardColors(
            containerColor =  if (isSystemInDarkTheme()) DarkGrayBackground else WhiteColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            DeliveryHeader(
                pickupTime = delivery.pickupTime,
                isFavorite = delivery.isFavorite,
                onFavoriteClick = onFavoriteClick
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            DeliveryRouteInfo(
                delivery = delivery,
                imageUrl = delivery.goodsPicture
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            DeliveryFooter(
                price = delivery.getFormattedPrice(),
                onClick = onClick
            )
        }
    }
}

@Composable
private fun DeliveryHeader(
    pickupTime: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeliveryTimeText(pickupTime = pickupTime)
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(
                    if (isFavorite) R.string.action_remove_from_favorite
                    else R.string.action_add_to_favorite
                ),
                tint = if (isFavorite) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DeliveryRouteInfo(
    delivery: Delivery,
    imageUrl: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.content_description_delivery_image),
            modifier = Modifier
                .size(dimensionResource(R.dimen.icon_size_xlarge))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius_medium))),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LocationRow(
                text = delivery.route.start,
                isStartLocation = true
            )
            LocationRow(
                text = delivery.route.end,
                isStartLocation = false
            )
        }
    }
}

@Composable
private fun DeliveryFooter(
    price: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = price,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.action_delivery_details),
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(dimensionResource(R.dimen.spacing_xsmall)))
                .clickable(
                    onClick = onClick,
                    indication = rememberRipple(bounded = true),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(dimensionResource(R.dimen.spacing_xsmall))
        )
    }
}

@Composable
private fun LocationRow(
    text: String,
    isStartLocation: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        val routeStartColor = DeepOrange40
        val routeEndColor = GreenColor

        Icon(
            imageVector = if (isStartLocation) Icons.Default.PinDrop else Icons.Default.LocationOn,
            contentDescription = null,
            tint = if (isStartLocation) routeStartColor else routeEndColor,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_xsmall))
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DeliveryTimeText(pickupTime: String) {
    val localDateTime = OffsetDateTime.parse(pickupTime)
        .toInstant()
        .atZone(ZoneId.systemDefault())

    val formatter = DateTimeFormatter.ofPattern(
        stringResource(R.string.format_date),
        Locale.getDefault()
    )

    Text(
        text = localDateTime.format(formatter),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}