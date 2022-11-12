package com.github.sikv.photos.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource

@Composable
internal fun PreferenceScreen(
    preferences: List<PreferenceItem>,
    onPreferencePress: (PreferenceAction) -> Unit
) {
    Column {
        preferences.forEach { item ->
            when (item) {
                is PreferenceItem.Item -> PreferenceItem(
                    item = item,
                    onPress = { onPreferencePress(item.action) }
                )
                is PreferenceItem.Divider -> Divider(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.paddingOne))
                )
            }
        }
    }
}

@Composable
private fun PreferenceItem(
    item: PreferenceItem.Item,
    onPress: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPress() }
            .padding(dimensionResource(id = R.dimen.paddingTwo))
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.title
        )
        Spacer(
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.paddingThree))
        )
        Column {
            Text(item.title)
            if (item.summary != null) {
                Text(item.summary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
