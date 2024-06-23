package com.github.sikv.photos.compose.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.sikv.photos.domain.ListLayout

@Composable
fun SwitchLayoutAction(
    listLayout: ListLayout,
    onSwitchLayoutClick: () -> Unit
) {
    val icon = when (listLayout) {
        ListLayout.LIST -> R.drawable.ic_view_grid_24dp
        ListLayout.GRID -> R.drawable.ic_view_list_24dp
    }

    IconButton(
        onClick = onSwitchLayoutClick
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.switch_layout)
        )
    }
}
