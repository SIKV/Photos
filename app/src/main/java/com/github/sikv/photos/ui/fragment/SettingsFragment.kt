package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.util.LoginStatus
import com.github.sikv.photos.util.ViewUtils
import com.github.sikv.photos.viewmodel.PreferenceViewModel

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

        ViewUtils.setToolbarTitle(this, R.string.settings)
    }

    /**
     * PreferenceFragment
     */

    internal class PreferenceFragment : PreferenceFragmentCompat() {

        private val viewModel: PreferenceViewModel by lazy {
            ViewModelProviders.of(this).get(PreferenceViewModel::class.java)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            observe()
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return when (preference?.key) {
                getString(R.string._pref_login) -> {
                    viewModel.login()
                    true
                }

                getString(R.string._pref_logout) -> {
                    viewModel.logout()
                    true
                }

                getString(R.string._pref_dark_theme) -> {
                    App.instance.updateTheme()
                    true
                }

                getString(R.string._pref_report_problem) -> {
                    showFragment(FeedbackFragment.newReportProblemFragment())
                    return true
                }

                getString(R.string._pref_send_feedback) -> {
                    showFragment(FeedbackFragment.newSendFeedbackFragment())
                    return true
                }

                else -> {
                    super.onPreferenceTreeClick(preference)
                }
            }
        }

        private fun observe() {
            viewModel.loginStatusChangedLiveData.observe(viewLifecycleOwner, Observer {
                findPreference<Preference>(getString(R.string._pref_login))?.isVisible = it == LoginStatus.LOGGED_OUT
                findPreference<Preference>(getString(R.string._pref_logout))?.isVisible = it == LoginStatus.LOGGED_IN
            })
        }

        private fun showFragment(fragment: Fragment) {
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.settingsFragmentContainer, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }
}