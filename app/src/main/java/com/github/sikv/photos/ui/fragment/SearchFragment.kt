package com.github.sikv.photos.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.github.sikv.photos.R
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.ui.adapter.ViewPagerAdapter
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.Utils.dp2px
import kotlinx.android.synthetic.main.fragment_search.*


const val SEARCH_MARGIN_ANIMATION_DURATION = 250L
const val TAB_LAYOUT_BACKGROUND_ANIMATION_DURATION = 750L


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

        clearButtonVisible = false
    }

    private fun searchPhotos(text: String) {
        viewPagerAdapter.fragments.forEach {
            (it as? SingleSearchFragment)?.searchPhotos(text)
        }

        searchTabLayout.visibility = View.VISIBLE
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
        }

        searchEdit.setOnFocusChangeListener { _, hasFocus ->
            animateSearchMargins(hasFocus)
            animateTabLayoutBackground(hasFocus)
        }
    }

    private fun animateSearchMargins(hasFocus: Boolean) {
        val margin = dp2px(6)

        var from = 0
        var to = margin

        if (hasFocus) {
            from = margin
            to = 0
        }

        val animator = ValueAnimator.ofInt(from, to)

        animator.addUpdateListener { valueAnimator ->
            val params = searchEditLayout.layoutParams as? ViewGroup.MarginLayoutParams

            val newMargin = valueAnimator.animatedValue as Int
            params?.setMargins(newMargin, newMargin, newMargin, newMargin)

            searchEditLayout.layoutParams = params
        }

        animator.duration = SEARCH_MARGIN_ANIMATION_DURATION
        animator.start()
    }

    private fun animateTabLayoutBackground(hasFocus: Boolean) {
        context?.let { context ->
            var fromColor = ContextCompat.getColor(context, R.color.colorSearchBackground)
            var toColor = ContextCompat.getColor(context, R.color.colorPrimary)

            if (hasFocus) {
                fromColor = ContextCompat.getColor(context, R.color.colorPrimary)
                toColor = ContextCompat.getColor(context, R.color.colorSearchBackground)
            }

            ObjectAnimator.ofObject(searchTabLayout, "backgroundColor", ArgbEvaluator(), fromColor, toColor)
                    .setDuration(TAB_LAYOUT_BACKGROUND_ANIMATION_DURATION)
                    .start()
        }
    }
}