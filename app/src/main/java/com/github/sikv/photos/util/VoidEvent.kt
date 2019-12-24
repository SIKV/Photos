package com.github.sikv.photos.util

/**
 * Used as a wrapper to send an event without data.
 */
class VoidEvent {

    var hasBeenHandled = false
        private set

    fun canHandle(): Boolean {
        return if (hasBeenHandled) {
            false
        } else {
            hasBeenHandled = true
            true
        }
    }
}