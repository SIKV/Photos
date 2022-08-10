package com.github.sikv.photos.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NothingHereScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "¯\\_(ツ)_/¯",
                style = MaterialTheme.typography.h5
            )
        }
    }
}
