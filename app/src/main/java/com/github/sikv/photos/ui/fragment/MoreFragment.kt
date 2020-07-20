package com.github.sikv.photos.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.LoginStatus
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.disableScrollableToolbar
import com.github.sikv.photos.util.setToolbarTitle
import com.github.sikv.photos.viewmodel.MoreViewModel
import com.google.android.material.snackbar.Snackbar

class MoreFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PreferenceFragment())
                    .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(R.string.more)
        disableScrollableToolbar()
    }

    override fun onCreateToolbar(): FragmentToolbar? {
        return FragmentToolbar.Builder()
                .withId(R.id.toolbar)
                .withMenu(R.menu.menu_more)
                .withMenuItems(
                        listOf(
                                R.id.itemSettings
                        ),
                        listOf(
                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        navigation?.addFragment(SettingsFragment())
                                        return true
                                    }
                                }
                        )
                )
                .build()
    }

    /**
     *
     */

    class PreferenceFragment : PreferenceFragmentCompat() {

        private val viewModel: MoreViewModel by lazy {
            ViewModelProvider(this).get(MoreViewModel::class.java)
        }

        private var signingInSnackbar: Snackbar? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_more, rootKey)

            handleSignInVisibility(viewModel.accountManager.loginStatus)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            observe()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            viewModel.handleSignInResult(requestCode, resultCode, data)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return when (preference?.key) {
                getString(R.string._pref_sign_in) -> {
                    viewModel.signInWithGoogle(this)
                    true
                }

                getString(R.string._pref_sign_out) -> {
                    viewModel.signOut()
                    true
                }

                else -> super.onPreferenceTreeClick(preference)
            }
        }

        private fun observe() {
            viewModel.loginStatusChangedEvent.observe(viewLifecycleOwner, Observer { loginStatusEvent ->
                loginStatusEvent.getContentIfNotHandled()?.let { loginStatus ->
                    handleSignInVisibility(loginStatus)

                    when (loginStatus) {
                        LoginStatus.SigningIn -> {
                            view?.let { view ->
                                signingInSnackbar = Snackbar.make(view, R.string.signing_in, Snackbar.LENGTH_INDEFINITE)
                                signingInSnackbar?.show()
                            }
                        }

                        LoginStatus.SignInError -> {
                            signingInSnackbar?.dismiss()

                            App.instance.postGlobalMessage(getString(R.string.error_signing_in))
                        }

                        else -> signingInSnackbar?.dismiss()
                    }
                }
            })
        }

        private fun handleSignInVisibility(loginStatus: LoginStatus) {
            when (loginStatus) {
                is LoginStatus.SignedIn -> {
                    findPreference<Preference>(getString(R.string._pref_sign_in))?.isVisible = false

                    findPreference<Preference>(getString(R.string._pref_sign_out))?.apply {
                        isVisible = true
                        summary = getString(R.string.signed_in_as_s, loginStatus.signedInAs)
                    }
                }

                is LoginStatus.SignedOut, is LoginStatus.SignInError -> {
                    findPreference<Preference>(getString(R.string._pref_sign_in))?.isVisible = true
                    findPreference<Preference>(getString(R.string._pref_sign_out))?.isVisible = false
                }
            }
        }
    }
}