package com.github.sikv.photos.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.github.sikv.photos.R
import com.github.sikv.photos.data.SearchSource
import com.github.sikv.photos.ui.fragment.SearchFragment
import com.github.sikv.photos.util.Utils
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    companion object {
        fun startActivity(activity: Activity) {
            val intent = Intent(activity, SearchActivity::class.java)

            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private var clearButtonVisible: Boolean = false
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        setSupportActionBar(searchToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initViewPager()
        init()

        searchRequestFocus()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_clear, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.itemClear)?.isVisible = clearButtonVisible
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemClear -> {
                searchEdit.text.clear()
                searchRequestFocus()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun searchPhotos(text: String) {
        viewPagerAdapter.fragments.forEach {
            it.searchPhotos(text)
        }
    }

    private fun searchRequestFocus() {
        searchEdit.requestFocus()
        Utils.showSoftInput(this, searchEdit)
    }

    private fun initViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(SearchFragment.newInstance(SearchSource.UNSPLASH), getString(R.string.unsplash))
        viewPagerAdapter.addFragment(SearchFragment.newInstance(SearchSource.PEXELS), getString(R.string.pexels))

        searchViewPager.adapter = viewPagerAdapter
        searchTabLayout.setupWithViewPager(searchViewPager)
    }

    private fun init() {
        searchEdit.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.hideSoftInput(this@SearchActivity, searchEdit)
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
    }

    internal inner class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        var fragments: MutableList<SearchFragment> = mutableListOf()
            private set

        private var titles: MutableList<String> = mutableListOf()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: SearchFragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}