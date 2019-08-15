package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.App
import com.github.sikv.photos.R

class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        childFragmentManager.beginTransaction()
                .replace(R.id.settingsContainer, PreferenceFragment())
                .commit()

        return view
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
                getString(R.string.pref_dark_theme) -> {
                    App.instance?.updateTheme()
                    true
                }
                else -> {
                    super.onPreferenceTreeClick(preference)
                }
            }
        }
    }
}