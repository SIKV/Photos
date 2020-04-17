package com.github.sikv.photos.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.github.sikv.photos.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_option.view.*
import java.util.*

class OptionsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_OPTIONS = "key_options"
        private const val KEY_SELECTED_OPTION_INDEX = "key_selected_option_index"

        fun newInstance(options: List<String>, selectedOptionIndex: Int?, onItemSelected: (Int) -> Unit): OptionsBottomSheetDialogFragment {
            val dialogFragment = OptionsBottomSheetDialogFragment()
            dialogFragment.onItemSelected = onItemSelected

            val args = Bundle()
            args.putStringArrayList(KEY_OPTIONS, ArrayList(options))

            selectedOptionIndex?.let {
                args.putInt(KEY_SELECTED_OPTION_INDEX, it)
            }

            dialogFragment.arguments = args

            return dialogFragment
        }
    }

    private var onItemSelected: ((Int) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_bottom_sheet, container, false)

        arguments?.let { args ->
            args.getStringArrayList(KEY_OPTIONS)?.let { optionsList ->
                val selectedOptionIndex = args.getInt(KEY_SELECTED_OPTION_INDEX, -1)

                val layout = view.findViewById<ViewGroup>(R.id.rootLayout)

                optionsList.forEachIndexed { index, optionText ->
                    val optionLayout = LayoutInflater.from(context).inflate(R.layout.item_option, null, false)

                    optionLayout.optionText.text = optionText

                    if (index == selectedOptionIndex) {
                        optionLayout.optionSelectedImage.setImageResource(R.drawable.ic_check_secondary_24dp)
                    } else {
                        optionLayout.optionSelectedImage.setImageDrawable(null)
                    }

                    optionLayout.setOnClickListener {
                        onItemSelected?.invoke(index)
                        dismiss()
                    }

                    layout.addView(optionLayout)
                }

                addCancelOption(layout)
            }
        }

        return view
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "")
    }

    private fun addCancelOption(layout: ViewGroup) {
        val optionLayout = LayoutInflater.from(context).inflate(R.layout.item_option, null, false)

        optionLayout.optionText.text = getString(R.string.cancel)
        optionLayout.optionText.alpha = 0.5F

        optionLayout.optionSelectedImage.setImageDrawable(null)

        optionLayout.setOnClickListener {
            dismiss()
        }

        layout.addView(optionLayout)
    }
}