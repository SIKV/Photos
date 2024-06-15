package com.github.sikv.photos.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.sikv.photos.common.ui.*
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.args.SingleSearchFragmentArguments
import com.github.sikv.photos.navigation.args.fragmentArguments
import com.github.sikv.photos.navigation.args.withArguments
import com.github.sikv.photos.search.SearchViewModel
import com.github.sikv.photos.search.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    override val overrideBackground: Boolean = true

    @Inject
    lateinit var configProvider: ConfigProvider

    private val viewModel: SearchViewModel by activityViewModels()
    private val args by fragmentArguments<SearchFragmentArguments>()

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBackButton(
            title = null,
            navigationOnClickListener = {
                findNavController().popBackStack()
            }
        )

        setupViewPager {
            if (savedInstanceState == null) {
                args.query?.let { query ->
                    binding.searchEdit.append(query)
                    performSearch(query)
                }

                shownKeyboardIfNeeded()
            }
        }

        setListeners()
        changeClearButtonVisibility(false, withAnimation = false)

        // TODO: Fix this.
//        navigation?.setOnBackPressedListener(object :
//            com.github.sikv.photos.navigation.OnBackPressedListener {
//            override fun onBackPressed() {
//                viewModel.clearSearch()
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performSearch(text: String) {
        context?.hideSoftInput(binding.searchEdit)
        viewModel.requestSearch(text)
    }

    private fun shownKeyboardIfNeeded() {
        val showKeyboard = args.query == null
        var keyboardShown = false

        if (showKeyboard) {
            if (!keyboardShown) {
                keyboardShown = requireActivity().showSoftInput(binding.searchEdit)
            }

            // TODO Fix crash.
//            binding.searchEdit.viewTreeObserver.addOnWindowFocusChangeListener(object :
//                ViewTreeObserver.OnWindowFocusChangeListener {
//                override fun onWindowFocusChanged(hasFocus: Boolean) {
//                    if (hasFocus && !keyboardShown) {
//                        keyboardShown = requireActivity().showSoftInput(binding.searchEdit)
//                    }
//
//                    binding.searchEdit.viewTreeObserver?.removeOnWindowFocusChangeListener(this)
//                }
//            })
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

    private fun setupViewPager(after: () -> Unit) {
        val searchSources = configProvider.getSearchSources().toList()

        if (searchSources.isNotEmpty()) {
            viewPagerAdapter = ViewPagerAdapter(this, searchSources.size) { position ->
                SingleSearchFragment()
                    .withArguments(SingleSearchFragmentArguments(searchSources[position]))
            }

            binding.viewPager.adapter = viewPagerAdapter
            binding.viewPager.offscreenPageLimit = searchSources.size

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = searchSources[position].title
            }.attach()
        }

        binding.viewPager.post {
            after()
        }
    }

    private fun setListeners() {
        binding.searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.searchEdit.text.toString())
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
}
