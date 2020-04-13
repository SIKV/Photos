package com.github.sikv.photos.enumeration

sealed class RequestStatus {
    class InProgress(val message: String = ""): RequestStatus()
    class Done(val success: Boolean, val message: String = "") : RequestStatus()
    class ValidationError(val message: String = "") : RequestStatus()
}