package com.github.sikv.photos.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.R
import com.github.sikv.photos.manager.ThemeManager
import com.github.sikv.photos.util.disableScrollableToolbar
import com.github.sikv.photos.util.setupToolbar
import com.github.sikv.photos.util.showFragment
import com.github.sikv.photos.viewmodel.MoreViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoreFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        setupToolbar(R.string.more)
        disableScrollableToolbar()
    }

    @AndroidEntryPoint
    class PreferenceFragment : PreferenceFragmentCompat() {

        @Inject
        lateinit var themeManager: ThemeManager

        private val viewModel: MoreViewModel by viewModels()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_more, rootKey)

            findPreference<ListPreference>(getString(R.string._pref_theme))
                ?.setOnPreferenceChangeListener { _, newValue ->
                    themeManager.applyTheme(newValue as? String)
                    true
                }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            observeAppVersion()
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            return when (preference.key) {
                getString(R.string._pref_send_feedback) -> {
                    showFragment(FeedbackFragment())
                    return true
                }
                getString(R.string._pref_open_source_licences) -> {
                    startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    OssLicensesMenuActivity.setActivityTitle(
                        context?.getString(R.string.open_source_licences) ?: ""
                    )
                    return true
                }
                else -> super.onPreferenceTreeClick(preference)
            }
        }

        private fun observeAppVersion() {
            viewModel.showAppVersionEvent.observe(viewLifecycleOwner) {
                findPreference<Preference>(getString(R.string._pref_app_version))?.summary = it.peekContent()
            }
        }
    }
}
