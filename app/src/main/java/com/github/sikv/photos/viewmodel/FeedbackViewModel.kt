package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.R
import com.github.sikv.photos.data.repository.FeedbackRepository
import com.github.sikv.photos.model.RequestStatus
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Feedback
import com.github.sikv.photos.service.PreferencesService
import com.github.sikv.photos.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    application: Application,
    private val feedbackRepository: FeedbackRepository,
    private val preferencesService: PreferencesService
) : AndroidViewModel(application) {

    private val sendFeedbackStatusMutableEvent = MutableLiveData<Event<RequestStatus>>()
    val sendFeedbackStatusEvent: LiveData<Event<RequestStatus>> = sendFeedbackStatusMutableEvent

    fun send(email: String?, description: String?) {
        if (!email.isValidEmail()) {
            val error = getApplication<Application>().getString(R.string.enter_valid_email)
            sendFeedbackStatusMutableEvent.value = Event(RequestStatus.ValidationError(error, 1))
            return
        }

        if (description.isNullOrBlank()) {
            val error = getApplication<Application>().getString(R.string.enter_some_text)
            sendFeedbackStatusMutableEvent.value = Event(RequestStatus.ValidationError(error, 2))
            return
        }

        val feedback = Feedback(
            preferencesService.getSessionId(),
            email,
            description
        )

        sendFeedbackStatusMutableEvent.value = Event(RequestStatus.InProgress)

        viewModelScope.launch {
            val sent = feedbackRepository.sendFeedback(feedback)

            val messageId = if (sent) R.string.feedback_sent else R.string.error_sending_feedback
            val message = getApplication<Application>().getString(messageId)

            sendFeedbackStatusMutableEvent.postValue(
                Event(
                    RequestStatus.Done(
                        success = sent,
                        message = message
                    )
                )
            )
        }
    }
}
