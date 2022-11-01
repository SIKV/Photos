package com.github.sikv.photos.feedback.domain

enum class RequestStatus {
    Idle,
    InProgress,
    Success,
    Error,
    InvalidEmail,
    InvalidDescription
}
