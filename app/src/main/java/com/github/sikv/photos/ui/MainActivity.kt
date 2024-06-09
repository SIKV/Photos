package com.github.sikv.photos.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.github.sikv.photos.FeatureFlagFetcher
import com.github.sikv.photos.R
import com.github.sikv.photos.databinding.ActivityMainBinding
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.route.SearchRoute
import com.github.sikv.photos.util.reportShortcutUsed
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var featureFlagFetcher: FeatureFlagFetcher

    @Inject
    lateinit var searchRoute: SearchRoute

    private lateinit var binding: ActivityMainBinding

    private val destinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        handleBottomNavigationVisibility(destination)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        featureFlagFetcher.fetch(this) {
            findNavController().addOnDestinationChangedListener(destinationChangedListener)
            binding.bottomNavigationView.setupWithNavController(findNavController())

            // This solves the follow issue:
            // https://stackoverflow.com/questions/71089052/android-navigation-component-bottomnavigationviews-selected-tab-icon-is-not-u
            // Always show selected Bottom Navigation item as selected (return true).
            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                // In order to get the expected behavior, you have to call default Navigation method manually.
                NavigationUI.onNavDestinationSelected(item, findNavController())
                return@setOnItemSelectedListener true
            }

            handleShortcuts()
        }
    }

    override fun onDestroy() {
        findNavController().removeOnDestinationChangedListener(destinationChangedListener)
        super.onDestroy()
    }

    private fun handleShortcuts() {
        when (intent.action) {
            getString(R.string._shortcut_action_search) -> {
                val searchDashboardItem = binding.bottomNavigationView.menu.findItem(R.id.searchDashboard)
                NavigationUI.onNavDestinationSelected(searchDashboardItem, findNavController())

                searchRoute.present(findNavController(), SearchFragmentArguments())

                getString(R.string._shortcut_search).reportShortcutUsed(this)
            }
        }
    }

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        return  navHostFragment.navController
    }

    private fun handleBottomNavigationVisibility(destination: NavDestination) {
        // Hide bottom navigation if Photo Details is opened.
        binding.bottomNavigationView.isVisible = destination.id != R.id.photoDetails
    }
}
