package com.github.sikv.photos.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.getAttributionPlaceholderBackgroundColor
import com.github.sikv.photos.model.getAttributionPlaceholderTextColor
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalMaterialApi
@Composable
fun PhotoDetailsScreen(
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
    val sheetRadius = 24.dp
    val sheetPeekHeight = 196.dp

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
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
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                modifier = modifier
            )
        } else {
            PlaceholderImage(
                text = photo.getPhotoPhotographerName().first().uppercaseChar().toString(),
                textColor = photo.getAttributionPlaceholderTextColor(LocalContext.current),
                backgroundColor = photo.getAttributionPlaceholderBackgroundColor(LocalContext.current),
                modifier = modifier
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(photo.getPhotoPhotographerName(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(photo.getPhotoSource().url,
                fontSize = 12.sp
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
                contentDescription = stringResource(id = R.string.cd_download),
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
                contentDescription = stringResource(id = R.string.cd_set_wallpaper),
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
            contentDescription = R.string.cd_share,
            onClick = onSharePressed
        )

        Spacer(modifier = Modifier.width(8.dp))

        val favoriteIcon =
            if (isFavorite) R.drawable.ic_favorite_red_24dp
            else R.drawable.ic_favorite_border_white_24dp

        ActionIconButton(
            icon = favoriteIcon,
            contentDescription = R.string.cd_toggle_favorite,
            iconTint = if (isFavorite) colorResource(id = R.color.colorRed) else null,
            onClick = onToggleFavorite
        )
    }
}
