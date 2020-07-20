package com.github.sikv.photos.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.style.ClickableSpan
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
import com.github.sikv.photos.util.disableScrollableToolbar
import com.github.sikv.photos.util.makeClickable
import com.github.sikv.photos.util.openUrl
import com.github.sikv.photos.util.setToolbarTitleWithBackButton
import com.github.sikv.photos.viewmodel.SettingsViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    override val overrideBackground: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PreferenceFragment())
                .commit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitleWithBackButton(R.string.settings) {
            navigation?.backPressed()
        }

        disableScrollableToolbar()

        showIconsAttribution()
    }

    private fun showIconsAttribution() {
        val thoseIcons = getString(R.string.those_icons)
        val thoseIconsUrl = "https://www.flaticon.com/authors/those-icons"

        val freepik = getString(R.string.freepik)
        val freepikUrl = "https://www.flaticon.com/authors/freepik"

        val flaticon = getString(R.string.flaticon)
        val flaticonUrl = "https://www.flaticon.com"

        iconsAttributionText.text = getString(R.string.icons_made_by_s_and_s_from_s,
                thoseIcons, freepik, flaticon)

        iconsAttributionText.makeClickable(arrayOf(
                thoseIcons, freepik, flaticon),
                arrayOf(
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                activity?.openUrl(thoseIconsUrl)
                            }
                        },
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                activity?.openUrl(freepikUrl)
                            }
                        },
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                activity?.openUrl(flaticonUrl)
                            }
                        }
                )
        )
    }

    /**
     *
     */

    class PreferenceFragment : PreferenceFragmentCompat() {

        private val viewModel: SettingsViewModel by lazy {
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            observe()
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return when (preference?.key) {
                getString(R.string._pref_dark_theme) -> {
                    App.instance.updateTheme()
                    true
                }

                getString(R.string._pref_send_feedback) -> {
                    showFragment(FeedbackFragment.newInstance())
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
            viewModel.showAppVersionEvent.observe(viewLifecycleOwner, Observer {
                it.getContentIfNotHandled()?.let { appVersion ->
                    findPreference<Preference>(getString(R.string._pref_app_version))?.summary = appVersion
                }
            })
        }

        private fun showFragment(fragment: Fragment) {
            (parentFragment as? BaseFragment)?.navigation?.addFragment(fragment)
        }
    }
}