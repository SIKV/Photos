package com.github.sikv.photos.preferences

import androidx.annotation.DrawableRes

internal enum class PreferenceAction {
    ChangeTheme, SendFeedback, OpenSourceLicenses, AppVersion
}

internal sealed interface PreferenceItem {

    data class Item(
        val action: PreferenceAction,
        @DrawableRes val icon: Int,
        val title: String,
        val summary: String? = null
    ) : PreferenceItem

    object Divider : PreferenceItem
}
