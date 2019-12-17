package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import kotlinx.android.synthetic.main.layout_scrollable_toolbar.*

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

        toolbarTitleText.setText(R.string.settings)
    }

    /**
     * PreferenceFragment
     */

    internal class PreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return when (preference?.key) {
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

        private fun showFragment(fragment: Fragment) {
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.settingsFragmentContainer, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }
}