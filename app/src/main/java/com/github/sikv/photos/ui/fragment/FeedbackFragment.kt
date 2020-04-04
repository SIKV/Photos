package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.setToolbarTitleWithBackButton
import com.github.sikv.photos.util.showSoftInput
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : BaseFragment() {

    companion object {
        private const val MODE_REPORT_PROBLEM = 1
        private const val MODE_SEND_FEEDBACK = 2

        private const val KEY_MODE = "key_mode"

        private const val DESCRIPTION_MAX_LENGTH = 500

        fun newReportProblemFragment(): FeedbackFragment {
            val args = Bundle()
            args.putInt(KEY_MODE, MODE_REPORT_PROBLEM)

            val fragment = FeedbackFragment()
            fragment.arguments = args

            return fragment
        }

        fun newSendFeedbackFragment(): FeedbackFragment {
            val args = Bundle()
            args.putInt(KEY_MODE, MODE_SEND_FEEDBACK)

            val fragment = FeedbackFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override val overrideBackground: Boolean = true

    private var mode: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mode = arguments?.getInt(KEY_MODE) ?: 0

        val title = when (mode) {
            MODE_REPORT_PROBLEM -> R.string.report_problem
            MODE_SEND_FEEDBACK -> R.string.send_feedback

            else -> 0
        }

        setToolbarTitleWithBackButton(title) {
            navigation?.backPressed()
        }

        context?.showSoftInput(descriptionEdit)

        init(mode)
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
                                        // TODO Implement
                                        return true
                                    }
                                }
                        )
                ).build()
    }

    private fun init(mode: Int) {
        when (mode) {
            MODE_REPORT_PROBLEM -> {
                descriptionEdit.setHint(R.string.what_went_wrong)
            }
            MODE_SEND_FEEDBACK -> {
                descriptionEdit.setHint(R.string.what_to_improve)
            }
        }

        descriptionEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateDescriptionLimitText(s?.length)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        updateDescriptionLimitText(0)
    }

    private fun updateDescriptionLimitText(length: Int?) {
        limitText.text = getString(R.string.d_delimiter_d, length, DESCRIPTION_MAX_LENGTH)
    }
}