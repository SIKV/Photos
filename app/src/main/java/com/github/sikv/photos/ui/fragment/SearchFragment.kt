package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.sikv.photos.databinding.FragmentSearchBinding
import com.github.sikv.photos.enumeration.SearchSource
import com.github.sikv.photos.util.changeVisibilityWithAnimation
import com.github.sikv.photos.util.hideSoftInput
import com.github.sikv.photos.util.showSoftInput
import com.github.sikv.photos.util.showToolbarBackButton

class SearchFragment : BaseFragment() {

    companion object {
        private const val KEY_SEARCH_TEXT = "searchText"
        private const val KEY_LAST_SEARCH_TEXT = "lastSearchText"

        fun newInstance(searchText: String? = null): SearchFragment {
            val fragment = SearchFragment()

            searchText?.let {
                val args = Bundle()
                args.putString(KEY_LAST_SEARCH_TEXT, it)

                fragment.arguments = args
            }

            return fragment
        }
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override val overrideBackground: Boolean = true

    private lateinit var viewPagerAdapter: SearchViewPagerAdapter

    private var lastSearchText: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showToolbarBackButton {
            navigation?.backPressed()
        }

        initViewPager {
            if (savedInstanceState == null) {
                arguments?.getString(KEY_SEARCH_TEXT)?.let { searchText ->
                    binding.searchEdit.append(searchText)
                    searchPhotos(searchText)
                }

                shownKeyboardIfNeeded()
            }
        }

        setListeners()

        changeClearButtonVisibility(false, withAnimation = false)
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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun searchPhotos(text: String) {
        context?.hideSoftInput(binding.searchEdit)

        lastSearchText = text

        viewPagerAdapter.searchPhotos(binding.viewPager, text)
    }

    private fun shownKeyboardIfNeeded() {
        val showKeyboard = arguments?.getString(KEY_SEARCH_TEXT) == null
        var keyboardShown = false

        if (showKeyboard) {
            if (!keyboardShown) {
                keyboardShown = requireActivity().showSoftInput(binding.searchEdit)
            }

            binding.searchEdit.viewTreeObserver.addOnWindowFocusChangeListener(object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus && !keyboardShown) {
                        keyboardShown = requireActivity().showSoftInput(binding.searchEdit)
                    }

                    binding.searchEdit.viewTreeObserver?.removeOnWindowFocusChangeListener(this)
                }
            })
        }
    }

    private fun changeClearButtonVisibility(visible: Boolean, withAnimation: Boolean = true) {
        val newVisibility = if (visible) View.VISIBLE else View.INVISIBLE

        if (binding.searchClearButton.visibility != newVisibility) {
            if (withAnimation) {
                binding.searchClearButton.changeVisibilityWithAnimation(newVisibility)
            } else {
                binding.searchClearButton.visibility = newVisibility
            }
        }
    }

    private fun initViewPager(after: () -> Unit) {
        viewPagerAdapter = SearchViewPagerAdapter(childFragmentManager)

        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = SearchSource.size

        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.viewPager.post {
            after()
        }
    }

    private fun setListeners() {
        binding.searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchPhotos(binding.searchEdit.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                changeClearButtonVisibility(editable?.isNotEmpty() ?: false)
            }

            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.searchClearButton.setOnClickListener {
            binding.searchEdit.text?.clear()
            context?.showSoftInput(binding.searchEdit)
        }
    }

    private class SearchViewPagerAdapter(
            fragmentManager: FragmentManager
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return SingleSearchFragment.newInstance(SearchSource.getAt(position).photoSource)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return SearchSource.getAt(position).photoSource.title
        }

        override fun getCount(): Int {
            return SearchSource.size
        }

        fun searchPhotos(viewPager: ViewPager, text: String) {
            startUpdate(viewPager)

            for (i in 0 until count) {
                (instantiateItem(viewPager, i) as? SingleSearchFragment)?.apply {
                    if (isAdded) {
                        searchPhotos(text)
                    }
                }
            }

            finishUpdate(viewPager)
        }
    }
}