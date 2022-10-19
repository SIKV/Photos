package com.github.sikv.photos.preferences

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface MoreUiState {
    object Empty : MoreUiState

    data class Data(
        val appVersion: String
    ) : MoreUiState
}

@HiltViewModel
internal class PreferenceViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private val mutableUiState = MutableStateFlow<MoreUiState>(MoreUiState.Empty)
    val uiState: StateFlow<MoreUiState> = mutableUiState

    init {
        mutableUiState.value = MoreUiState.Data(
            appVersion = getAppVersion() ?: ""
        )
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
}
