package com.github.sikv.photos.data.repository

import com.github.sikv.photos.model.Feedback

interface FeedbackRepository {
    suspend fun sendFeedback(feedback: Feedback): Boolean
}