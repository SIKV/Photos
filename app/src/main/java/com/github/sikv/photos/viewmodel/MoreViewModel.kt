package com.github.sikv.photos.viewmodel

import androidx.lifecycle.ViewModel
import com.github.sikv.photos.BuildConfig
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
class MoreViewModel @Inject constructor() : ViewModel() {

    private val mutableUiState = MutableStateFlow<MoreUiState>(MoreUiState.Empty)
    val uiState: StateFlow<MoreUiState> = mutableUiState

    init {
        mutableUiState.value = MoreUiState.Data(
            appVersion = BuildConfig.VERSION_NAME
        )
    }
}
