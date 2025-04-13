package com.delivery.compose.android.presentation.deliverylist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.delivery.compose.android.R
import com.delivery.compose.android.ui.theme.DarkGrayBackground
import com.delivery.compose.android.ui.theme.WhiteColor

/**
 * Top app bar for the delivery list screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryListTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = stringResource(R.string.content_description_app_icon),
                    modifier = Modifier.size(dimensionResource(R.dimen.toolbar_icon_size))
                )
                Text(
                    text = stringResource(R.string.screen_delivery_list),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_small))
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isSystemInDarkTheme()) DarkGrayBackground else WhiteColor,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(dimensionResource(R.dimen.elevation_xlarge))
    )
}