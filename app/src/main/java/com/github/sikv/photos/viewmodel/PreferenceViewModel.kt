package com.github.sikv.photos.viewmodel

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.BuildConfig
import com.github.sikv.photos.enumeration.LoginStatus
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.util.AccountManager
import javax.inject.Inject

class PreferenceViewModel: ViewModel(), AccountManager.Callback {

    @Inject
    lateinit var accountManager: AccountManager

    private val loginStatusChangedMutableLiveData = MutableLiveData<LoginStatus>()
    val loginStatusChangedLiveData: LiveData<LoginStatus> = loginStatusChangedMutableLiveData

    private val showAppVersionMutableEvent = MutableLiveData<Event<String>>()
    val showAppVersionEvent: LiveData<Event<String>> = showAppVersionMutableEvent

    init {
        App.instance.appComponent.inject(this)

        accountManager.subscribe(this)

        loginStatusChangedMutableLiveData.postValue(accountManager.loginStatus)

        val appVersion = BuildConfig.VERSION_NAME
        showAppVersionMutableEvent.postValue(Event(appVersion))
    }

    override fun onCleared() {
        super.onCleared()

        accountManager.unsubscribe(this)
    }

    override fun onLoginStatusChanged(status: LoginStatus) {
        loginStatusChangedMutableLiveData.postValue(status)
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