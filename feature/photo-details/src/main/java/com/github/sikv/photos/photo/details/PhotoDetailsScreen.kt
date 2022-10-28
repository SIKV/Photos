package com.github.sikv.photos.photo.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.sikv.photos.common.ui.*
import com.github.sikv.photos.domain.Photo

private val sheetPeekHeight = 196.dp
private const val imageRevealDuration = 1000

private const val favoriteAnimationDuration = 100
private const val unFavoriteAnimationDuration = 400

@ExperimentalMaterialApi
@Composable
internal fun PhotoDetailsScreen(
    photo: Photo,
    onBackPressed: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePressed: () -> Unit,
    onDownloadPressed: () -> Unit,
    onSetWallpaperPressed: () -> Unit,
    onAttributionPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val sheetRadius = (24 - (24 * scaffoldState.bottomSheetState.currentFraction)).dp

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SheetContent(
                photo = photo,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                onSharePressed = onSharePressed,
                onDownloadPressed = onDownloadPressed,
                onSetWallpaperPressed = onSetWallpaperPressed,
                onAttributionPressed = onAttributionPressed
            )
        },
        sheetShape = RoundedCornerShape(topStart = sheetRadius, topEnd = sheetRadius),
        sheetPeekHeight = sheetPeekHeight,
        modifier = Modifier
            .navigationBarsPadding()
    ) {
        Box(
            modifier = modifier
                .padding(bottom = sheetPeekHeight - sheetRadius)
        ) {
            NetworkImage(
                imageUrl = photo.getPhotoFullPreviewUrl(),
                revealDuration = imageRevealDuration,
                modifier = Modifier
                    .fillMaxSize()
            )
            TransparentTopAppBar(
                onBackPressed = onBackPressed,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun SheetContent(
    photo: Photo,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePressed: () -> Unit,
    onDownloadPressed: () -> Unit,
    onSetWallpaperPressed: () -> Unit,
    onAttributionPressed: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight)
            .padding(horizontal = 16.dp)
    ) {
        DragIndicator(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
        )

        ActionableContent(
            photo = photo,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            onSharePressed = onSharePressed,
            onDownloadPressed = onDownloadPressed,
            onSetWallpaperPressed = onSetWallpaperPressed,
            onAttributionPressed = onAttributionPressed
        )
    }
}

@Composable
private fun ActionableContent(
    photo: Photo,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePressed: () -> Unit,
    onDownloadPressed: () -> Unit,
    onSetWallpaperPressed: () -> Unit,
    onAttributionPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Attribution(
                photo = photo,
                onAttributionPressed = onAttributionPressed
            )
            SecondaryActions(
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                onSharePressed = onSharePressed
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryActions(
            onDownloadPressed = onDownloadPressed,
            onSetWallpaperPressed = onSetWallpaperPressed
        )
    }
}

@Composable
private fun Attribution(
    photo: Photo,
    onAttributionPressed: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onAttributionPressed() }
            .padding(end = 8.dp)
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
                style = MaterialTheme.typography.h6
            )
            Text(photo.getPhotoSource().url,
                style = MaterialTheme.typography.caption
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
        modifier = Modifier.fillMaxWidth()
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
        else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)

    val tint: Color by animateColorAsState(
        targetValue = tintColor,
        animationSpec = tween(favoriteAnimationDuration)
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
