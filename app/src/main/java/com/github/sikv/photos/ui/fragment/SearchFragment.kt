package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.github.sikv.photos.R
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.util.Utils
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : BaseFragment() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private var clearButtonVisible: Boolean = false
        set(value) {
            field = value

            searchClearButton.visibility = if (field) View.VISIBLE else View.INVISIBLE
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        init()

        searchRequestFocus(false)

        clearButtonVisible = false
    }

    private fun searchPhotos(text: String) {
        viewPagerAdapter.fragments.forEach {
            it.searchPhotos(text)
        }
    }

    private fun searchRequestFocus(showSoftInput: Boolean = true) {
        searchEdit.requestFocus()

        if (showSoftInput) {
            Utils.showSoftInput(context!!, searchEdit)
        }
    }

    private fun initViewPager() {
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)

        viewPagerAdapter.addFragment(SingleSearchFragment.newInstance(PhotoSource.UNSPLASH), getString(R.string.unsplash))
        viewPagerAdapter.addFragment(SingleSearchFragment.newInstance(PhotoSource.PEXELS), getString(R.string.pexels))

        searchViewPager.adapter = viewPagerAdapter
        searchTabLayout.setupWithViewPager(searchViewPager)
    }

    private fun init() {
        searchEdit.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.hideSoftInput(context!!, searchEdit)
                searchEdit.clearFocus()

                searchPhotos(textView.text.toString())
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                clearButtonVisible = editable?.isNotEmpty() ?: false
            }

            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        searchClearButton.setOnClickListener {
            searchEdit.text.clear()
            searchRequestFocus()
        }
    }

    internal inner class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        var fragments: MutableList<SingleSearchFragment> = mutableListOf()
            private set

        private var titles: MutableList<String> = mutableListOf()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: SingleSearchFragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}