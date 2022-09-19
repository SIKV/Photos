package com.github.sikv.photos.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.sikv.photos.R
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.util.ThemeManager
import com.github.sikv.photos.viewmodel.MoreUiState
import com.github.sikv.photos.viewmodel.MoreViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PreferenceFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var themeManager: ThemeManager

    private val viewModel: MoreViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>(getString(R.string._pref_theme))
            ?.setOnPreferenceChangeListener { _, newValue ->
                themeManager.applyTheme(newValue as? String)
                true
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectUiState()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            getString(R.string._pref_send_feedback) -> {

                showFragment(com.github.sikv.photos.feedback.FeedbackFragment())

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

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState is MoreUiState.Data) {
                        findPreference<Preference>(getString(R.string._pref_app_version))
                            ?.summary = uiState.appVersion
                    }
                }
            }
        }
    }
}

private fun PreferenceFragmentCompat.showFragment(fragment: Fragment) {
    (parentFragment as? BaseFragment)?.navigation?.addFragment(fragment)
}