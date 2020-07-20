package com.github.sikv.photos.enumeration

sealed class LoginStatus {
    object NotSet : LoginStatus()
    object SigningIn : LoginStatus()
    class SignedIn(val signedInAs: String) : LoginStatus()
    object SignInError : LoginStatus()
    object SignedOut : LoginStatus()
}