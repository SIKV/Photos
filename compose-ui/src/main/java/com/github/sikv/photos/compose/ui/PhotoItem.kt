package com.github.sikv.photos.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.sikv.photos.common.ui.ActionIconButton
import com.github.sikv.photos.common.ui.NetworkImage
import com.github.sikv.photos.common.ui.PlaceholderImage
import com.github.sikv.photos.common.ui.getAttributionPlaceholderBackgroundColor
import com.github.sikv.photos.common.ui.getAttributionPlaceholderTextColor
import com.github.sikv.photos.domain.Photo

@Composable
fun PhotoItem(
    photo: Photo,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onAttributionClick: () -> Unit,
    onMoreClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(Spacing.One))
            Attribution(
                photo = photo,
                onAttributionClick = onAttributionClick
            )
            IconButton(
                modifier = Modifier
                    .background(Color.Transparent, shape = CircleShape),
                onClick = onMoreClick
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.One))

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
        Row {
            FavoriteButton(
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
            IconButton(
                modifier = Modifier
                    .background(Color.Transparent, shape = CircleShape),
                onClick = onShareClick
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(id = R.string.share)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ActionIconButton(
                icon = R.drawable.ic_file_download_24dp,
                contentDescription = R.string.download,
                onClick = onDownloadClick
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Two))
    }
}

@Composable
private fun RowScope.Attribution(
    photo: Photo,
    onAttributionClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onAttributionClick)
            .padding(end = Spacing.One)
            .weight(1f)
    ) {
        val photographerImageUrl = photo.getPhotoPhotographerImageUrl()

        val modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)

        if (photographerImageUrl != null) {
            NetworkImage(
                imageUrl = photographerImageUrl,
                modifier = modifier
            )
        } else {
            PlaceholderImage(
                text = photo.getPhotoPhotographerName().first().uppercaseChar().toString(),
                textColor = getAttributionPlaceholderTextColor(LocalContext.current),
                backgroundColor = getAttributionPlaceholderBackgroundColor(LocalContext.current),
                modifier = modifier
            )
        }
        Spacer(modifier = Modifier.width(Spacing.One))
        Column {
            Text(photo.getPhotoPhotographerName(),
                style = MaterialTheme.typography.titleSmall
            )
            Text(photo.getPhotoSource().title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
