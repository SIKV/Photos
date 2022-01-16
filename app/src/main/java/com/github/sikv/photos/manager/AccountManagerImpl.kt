package com.github.sikv.photos.manager

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.model.LoginStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManagerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AccountManager {

    companion object {
        const val RC_GOOGLE_SIGN_IN = 500
    }

    private val subscribers = mutableListOf<AccountManager.Listener>()

    private var loginStatus: LoginStatus = LoginStatus.NotSet
        private set(value) {
            field = value

            subscribers.forEach {
                it.onLoginStatusChanged(value)
            }
        }

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )

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

    override fun signInWithGoogle(fragment: Fragment) {
        fragment.startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)

        loginStatus = LoginStatus.SigningIn
    }

    override fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                loginStatus = if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    LoginStatus.SignedOut
                } else {
                    LoginStatus.SignInError
                }
            }
        }
    }

    override fun signOut() {
        auth.signOut()

        loginStatus = LoginStatus.SignedOut
    }

    private fun firebaseAuthWithGoogle(googleAccount: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(googleAccount?.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                loginStatus = if (task.isSuccessful) {
                    LoginStatus.SignedIn(getSignedInEmail())
                } else {
                    LoginStatus.SignInError
                }
            }
    }

    private fun getSignedInEmail(): String {
        return auth.currentUser?.email ?: ""
    }
}
