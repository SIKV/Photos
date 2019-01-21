package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.data.SearchSource

class SearchFragment : Fragment() {

    companion object {
        private const val SEARCH_SOURCE = "search_source"

        fun newInstance(searchSource: SearchSource): SearchFragment {
            val fragment = SearchFragment()

            val args = Bundle()
            args.putSerializable(SEARCH_SOURCE, searchSource)

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        return view
    }
}