package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.data.repository.FeedbackRepository
import com.github.sikv.photos.enumeration.FeedbackMode
import com.github.sikv.photos.enumeration.RequestStatus
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Feedback
import com.github.sikv.photos.util.Utils
import javax.inject.Inject

class FeedbackViewModel(
        application: Application,
        private val mode: FeedbackMode
) : AndroidViewModel(application) {

    @Inject
    lateinit var feedbackRepository: FeedbackRepository

    private val showTitleMutableEvent = MutableLiveData<Event<String>>()
    val showTitleEvent: LiveData<Event<String>> = showTitleMutableEvent

    private val showDescriptionHintMutableEvent = MutableLiveData<Event<String>>()
    val showDescriptionHintEvent: LiveData<Event<String>> = showDescriptionHintMutableEvent

    private val sendFeedbackStatusMutableEvent = MutableLiveData<Event<RequestStatus>>()
    val sendFeedbackStatusEvent: LiveData<Event<RequestStatus>> = sendFeedbackStatusMutableEvent

    init {
        App.instance.appComponent.inject(this)

        when (mode) {
            FeedbackMode.SEND_FEEDBACK -> {
                showTitleMutableEvent.value = Event(application.getString(R.string.send_feedback))
                showDescriptionHintMutableEvent.value = Event(application.getString(R.string.what_to_improve))
            }

            FeedbackMode.REPORT_PROBLEM -> {
                showTitleMutableEvent.value = Event(application.getString(R.string.report_problem))
                showDescriptionHintMutableEvent.value = Event(application.getString(R.string.what_went_wrong))
            }
        }
    }

    fun send(email: String?, description: String?) {
        if (description.isNullOrBlank()) {
            val error = getApplication<Application>().getString(R.string.enter_some_text)
            sendFeedbackStatusMutableEvent.value = Event(RequestStatus.ValidationError(error))
            return
        }

        val feedback = Feedback(
                Utils.getSessionId(),
                mode,
                email,
                description
        )

        sendFeedbackStatusMutableEvent.value = Event(RequestStatus.InProgress())

        feedbackRepository.sendFeedback(feedback) { sent ->
            val messageId = when (mode) {
                FeedbackMode.SEND_FEEDBACK -> {
                    if (sent) R.string.feedback_sent else R.string.error_sending_feedback
                }

                FeedbackMode.REPORT_PROBLEM -> {
                    if (sent) R.string.report_sent else R.string.error_sending_report
                }
            }

            val message = getApplication<Application>().getString(messageId)
            sendFeedbackStatusMutableEvent.value = Event(RequestStatus.Done(success = sent, message = message))
        }
    }
}