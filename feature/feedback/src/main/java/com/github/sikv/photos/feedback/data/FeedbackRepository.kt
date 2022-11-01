package com.github.sikv.photos.feedback.data

import com.github.sikv.photos.feedback.domain.Feedback

internal interface FeedbackRepository {
    suspend fun sendFeedback(feedback: Feedback): Boolean
}
