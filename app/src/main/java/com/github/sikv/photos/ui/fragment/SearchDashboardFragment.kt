package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.adapter.TagAdapter
import kotlinx.android.synthetic.main.fragment_search_dashboard.*

class SearchDashboardFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchImage.setOnClickListener {
            showSearchFragment()
        }

        searchText.setOnClickListener {
            showSearchFragment()
        }

        val tagList = listOf("Tag 1", "Tag 2", "Tag 3", "Tag 4", "Tag 5")
        tagsRecycler.adapter = TagAdapter(tagList)
    }

    private fun showSearchFragment() {
        fragmentManager?.beginTransaction()
                ?.replace(R.id.searchContainer, SearchFragment())
                ?.addToBackStack(null)
                ?.commit()
    }
}