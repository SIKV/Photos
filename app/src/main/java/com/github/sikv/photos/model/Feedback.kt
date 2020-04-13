package com.github.sikv.photos.model

import com.github.sikv.photos.enumeration.FeedbackMode

data class Feedback(
        val sessionId: String,
        val mode: FeedbackMode,
        val email: String?,
        val description: String
)