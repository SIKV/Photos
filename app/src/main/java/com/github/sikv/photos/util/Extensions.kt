package com.github.sikv.photos.util

import android.app.Application
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.subscribeAsync(onSubscribe: (T) -> Unit, onError: (Throwable) -> Unit): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onSubscribe(it)
            }, {
                onError(it)
            })
}

fun AndroidViewModel.getString(@StringRes id: Int): String {
    return getApplication<Application>().resources.getString(id)
}

fun String?.isValidEmail(): Boolean {
    val email = this
    return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}