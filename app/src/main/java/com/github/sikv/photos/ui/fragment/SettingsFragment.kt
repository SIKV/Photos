package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.util.disableScrollableToolbar
import com.github.sikv.photos.util.setToolbarTitle

class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        childFragmentManager.beginTransaction()
                .replace(R.id.settingsPreferenceContainer, PreferenceFragment())
                .commit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(R.string.settings)
        disableScrollableToolbar()
    }
}