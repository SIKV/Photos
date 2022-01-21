package com.github.sikv.photos.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.R
import com.github.sikv.photos.config.feature.FeatureFlag
import com.github.sikv.photos.config.feature.FeatureFlagProvider
import com.github.sikv.photos.model.LoginStatus
import com.github.sikv.photos.ui.compose.NothingHereScreen
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.disableScrollableToolbar
import com.github.sikv.photos.util.setupToolbar
import com.github.sikv.photos.util.showFragment
import com.github.sikv.photos.viewmodel.MoreViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoreFragment : BaseFragment() {

    @Inject
    lateinit var featureFlagProvider: FeatureFlagProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        if (savedInstanceState == null) {
            val contentFragment = if (showContentFragment()) {
                PreferenceFragment()
            } else {
                NothingHereFragment()
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, contentFragment)
                .commit()
        }

        return view
    }

    private fun showContentFragment() = featureFlagProvider.isFeatureEnabled(FeatureFlag.SYNC)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(R.string.more)
        disableScrollableToolbar()
    }

    override fun onCreateToolbar(): FragmentToolbar {
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

    @AndroidEntryPoint
    class PreferenceFragment : PreferenceFragmentCompat() {

        @Inject
        lateinit var featureFlagProvider: FeatureFlagProvider

        private val viewModel: MoreViewModel by viewModels()

        private var signInSnackbar: Snackbar? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_more, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            if (featureFlagProvider.isFeatureEnabled(FeatureFlag.SYNC)) {
                observeLoginStatus()
            } else {
                findPreference<Preference>(getString(R.string._pref_sign_in))?.isVisible = false
                findPreference<Preference>(getString(R.string._pref_sign_out))?.isVisible = false
            }
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

        private fun observeLoginStatus() {
            viewModel.loginStatusChanged.observe(viewLifecycleOwner, { loginStatus ->
                handleSignInVisibility(loginStatus)

                when (loginStatus) {
                    LoginStatus.SigningIn -> {
                        view?.let { view ->
                            signInSnackbar = Snackbar.make(
                                view,
                                R.string.signing_in,
                                Snackbar.LENGTH_INDEFINITE
                            )
                            signInSnackbar?.show()
                        }
                    }
                    LoginStatus.SignInError -> {
                        signInSnackbar?.dismiss()
                    }
                    else -> signInSnackbar?.dismiss()
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
                    findPreference<Preference>(getString(R.string._pref_sign_out))?.isVisible =
                        false
                }
                else -> {}
            }
        }
    }
}
