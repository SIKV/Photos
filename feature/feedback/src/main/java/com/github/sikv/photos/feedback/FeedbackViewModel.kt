package com.github.sikv.photos.feedback

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.common.PreferencesService
import com.github.sikv.photos.feedback.data.FeedbackRepository
import com.github.sikv.photos.feedback.domain.Feedback
import com.github.sikv.photos.feedback.domain.RequestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedbackUiState(
    val email: String?,
    val description: String?,
    val requestStatus: RequestStatus
)

@HiltViewModel
internal class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val preferencesService: PreferencesService
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(
        FeedbackUiState(
            email = null,
            description = null,
            requestStatus = RequestStatus.Idle
        )
    )
    val uiState: StateFlow<FeedbackUiState> = mutableUiState

    fun emailChanged(email: String) {
        mutableUiState.update { state ->
            state.copy(email = email)
        }
    }

    fun descriptionChanged(feedback: String) {
        mutableUiState.update { state ->
            state.copy(description = feedback)
        }
    }

    fun submit() {
        val email = uiState.value.email
        val description = uiState.value.description

        if (!email.isValidEmail()) {
            mutableUiState.update { state ->
                state.copy(requestStatus = RequestStatus.InvalidEmail)
            }
            return
        }
        if (description.isNullOrBlank()) {
            mutableUiState.update { state ->
                state.copy(requestStatus = RequestStatus.InvalidDescription)
            }
            return
        }

        val feedback = Feedback(
            preferencesService.getSessionId(),
            email,
            description
        )

        mutableUiState.update { state ->
            state.copy(requestStatus = RequestStatus.InProgress)
        }

        viewModelScope.launch {
            val sent = feedbackRepository.sendFeedback(feedback)
            mutableUiState.update { state ->
                state.copy(requestStatus = if (sent) RequestStatus.Success else RequestStatus.Error)
            }
        }
    }
}

fun String?.isValidEmail(): Boolean {
    val email = this
    return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
