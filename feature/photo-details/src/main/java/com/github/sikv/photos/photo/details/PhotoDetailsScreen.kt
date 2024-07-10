package com.github.sikv.photos.photo.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.sikv.photos.common.ui.ActionIconButton
import com.github.sikv.photos.common.ui.NetworkImage
import com.github.sikv.photos.common.ui.PlaceholderImage
import com.github.sikv.photos.common.ui.TransparentTopAppBar
import com.github.sikv.photos.common.ui.getAttributionPlaceholderBackgroundColor
import com.github.sikv.photos.common.ui.getAttributionPlaceholderTextColor
import com.github.sikv.photos.domain.Photo

private const val imageRevealDuration = 1000
private const val actionableContentAlpha = 0.9f
private const val favoriteAnimationDuration = 100
private const val unFavoriteAnimationDuration = 400

@Composable
internal fun PhotoDetailsScreen(
    onBackClick: () -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit,
    onSetWallpaperClick: (Photo) -> Unit,
    viewModel: PhotoDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box {
        NetworkImage(
            imageUrl = uiState.photo.getPhotoFullPreviewUrl(),
            revealDuration = imageRevealDuration,
            modifier = Modifier
                .fillMaxSize()
        )
        TransparentTopAppBar(
            onBackPressed = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
        )
        ActionableContent(
            photo = uiState.photo,
            isFavorite = uiState.isFavorite,
            onToggleFavorite = viewModel::toggleFavorite,
            onShareClick = {
                onSharePhotoClick(uiState.photo)
            },
            onDownloadClick = {
                onDownloadPhotoClick(uiState.photo)
            },
            onSetWallpaperClick = {
                onSetWallpaperClick(uiState.photo)
            },
            onAttributionClick = {
                onPhotoAttributionClick(uiState.photo)
            },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(12.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = actionableContentAlpha),
                    shape = Shapes().large,
                )
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ActionableContent(
    photo: Photo,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onSetWallpaperClick: () -> Unit,
    onAttributionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Attribution(
                photo = photo,
                onAttributionClick = onAttributionClick
            )
            SecondaryActions(
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                onSharePressed = onShareClick
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryActions(
            onDownloadPressed = onDownloadClick,
            onSetWallpaperPressed = onSetWallpaperClick
        )
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
            .clickable { onAttributionClick() }
            .padding(end = 8.dp)
            .weight(1f)
    ) {
        val photographerImageUrl = photo.getPhotoPhotographerImageUrl()

        val modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)

        if (photographerImageUrl != null) {
            NetworkImage(
                imageUrl = photographerImageUrl,
                revealDuration = imageRevealDuration,
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

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(photo.getPhotoPhotographerName(),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(photo.getPhotoSource().url,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PrimaryActions(
    onDownloadPressed: () -> Unit,
    onSetWallpaperPressed: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onDownloadPressed
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_file_download_24dp),
                contentDescription = stringResource(id = R.string.content_description_download),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(id = R.string.download))
        }
        Spacer(modifier = Modifier.width(24.dp))
        Button(
            onClick = onSetWallpaperPressed
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wallpaper),
                contentDescription = stringResource(id = R.string.content_description_set_wallpaper),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(id = R.string.wallpaper))
        }
    }
}

@Composable
private fun SecondaryActions(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePressed: () -> Unit
) {
    Row {
        ActionIconButton(
            icon = R.drawable.ic_share_24dp,
            contentDescription = R.string.content_description_share,
            onClick = onSharePressed
        )

        Spacer(modifier = Modifier.width(8.dp))

        FavoriteButton(
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite
        )
    }
}

// TODO: Use FavoriteButton from compose-ui module.
@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val scale = remember { Animatable(1.0f) }
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(favoriteAnimationDuration, easing = LinearEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(favoriteAnimationDuration, easing = LinearEasing)
            )
        } else {
            // Source: https://ophilia.in/creating-a-wiggle-animation-in-jetpack-compose
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    for (i in 1..8) {
                        val x = when (i % 3) {
                            0 -> 2f
                            1 -> -2f
                            else -> 0f
                        }
                        x at unFavoriteAnimationDuration / 10 * i with LinearEasing
                    }
                }
            )
        }
    }

    val icon =
        if (isFavorite) R.drawable.ic_favorite_red_24dp
        else R.drawable.ic_favorite_border_white_24dp

    val tintColor =
        if (isFavorite) colorResource(id = R.color.colorRed)
        else LocalContentColor.current

    val tint: Color by animateColorAsState(
        targetValue = tintColor,
        animationSpec = tween(favoriteAnimationDuration),
        label = "Favorite button color animation",
    )

    ActionIconButton(
        icon = icon,
        contentDescription = R.string.content_description_toggle_favorite,
        iconTint = tint,
        onClick = onToggleFavorite,
        modifier = Modifier
            .scale(scale.value)
            .offset(x = offsetX.value.dp, y = 0.dp)
    )
}
