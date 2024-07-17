package com.github.sikv.photos.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.github.sikv.photos.domain.ListLayout

// TODO: Add shimmer animation.
@Composable
fun ShimmerPhotoItem(
    listLayout: ListLayout
) {
    val modifier = when (listLayout) {
        ListLayout.LIST -> Modifier.padding(vertical = Spacing.One)
        ListLayout.GRID -> Modifier.padding(
            top = Spacing.Half,
            end = Spacing.Half,
        )
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .background(colorResource(id = R.color.colorPlaceholder))
        )
    }
}
