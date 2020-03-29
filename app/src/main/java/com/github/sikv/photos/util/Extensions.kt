package com.github.sikv.photos.util

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