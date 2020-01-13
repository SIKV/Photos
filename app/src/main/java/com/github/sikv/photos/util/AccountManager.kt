package com.github.sikv.photos.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(context: Context) {

    companion object {
        const val RC_GOOGLE_SIGN_IN = 500
    }

    interface Callback {
        fun onLoginStatusChanged(status: LoginStatus)
    }

    private val subscribers = mutableListOf<Callback>()

    var loginStatus = LoginStatus.NOT_SET
        private set(value) {
            field = value

            subscribers.forEach {
                it.onLoginStatusChanged(value)
            }
        }

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val googleSignInClient = GoogleSignIn.getClient(context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
    )

    init {
        loginStatus = if (auth.currentUser != null) {
            LoginStatus.SIGNED_IN
        } else {
            LoginStatus.SIGNED_OUT
        }
    }

    fun subscribe(callback: Callback) {
        subscribers.add(callback)
    }

    fun unsubscribe(callback: Callback) {
        subscribers.remove(callback)
    }

    fun signInWithGoogle(fragment: Fragment) {
        fragment.startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)

        loginStatus = LoginStatus.SIGNING_IN
    }

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)

            } catch (e: ApiException) {
                loginStatus = LoginStatus.SIGNED_OUT

                App.instance.postMessage("Error signing in... " + e.localizedMessage)
            }
        }
    }

    fun signOut() {
        auth.signOut()

        loginStatus = LoginStatus.SIGNED_OUT
    }

    private fun firebaseAuthWithGoogle(googleAccount: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(googleAccount?.idToken, null)

        auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginStatus = LoginStatus.SIGNED_IN

                    } else {
                        loginStatus = LoginStatus.SIGNED_OUT

                        App.instance.postMessage("Error signing in...")
                    }
                }
    }
}