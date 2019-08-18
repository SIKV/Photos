package com.github.sikv.photos.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.util.Utils
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment() {

    companion object {
        private const val SEARCH_ANIMATION_DURATION = 250L
        private const val TAB_LAYOUT_BACKGROUND_ANIMATION_DURATION = 750L

        private const val KEY_LAST_SEARCH_TEXT = "key_last_search_text"
    }

    private lateinit var viewPagerAdapter: SearchViewPagerAdapter

    private var clearButtonVisible: Boolean = false
        set(value) {
            field = value

            searchClearButton.visibility = if (field) View.VISIBLE else View.INVISIBLE
        }

    private var lastSearchText: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        setListeners()

        clearButtonVisible = false
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

        viewPagerAdapter.searchPhotos(searchViewPager, text)

        searchTabLayout.visibility = View.VISIBLE
    }

    private fun initViewPager() {
        viewPagerAdapter = SearchViewPagerAdapter(childFragmentManager)

        searchViewPager.adapter = viewPagerAdapter

        searchTabLayout.setupWithViewPager(searchViewPager)
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
            animateSearch(hasFocus)
            animateTabLayoutBackground(hasFocus)
        }
    }

    private fun animateSearch(hasFocus: Boolean) {
        val margin: Float = context?.resources?.getDimension(R.dimen.searchMargins) ?: 0.0F
        val cornerRadius: Float = context?.resources?.getDimension(R.dimen.searchCornerRadius) ?: 0.0F

        var fromMargin = 0
        var toMargin: Int = margin.toInt()

        var fromCornerRadius = 0
        var toCornerRadius: Int = cornerRadius.toInt()

        if (hasFocus) {
            fromMargin = margin.toInt()
            toMargin = 0

            fromCornerRadius = cornerRadius.toInt()
            toCornerRadius = 0
        }

        val marginAnimator = ValueAnimator.ofInt(fromMargin, toMargin)
        val cornerRadiusAnimator = ValueAnimator.ofInt(fromCornerRadius, toCornerRadius)

        marginAnimator.addUpdateListener { valueAnimator ->
            val params = searchEditCard.layoutParams as? ViewGroup.MarginLayoutParams

            val newMargin = valueAnimator.animatedValue as Int
            params?.setMargins(newMargin, newMargin, newMargin, newMargin)

            searchEditCard.layoutParams = params
        }

        cornerRadiusAnimator.addUpdateListener { valueAnimator ->
            val newCornerRadius = valueAnimator.animatedValue as Int

            searchEditCard.radius = newCornerRadius.toFloat()
        }

        marginAnimator.duration = SEARCH_ANIMATION_DURATION
        cornerRadiusAnimator.duration = SEARCH_ANIMATION_DURATION

        marginAnimator.start()
        cornerRadiusAnimator.start()
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

    /**
     * SearchViewPagerAdapter
     */

    private class SearchViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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
                    App.instance?.getString(R.string.unsplash)
                }
                1 -> {
                    App.instance?.getString(R.string.pexels)
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