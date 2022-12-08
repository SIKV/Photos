package com.github.sikv.photos.account

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AccountManagerImpl @Inject constructor() : AccountManager {

    private val subscribers = mutableListOf<AccountManager.Listener>()

    private var loginStatus: LoginStatus = LoginStatus.NotSet
        private set(value) {
            field = value

            subscribers.forEach {
                it.onLoginStatusChanged(value)
            }
        }

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        loginStatus = if (isSignedIn()) {
            LoginStatus.SignedIn(getSignedInEmail())
        } else {
            LoginStatus.SignedOut
        }
    }

    override fun subscribe(listener: AccountManager.Listener) {
        subscribers.add(listener)
    }

    override fun unsubscribe(listener: AccountManager.Listener) {
        subscribers.remove(listener)
    }

    override fun getLoginStatus(): LoginStatus {
        return loginStatus
    }

    private fun isSignedIn(): Boolean {
        return auth.currentUser != null && auth.currentUser?.isAnonymous == false
    }

    private fun isSignedInAnonymously(): Boolean {
        return auth.currentUser != null && auth.currentUser?.isAnonymous == true
    }

    override fun signInAnonymously(doAfter: () -> Unit) {
        if (isSignedIn() || isSignedInAnonymously()) {
            doAfter()
        } else {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        doAfter()
                    }
                }
        }
    }

    override fun signOut() {
        auth.signOut()

        loginStatus = LoginStatus.SignedOut
    }

    private fun getSignedInEmail(): String {
        return auth.currentUser?.email ?: ""
    }
}
