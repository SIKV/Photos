package com.github.sikv.photos.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragment: Fragment,
    private val itemCount: Int,
    private val onCreateFragment: (Int) -> Fragment
) : FragmentStateAdapter(fragment) {

    private val fragments = hashMapOf<Int, Fragment>()

    override fun getItemCount(): Int {
        return itemCount
    }

    override fun createFragment(position: Int): Fragment {
        return onCreateFragment(position).also { fragment ->
            fragments[position] = fragment
        }
    }

    fun getFragment(position: Int): Fragment? {
        return fragments[position]
    }
}
