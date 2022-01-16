package com.github.sikv.photos.model

sealed class RequestStatus {
    object InProgress : RequestStatus()
    class Done(val success: Boolean, val message: String = "") : RequestStatus()
    class ValidationError(val message: String = "", val invalidInputIndex: Int = -1) : RequestStatus()
}
