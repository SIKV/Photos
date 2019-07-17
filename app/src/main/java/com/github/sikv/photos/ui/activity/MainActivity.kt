package com.github.sikv.photos.ui.activity

import android.os.Bundle
import com.github.sikv.photos.R
import com.github.sikv.photos.util.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navGraphIds = listOf(
                R.navigation.photos,
                R.navigation.search,
                R.navigation.favorites,
                R.navigation.more
        )

        mainBottomNavigation.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.mainNavigationHostContainer,
                intent = intent)
    }
}