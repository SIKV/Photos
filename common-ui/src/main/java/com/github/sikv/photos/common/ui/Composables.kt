@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.sikv.photos.common.ui

import android.graphics.drawable.BitmapDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage

// TODO: Move to compose-ui module.

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
                color = MaterialTheme.colorScheme.surface,
                onClick = onBackPressed
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ActionIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes contentDescription: Int,
    iconTint: Color? = null,
    color: Color? = null,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .background(color = color ?: Color.Transparent, shape = CircleShape),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = contentDescription),
            tint = iconTint ?: LocalContentColor.current
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
            revealDuration = 1000,
            modifier = modifier
        )
    }
}

@Composable
fun NetworkImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    loading: @Composable (BoxScope.() -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    revealDuration: Int? = null
) {
    Image(
        modifier = modifier,
        imageModel = imageUrl,
        contentScale = contentScale,
        revealDuration = revealDuration,
        loading = loading,
    )
}

@Composable
private fun Image(
    imageModel: Any,
    contentScale: ContentScale,
    revealDuration: Int?,
    modifier: Modifier = Modifier,
    loading: @Composable (BoxScope.() -> Unit)? = null
) {
    GlideImage(
        modifier = modifier,
        imageModel = { imageModel },
        imageOptions = ImageOptions(
            contentScale = contentScale,
        ),
        component = rememberImageComponent {
            if (revealDuration != null) {
                add(CircularRevealPlugin(duration = revealDuration))
            }
        },
        loading = {
            loading?.invoke(this)
        },
    )
}
