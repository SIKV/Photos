package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.enumeration.FeedbackMode

class FeedbackViewModelFactory(
        private val application: Application,
        private val mode: FeedbackMode
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(modelClass)) {
            return FeedbackViewModel(application, mode) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}