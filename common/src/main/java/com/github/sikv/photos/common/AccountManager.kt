package com.github.sikv.photos.common

import com.github.sikv.photos.domain.LoginStatus

interface AccountManager {

    interface Listener {
        fun onLoginStatusChanged(status: LoginStatus)
    }

    fun subscribe(listener: Listener)
    fun unsubscribe(listener: Listener)

    fun getLoginStatus(): LoginStatus

    fun signInAnonymously(doAfter: () -> Unit)
    fun signOut()
}
