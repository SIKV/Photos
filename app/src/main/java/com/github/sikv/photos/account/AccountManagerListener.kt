package com.github.sikv.photos.account

import com.github.sikv.photos.enumeration.LoginStatus

interface AccountManagerListener {
    fun onLoginStatusChanged(status: LoginStatus)
}