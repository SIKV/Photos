package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.FeedbackMode
import com.github.sikv.photos.enumeration.RequestStatus
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.*
import com.github.sikv.photos.viewmodel.FeedbackViewModel
import com.github.sikv.photos.viewmodel.FeedbackViewModelFactory
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : BaseFragment() {

    companion object {
        private const val KEY_MODE = "mode"

        fun newInstance(mode: FeedbackMode): FeedbackFragment {
            val args = Bundle()
            args.putSerializable(KEY_MODE, mode)

            val fragment = FeedbackFragment()
            fragment.arguments = args

            return fragment
        }
    }

    private val viewModel by lazy {
        val mode: FeedbackMode = arguments?.getSerializable(KEY_MODE) as FeedbackMode

        val viewModelFactory = FeedbackViewModelFactory(requireActivity().application, mode)
        ViewModelProvider(this, viewModelFactory).get(FeedbackViewModel::class.java)
    }

    override val overrideBackground: Boolean = true

    private var sendMenuItem: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitleWithBackButton(null) {
            navigation?.backPressed()
        }

        context?.showSoftInput(descriptionEdit)

        observe()

        descriptionEdit.resetErrorWhenTextChanged(descriptionInputLayout)
    }

    override fun onCreateToolbar(): FragmentToolbar? {
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
                                                email = emailEdit.text.toString(),
                                                description = descriptionEdit.text.toString()
                                        )

                                        return true
                                    }
                                }
                        )
                ).build()
    }

    private fun observe() {
        viewModel.showTitleEvent.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { title ->
                setToolbarTitle(title)
            }
        })

        viewModel.showDescriptionHintEvent.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { hint ->
                descriptionInputLayout.hint = hint
            }
        })

        viewModel.sendFeedbackStatusEvent.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.InProgress -> {
                        sendMenuItem?.setActionView(R.layout.layout_action_progress)
                    }

                    is RequestStatus.Done -> {
                        sendMenuItem?.actionView = null

                        postGlobalMessage(requestStatus.message)

                        if (requestStatus.success) {
                            navigation?.backPressed()
                        } else {
                            activity?.hideSoftInput()
                        }
                    }

                    is RequestStatus.ValidationError -> {
                        descriptionInputLayout.error = requestStatus.message
                    }
                }
            }
        })
    }
}