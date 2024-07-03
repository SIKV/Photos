package com.github.sikv.photos.compose.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun BackAction(
    onBackClick: () -> Unit
) {
    IconButton(
        onClick = onBackClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back_24dp),
            contentDescription = stringResource(id = R.string.navigate_back)
        )
    }
}
