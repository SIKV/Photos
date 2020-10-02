package com.github.sikv.photos.account

import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.sikv.photos.enumeration.LoginStatus

interface AccountManager {

    interface Listener {
        fun onLoginStatusChanged(status: LoginStatus)
    }

    fun subscribe(listener: Listener)
    fun unsubscribe(listener: Listener)

    fun getLoginStatus(): LoginStatus

    fun signInAnonymously(doAfter: () -> Unit)
    fun signInWithGoogle(fragment: Fragment)

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun signOut()
}