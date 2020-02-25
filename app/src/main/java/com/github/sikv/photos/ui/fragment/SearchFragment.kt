package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.ViewUtils
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment() {

    companion object {
        private const val KEY_LAST_SEARCH_TEXT = "key_last_search_text"
    }

    private lateinit var viewPagerAdapter: SearchViewPagerAdapter

    private var lastSearchText: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewUtils.showToolbarBackButton(this) {
            activity?.onBackPressed()
        }

        initViewPager()
        setListeners()

        changeClearButtonVisibility(false, withAnimation = false)

        Utils.showSoftInput(context, searchEdit)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            savedInstanceState.getString(KEY_LAST_SEARCH_TEXT)?.let { text ->
                searchPhotos(text)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        lastSearchText?.let { text ->
            outState.putSerializable(KEY_LAST_SEARCH_TEXT, text)
        }
    }

    private fun searchPhotos(text: String) {
        lastSearchText = text

        viewPagerAdapter.searchPhotos(viewPager, text)

        tabLayout.visibility = View.VISIBLE
    }

    private fun changeClearButtonVisibility(visible: Boolean, withAnimation: Boolean = true) {
        val newVisibility = if (visible) View.VISIBLE else View.INVISIBLE

        if (searchClearButton.visibility != newVisibility) {
            if (withAnimation) {
                ViewUtils.changeVisibilityWithAnimation(searchClearButton, newVisibility)
            } else {
                searchClearButton.visibility = newVisibility
            }
        }
    }

    private fun initViewPager() {
        viewPagerAdapter = SearchViewPagerAdapter(childFragmentManager)

        viewPager.adapter = viewPagerAdapter

        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setListeners() {
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
                changeClearButtonVisibility(editable?.isNotEmpty() ?: false)
            }

            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        searchClearButton.setOnClickListener {
            searchEdit.text.clear()
        }
    }

    /**
     * SearchViewPagerAdapter
     */

    private class SearchViewPagerAdapter(
            fragmentManager: FragmentManager
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    SingleSearchFragment.newInstance(PhotoSource.UNSPLASH)
                }

                1 -> {
                    SingleSearchFragment.newInstance(PhotoSource.PEXELS)
                }

                else -> {
                    Fragment()
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> {
                    App.instance.getString(R.string.unsplash)
                }

                1 -> {
                    App.instance.getString(R.string.pexels)
                }

                else -> {
                    null
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

        fun searchPhotos(viewPager: ViewPager, text: String) {
            startUpdate(viewPager)

            for (i in 0 until count) {
                (instantiateItem(viewPager, i) as? SingleSearchFragment)?.searchPhotos(text)
            }

            finishUpdate(viewPager)
        }
    }
}