package com.github.sikv.photos.account

import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.sikv.photos.enumeration.LoginStatus

interface AccountManager {
    fun subscribe(listener: AccountManagerListener)
    fun unsubscribe(listener: AccountManagerListener)

    fun getLoginStatus(): LoginStatus

    fun signInAnonymously(doAfter: () -> Unit)
    fun signInWithGoogle(fragment: Fragment)

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun signOut()
}