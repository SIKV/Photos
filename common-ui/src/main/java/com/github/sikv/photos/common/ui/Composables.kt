package com.github.sikv.photos.common.ui

import android.graphics.drawable.BitmapDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun TransparentTopAppBar(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { },
        modifier = modifier,
        navigationIcon = {
            ActionIconButton(
                icon = R.drawable.ic_arrow_back_24dp,
                contentDescription = R.string.content_description_back,
                color = MaterialTheme.colors.surface,
                onClick = onBackPressed
            )
        },
        elevation = 0.dp,
        backgroundColor = Color.Transparent
    )
}

@Composable
fun DragIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(32.dp)
            .height(4.5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.5F))
    )
}

@Composable
fun ActionIconButton(
    @DrawableRes icon: Int,
    @StringRes contentDescription: Int,
    iconTint: Color? = null,
    color: Color? = null,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .background(color = color ?: Color.Transparent, shape = CircleShape),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = contentDescription),
            tint = iconTint ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        )
    }
}

@Composable
fun PlaceholderImage(
    text: String,
    @ColorInt textColor: Int,
    @ColorInt backgroundColor: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val placeholder = remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(text) {
        placeholder.value = TextPlaceholder.with(context)
            .text(text)
            .textColor(textColor)
            .background(TextPlaceholder.Shape.CIRCLE, backgroundColor)
            .build()
    }

    placeholder.value?.let { bitmap ->
        Image(
            imageModel = bitmap,
            contentScale =  ContentScale.Crop,
            circularReveal = CircularReveal(duration = 1000),
            modifier = modifier
        )
    }
}

@Composable
fun NetworkImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    circularReveal: CircularReveal? = CircularReveal(duration = 1000)
) {
    Image(
        modifier = modifier,
        imageModel = imageUrl,
        contentScale = contentScale,
        circularReveal = circularReveal
    )
}

@Composable
private fun Image(
    imageModel: Any,
    contentScale: ContentScale,
    circularReveal: CircularReveal?,
    modifier: Modifier = Modifier
) {
    GlideImage(
        modifier = modifier,
        imageModel = imageModel,
        contentScale = contentScale,
        circularReveal = circularReveal
    )
}
