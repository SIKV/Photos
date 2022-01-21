package com.github.sikv.photos.viewmodel

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.manager.AccountManager
import com.github.sikv.photos.model.LoginStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val accountManager: AccountManager
) : ViewModel(), AccountManager.Listener {

    private val mutableLoginStatusChanged = MutableLiveData<LoginStatus>()
    val loginStatusChanged: LiveData<LoginStatus> = mutableLoginStatusChanged

    init {
        accountManager.subscribe(this)

        mutableLoginStatusChanged.value = accountManager.getLoginStatus()
    }

    override fun onCleared() {
        super.onCleared()

        accountManager.unsubscribe(this)
    }

    override fun onLoginStatusChanged(status: LoginStatus) {
        mutableLoginStatusChanged.value = status
    }

    fun signInWithGoogle(fragment: Fragment) {
        accountManager.signInWithGoogle(fragment)
    }

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        accountManager.handleSignInResult(requestCode, resultCode, data)
    }

    fun signOut() {
        accountManager.signOut()
    }
}
