package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.sikv.photos.R
import com.github.sikv.photos.databinding.FragmentFeedbackBinding
import com.github.sikv.photos.enumeration.RequestStatus
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.hideSoftInput
import com.github.sikv.photos.util.resetErrorWhenTextChanged
import com.github.sikv.photos.util.setToolbarTitleWithBackButton
import com.github.sikv.photos.util.showSoftInput
import com.github.sikv.photos.viewmodel.FeedbackViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : BaseFragment() {

    companion object {
        fun newInstance(): FeedbackFragment {
            return FeedbackFragment()
        }
    }

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedbackViewModel by viewModels()

    override val overrideBackground: Boolean = true

    private var sendMenuItem: MenuItem? = null

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

        setToolbarTitleWithBackButton(R.string.send_feedback) {
            navigation?.backPressed()
        }

        context?.showSoftInput(binding.emailEdit)

        observe()

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

    private fun observe() {
        viewModel.sendFeedbackStatusEvent.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { requestStatus ->
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
        })
    }
}
