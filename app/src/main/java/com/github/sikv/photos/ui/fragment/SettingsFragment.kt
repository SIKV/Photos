package com.github.sikv.photos.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.LoginStatus
import com.github.sikv.photos.util.defaultStyle
import com.github.sikv.photos.util.disableScrollableToolbar
import com.github.sikv.photos.util.setToolbarTitle
import com.github.sikv.photos.viewmodel.PreferenceViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        childFragmentManager.beginTransaction()
                .replace(R.id.settingsPreferenceContainer, PreferenceFragment())
                .commit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(R.string.settings)
        disableScrollableToolbar()
    }

    /**
     * PreferenceFragment
     */

    internal class PreferenceFragment : PreferenceFragmentCompat() {

        private val viewModel: PreferenceViewModel by lazy {
            ViewModelProvider(this).get(PreferenceViewModel::class.java)
        }

        private var signingInSnackbar: Snackbar? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            handleVisibility(viewModel.accountManager.loginStatus)
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

                getString(R.string._pref_dark_theme) -> {
                    App.instance.updateTheme()
                    true
                }

                getString(R.string._pref_send_feedback) -> {
                    showFragment(FeedbackFragment.newSendFeedbackFragment())
                    return true
                }

                getString(R.string._pref_report_problem) -> {
                    showFragment(FeedbackFragment.newReportProblemFragment())
                    return true
                }

                getString(R.string._pref_open_source_licences) -> {
                    startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    OssLicensesMenuActivity.setActivityTitle(context?.getString(R.string.open_source_licences) ?: "")
                    return true
                }

                else -> {
                    super.onPreferenceTreeClick(preference)
                }
            }
        }

        private fun observe() {
            viewModel.loginStatusChangedLiveData.observe(viewLifecycleOwner, Observer {
                handleVisibility(it)

                if (it == LoginStatus.SIGNING_IN) {
                    view?.let { view ->
                        signingInSnackbar = Snackbar.make(view, R.string.signing_in, Snackbar.LENGTH_INDEFINITE)
                                .defaultStyle()

                        signingInSnackbar?.show()
                    }
                } else {
                    signingInSnackbar?.dismiss()
                }
            })

            viewModel.showAppVersionEvent.observe(viewLifecycleOwner, Observer {
                it.getContentIfNotHandled()?.let { appVersion ->
                    findPreference<Preference>(getString(R.string._pref_app_version))?.summary = appVersion
                }
            })
        }

        private fun handleVisibility(loginStatus: LoginStatus) {
            findPreference<Preference>(getString(R.string._pref_sign_in))?.isVisible = loginStatus == LoginStatus.SIGNED_OUT
            findPreference<Preference>(getString(R.string._pref_sign_out))?.isVisible = loginStatus == LoginStatus.SIGNED_IN
        }

        private fun showFragment(fragment: Fragment) {
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.settingsFragmentContainer, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }
}