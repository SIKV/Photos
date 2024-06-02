package com.github.sikv.photos.compose.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.github.sikv.photos.common.ui.ActionIconButton

private const val favoriteAnimationDuration = 100
private const val unFavoriteAnimationDuration = 400

@Composable
fun FavoriteButton(
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
        contentDescription = R.string.toggle_favorite,
        iconTint = tint,
        onClick = onToggleFavorite,
        modifier = Modifier
            .scale(scale.value)
            .offset(x = offsetX.value.dp, y = 0.dp)
    )
}
