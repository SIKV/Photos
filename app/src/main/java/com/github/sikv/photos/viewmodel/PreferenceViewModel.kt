package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.util.AccountManager
import com.github.sikv.photos.util.LoginStatus
import javax.inject.Inject

class PreferenceViewModel: ViewModel(), AccountManager.Callback {

    @Inject
    lateinit var accountManager: AccountManager

    private val loginStatusChangedMutableLiveData = MutableLiveData<LoginStatus>()
    val loginStatusChangedLiveData: LiveData<LoginStatus> = loginStatusChangedMutableLiveData

    init {
        App.instance.appComponent.inject(this)

        accountManager.subscribe(this)

        loginStatusChangedMutableLiveData.postValue(accountManager.loginStatus)
    }

    override fun onCleared() {
        super.onCleared()

        accountManager.unsubscribe(this)
    }

    override fun onLoginStatusChanged(status: LoginStatus) {
        loginStatusChangedMutableLiveData.postValue(status)
    }

    fun login() {
        accountManager.login()
    }

    fun logout() {
        accountManager.logout()
    }
}