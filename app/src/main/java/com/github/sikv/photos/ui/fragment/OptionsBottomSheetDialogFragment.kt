package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.github.sikv.photos.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class OptionsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_OPTIONS = "key_options"

        fun newInstance(items: List<String>, onItemSelected: (Int) -> Unit): OptionsBottomSheetDialogFragment {
            val dialogFragment = OptionsBottomSheetDialogFragment()
            dialogFragment.onItemSelected = onItemSelected

            val args = Bundle()
            args.putStringArrayList(KEY_OPTIONS, ArrayList(items))

            dialogFragment.arguments = args

            return dialogFragment
        }
    }

    private var onItemSelected: ((Int) -> Unit)? = null

    override fun getTheme(): Int = R.style.BottomSheetTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_options, container, false)

        arguments?.let { args ->
            args.getStringArrayList(KEY_OPTIONS)?.let { optionsList ->
                val layout = view.findViewById<ViewGroup>(R.id.optionsBottomSheetRootLayout)

                optionsList.forEachIndexed { index, option ->
                    val button = Button(ContextThemeWrapper(context, R.style.BottomSheetButton), null, 0)
                    button.text = option

                    button.setOnClickListener {
                        onItemSelected?.invoke(index)
                        dismiss()
                    }

                    layout.addView(button)
                }
            }
        }

        return view
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "")
    }
}