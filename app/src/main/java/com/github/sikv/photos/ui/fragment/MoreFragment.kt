package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.common.ui.disableScrollableToolbar
import com.github.sikv.photos.common.ui.setupToolbar
import com.github.sikv.photos.preferences.PreferenceFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PreferenceFragment())
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(R.string.more)
        disableScrollableToolbar()
    }
}
