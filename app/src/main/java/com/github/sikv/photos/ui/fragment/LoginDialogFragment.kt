package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.github.sikv.photos.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_login_options.*

class LoginDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(signInWithGoogleClickListener: View.OnClickListener): LoginDialogFragment {
            val fragment = LoginDialogFragment()
            fragment.signInWithGoogleClickListener = signInWithGoogleClickListener

            return fragment
        }
    }

    private var signInWithGoogleClickListener: View.OnClickListener? = null

    override fun getTheme(): Int = R.style.BottomSheetTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_bottom_sheet, container, false)

        view.findViewById<TextView>(R.id.titleText).run {
            visibility = View.VISIBLE
            setText(R.string.sign_in)
        }

        val rootLayout = view.findViewById<ViewGroup>(R.id.rootLayout)
        rootLayout.addView(layoutInflater.inflate(R.layout.layout_login_options, rootLayout, false))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInWithGoogleButton.setOnClickListener {
            signInWithGoogleClickListener?.onClick(it)
            dismiss()
        }
    }

    fun show(fragmentManager: FragmentManager?) {
        fragmentManager?.let {
            show(it, "")
        }
    }
}