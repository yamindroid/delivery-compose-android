package com.delivery.compose.android.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.delivery.compose.android.R
import com.delivery.compose.android.ui.theme.DeepOrange40
import com.delivery.compose.android.ui.theme.GreenColor

/**
 * A reusable component for visualizing a route with start and end points
 * connected by a dotted line.
 *
 * @param modifier Modifier to be applied to the component
 * @param startPointColor Color of the start point
 * @param endPointColor Color of the end point
 * @param lineColor Color of the connecting line
 */

@Composable
fun RouteVisualization(
    modifier: Modifier = Modifier,
    startPointColor: Color = MaterialTheme.colorScheme.primary,
    endPointColor: Color = MaterialTheme.colorScheme.secondary,
    lineColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(dimensionResource(R.dimen.icon_size_medium))
    ) {
        // Start point
        RoutePoint(isStartPoint = true)

        // Dotted line
        DottedLine(color = lineColor)

        // End point
        RoutePoint(isStartPoint = false)
    }
}

@Composable
private fun RoutePoint(
    isStartPoint: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isStartPoint) {
        DeepOrange40
    } else {
        GreenColor
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(32.dp)
            .border(
                width = 2.dp,
                color = color,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (isStartPoint) {
                Icons.Default.PinDrop
            } else {
                Icons.Default.LocationOn
            },
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun DottedLine(
    color: Color,
    modifier: Modifier = Modifier
) {
    // Draws a dotted vertical line using a Canvas
    Canvas(
        modifier = modifier
            .width(dimensionResource(R.dimen.route_line_width)) // Set the width of the line
            .height(dimensionResource(R.dimen.route_line_height)) // Set the height of the line
    ) {
        val dashLength = 1.dp.toPx() // Length of each dash
        val gapLength = 4.dp.toPx() // Gap between dashes
        var startY = 0f // Starting Y-coordinate for the line
        val endY = size.height // Ending Y-coordinate for the line
        val x = size.width / 2 // X-coordinate for the center of the line

        // Loop to draw dashes and gaps until the end of the line
        while (startY < endY) {
            drawLine(
                color = color, // Color of the line
                start = Offset(x, startY), // Start point of the dash
                end = Offset(x, (startY + dashLength).coerceAtMost(endY)), // End point of the dash
                strokeWidth = size.width, // Width of the line
                cap = StrokeCap.Round // Rounded ends for the dashes
            )
            startY += dashLength + gapLength // Move to the next dash position
        }
    }
}