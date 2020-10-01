package com.github.sikv.photos.viewmodel

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sikv.photos.account.AccountManager
import com.github.sikv.photos.account.AccountManagerListener
import com.github.sikv.photos.enumeration.LoginStatus
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoreViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MoreViewModel

    private val fakeAccountManager = object : AccountManager {
        private var listener: AccountManagerListener? = null

        override fun subscribe(listener: AccountManagerListener) {
            this.listener = listener
        }

        override fun unsubscribe(listener: AccountManagerListener) {
            this.listener = null
        }

        override fun getLoginStatus(): LoginStatus {
            return LoginStatus.NotSet
        }

        override fun signInAnonymously(doAfter: () -> Unit) { }

        override fun signInWithGoogle(fragment: Fragment) {
            listener?.onLoginStatusChanged(LoginStatus.SigningIn)
        }

        override fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) { }

        override fun signOut() {
            listener?.onLoginStatusChanged(LoginStatus.SignedOut)
        }
    }

    @Before
    fun setup() {
        viewModel = MoreViewModel(fakeAccountManager)
    }

    @Test
    fun signInWithGoogle_triggersLoginStatusChangedEvent() {
        viewModel.signInWithGoogle(Fragment())

        viewModel.loginStatusChangedEvent.observeForever { }
        val value = viewModel.loginStatusChangedEvent.value?.getContentIfNotHandled()

        Assert.assertEquals(LoginStatus.SigningIn, value)
    }

    @Test
    fun signOut_triggersLoginStatusChangedEvent() {
        viewModel.signOut()

        viewModel.loginStatusChangedEvent.observeForever { }
        val value = viewModel.loginStatusChangedEvent.value?.getContentIfNotHandled()

        Assert.assertEquals(LoginStatus.SignedOut, value)
    }
}