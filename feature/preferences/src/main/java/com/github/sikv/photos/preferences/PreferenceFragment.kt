package com.github.sikv.photos.preferences

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.navigation.route.FeedbackRoute
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreferenceFragment : BaseFragment() {

    @Inject
    lateinit var feedbackRoute: FeedbackRoute

    private val viewModel: PreferenceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                Mdc3Theme {
                    val uiState = viewModel.uiState.collectAsState()

                    PreferenceScreen(
                        preferences = uiState.value.preferences,
                        onPreferencePress = ::handlePreferencePress
                    )
                }
            }
        }
    }

    private fun handlePreferencePress(action: PreferenceAction) {
        when (action) {
            PreferenceAction.ChangeTheme -> {
                viewModel.createChangeThemeDialog().show(childFragmentManager)
            }
            PreferenceAction.SendFeedback -> {
                feedbackRoute.present((parentFragment as? BaseFragment)?.navigation)
            }
            PreferenceAction.OpenSourceLicenses -> {
                startActivity(Intent(context, OssLicensesMenuActivity::class.java))

                OssLicensesMenuActivity.setActivityTitle(
                    context?.getString(R.string.open_source_licences) ?: ""
                )
            }
            PreferenceAction.AppVersion -> {
                // Nothing need to do.
            }
        }
    }
}
