package com.github.sikv.photos.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.sikv.photos.common.ui.*
import com.github.sikv.photos.common.ui.toolbar.FragmentToolbar
import com.github.sikv.photos.domain.RequestStatus
import com.github.sikv.photos.feedback.databinding.FragmentFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedbackFragment : BaseFragment() {

    private val viewModel: FeedbackViewModel by viewModels()

    override val overrideBackground: Boolean = true

    private var sendMenuItem: MenuItem? = null

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBackButton(
            title = R.string.send_feedback,
            navigationOnClickListener = { navigation?.backPressed() }
        )

        context?.showSoftInput(binding.emailEdit)

        collectUiState()

        binding.emailEdit.resetErrorWhenTextChanged(binding.emailInputLayout)
        binding.descriptionEdit.resetErrorWhenTextChanged(binding.descriptionInputLayout)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onCreateToolbar(): FragmentToolbar {
        return FragmentToolbar.Builder()
            .withId(R.id.toolbar)
            .withMenu(R.menu.menu_feedback)
            .withMenuItems(
                listOf(
                    R.id.itemSubmit
                ),
                listOf(
                    object : MenuItem.OnMenuItemClickListener {
                        override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                            sendMenuItem = menuItem

                            viewModel.send(
                                email = binding.emailEdit.text.toString(),
                                description = binding.descriptionEdit.text.toString()
                            )
                            return true
                        }
                    }
                )
            ).build()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState is FeedbackUiState.Data) {
                        updateRequestStatus(uiState.requestStatus)
                    }
                }
            }
        }
    }

    private fun updateRequestStatus(requestStatus: RequestStatus) {
        when (requestStatus) {
            is RequestStatus.InProgress -> {
                sendMenuItem?.setActionView(R.layout.layout_action_progress)
            }
            is RequestStatus.Done -> {
                sendMenuItem?.actionView = null

                showMessage(requestStatus.message)

                if (requestStatus.success) {
                    navigation?.backPressed()
                } else {
                    activity?.hideSoftInput()
                }
            }
            is RequestStatus.ValidationError -> {
                when (requestStatus.invalidInputIndex) {
                    1 -> binding.emailInputLayout.error = requestStatus.message
                    2 -> binding.descriptionInputLayout.error = requestStatus.message
                    else -> {
                    }
                }
            }
        }
    }
}
