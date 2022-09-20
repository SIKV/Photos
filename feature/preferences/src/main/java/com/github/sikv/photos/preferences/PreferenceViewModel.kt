package com.github.sikv.photos.preferences

import androidx.lifecycle.ViewModel
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
internal class PreferenceViewModel @Inject constructor() : ViewModel() {

    private val mutableUiState = MutableStateFlow<MoreUiState>(MoreUiState.Empty)
    val uiState: StateFlow<MoreUiState> = mutableUiState

    init {
        mutableUiState.value = MoreUiState.Data(
            appVersion = "" // TODO Fix
        )
    }
}
