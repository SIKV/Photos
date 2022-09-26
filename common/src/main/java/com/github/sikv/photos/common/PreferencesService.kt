package com.github.sikv.photos.common

import android.content.Context
import com.github.sikv.photos.domain.ListLayout
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesService @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val featureFlagsFetched = "featureFlagsFetched"
    private val curatedSpanCount = "curatedSpanCount"
    private val favoritesSpanCount = "favoritesSpanCount"
    private val sessionId = "sessionId"

    private val preferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

    fun isFeatureFlagsFetched(): Boolean = getBoolean(featureFlagsFetched, false)
    fun setFeatureFlagsFetched() = putBoolean(featureFlagsFetched, true)

    fun getCuratedListLayout(): ListLayout = ListLayout
        .findBySpanCount(getInt(curatedSpanCount, ListLayout.LIST.spanCount))

    fun setCuratedListLayout(value: ListLayout) = putInt(curatedSpanCount, value.spanCount)

    fun getFavoritesListLayout(): ListLayout = ListLayout
        .findBySpanCount(getInt(favoritesSpanCount, ListLayout.GRID.spanCount))

    fun setFavoritesListLayout(value: ListLayout) = putInt(favoritesSpanCount, value.spanCount)

    fun getSessionId(): String {
        preferences.getString(sessionId, null)?.let { sessionId ->
            return sessionId
        } ?: run {
            val sessionId = UUID.randomUUID().toString()

            preferences
                .edit()
                .putString(sessionId, sessionId)
                .apply()

            return sessionId
        }
    }

    private fun getInt(key: String, default: Int): Int = preferences
        .getInt(key, default)

    private fun putInt(key: String, value: Int) = preferences
        .edit()
        .putInt(key, value)
        .apply()

    private fun getBoolean(key: String, default: Boolean): Boolean = preferences
        .getBoolean(key, default)

    private fun putBoolean(key: String, value: Boolean) = preferences
        .edit()
        .putBoolean(key, value)
        .apply()
}
