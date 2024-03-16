package com.github.sikv.photos.preferences

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import com.github.sikv.photos.common.ui.OptionsBottomSheetDialog
import com.github.sikv.photos.thememanager.AppTheme
import com.github.sikv.photos.thememanager.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal data class PreferenceUiState(
    val preferences: List<PreferenceItem> = emptyList()
)

@HiltViewModel
internal class PreferenceViewModel @Inject constructor(
    application: Application,
    private val themeManager: ThemeManager
) : AndroidViewModel(application) {

    private val mutableUiState = MutableStateFlow(PreferenceUiState())
    val uiState: StateFlow<PreferenceUiState> = mutableUiState

    init {
        mutableUiState.value = PreferenceUiState(
            preferences = listOf(
                createThemePreferenceItem(themeManager.getCurrentTheme()),
                PreferenceItem.Divider,
                // TODO: Disabled for Dev release.
//                PreferenceItem.Item(
//                    action = PreferenceAction.SendFeedback,
//                    icon = R.drawable.ic_bubble_24dp,
//                    title = getString(R.string.send_feedback)
//                ),
                PreferenceItem.Item(
                    action = PreferenceAction.OpenSourceLicenses,
                    icon = R.drawable.ic_code_24dp,
                    title = getString(R.string.open_source_licences)
                ),
                PreferenceItem.Item(
                    action = PreferenceAction.AppVersion,
                    icon = R.drawable.ic_info_24dp,
                    title = getString(R.string.app_version),
                    summary = getAppVersion()
                ),
            )
        )
    }

    fun createChangeThemeDialog(): OptionsBottomSheetDialog {
        val themes = themeManager.getThemes()

        val options = themes.map { getThemeString(it) }.toList()
        val selectedOptionIndex = themes.indexOf(themeManager.getCurrentTheme())

        return OptionsBottomSheetDialog.newInstance(options, selectedOptionIndex) { index ->
            val selectedTheme = themes[index]
            themeManager.applyTheme(selectedTheme)

            updateUiState(selectedTheme)
        }
    }

    private fun updateUiState(theme: AppTheme) {
        mutableUiState.update { state ->
            val preferences = state.preferences

            val indexOfThemeItem = preferences.indexOfFirst {
                it is PreferenceItem.Item && it.action == PreferenceAction.ChangeTheme
            }

            val updatedPreferences = preferences.toMutableList()
            updatedPreferences[indexOfThemeItem] = createThemePreferenceItem(theme)

            state.copy(preferences = updatedPreferences)
        }
    }

    private fun createThemePreferenceItem(theme: AppTheme): PreferenceItem {
        return PreferenceItem.Item(
            action = PreferenceAction.ChangeTheme,
            icon = R.drawable.ic_brush_24dp,
            title = getString(R.string.theme),
            summary = getThemeString(theme)
        )
    }

    private fun getThemeString(appTheme: AppTheme): String {
        return when (appTheme) {
            AppTheme.Light -> getString(R.string.light)
            AppTheme.Dark -> getString(R.string.dark)
            AppTheme.FollowSystem -> getString(R.string.system_default)
        }
    }

    private fun getAppVersion(): String? {
        return try {
            val context = getApplication<Application>()
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("Deprecation")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName
        } catch (e: Exception) {
            null
        }
    }

    private fun getString(@StringRes resId: Int): String = getApplication<Application>().getString(resId)
}
