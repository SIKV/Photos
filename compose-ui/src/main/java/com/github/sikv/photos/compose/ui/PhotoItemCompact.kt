package com.github.sikv.photos.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.github.sikv.photos.common.ui.NetworkImage
import com.github.sikv.photos.domain.Photo

@Composable
fun PhotoItemCompact(
    photo: Photo,
    onClick: () -> Unit
) {
    NetworkImage(
        imageUrl = photo.getPhotoPreviewUrl(),
        loading = {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(colorResource(id = R.color.colorPlaceholder))
            )
        },
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    )
}
