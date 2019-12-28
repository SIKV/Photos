package com.github.sikv.photos.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor() {

    interface Callback {
        fun onLoginStatusChanged(status: LoginStatus)
    }

    private val subscribers = mutableListOf<Callback>()

    var loginStatus = LoginStatus.LOGGED_OUT
        private set

    fun subscribe(callback: Callback) {
        subscribers.add(callback)
    }

    fun unsubscribe(callback: Callback) {
        subscribers.remove(callback)
    }

    fun login() {
        loginStatus = LoginStatus.LOGGED_IN

        subscribers.forEach {
            it.onLoginStatusChanged(loginStatus)
        }
    }

    fun logout() {
        loginStatus = LoginStatus.LOGGED_OUT

        subscribers.forEach {
            it.onLoginStatusChanged(loginStatus)
        }
    }
}