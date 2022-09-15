package com.github.sikv.photos.feedback

interface FeedbackRepository {
    suspend fun sendFeedback(feedback: Feedback): Boolean
}
