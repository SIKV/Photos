package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import kotlinx.android.synthetic.main.layout_scrollable_toolbar.*

class FeedbackFragment : BaseFragment() {

    companion object {
        private const val MODE_REPORT_PROBLEM = 1
        private const val MODE_SEND_FEEDBACK = 2

        private const val KEY_MODE = "key_mode"

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

    private var mode: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mode = arguments?.getInt(KEY_MODE) ?: 0

        when (mode) {
            MODE_REPORT_PROBLEM -> {
                toolbarTitleText.setText(R.string.report_problem)
            }

            MODE_SEND_FEEDBACK -> {
                toolbarTitleText.setText(R.string.send_feedback)
            }
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
}