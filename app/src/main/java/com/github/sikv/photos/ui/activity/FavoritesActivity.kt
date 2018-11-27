package com.github.sikv.photos.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.sikv.photos.R
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity() {

    private val viewModel: FavoritesViewModel by lazy {
        ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
    }

    companion object {

        fun startActivity(activity: Activity) {
            val intent = Intent(activity, FavoritesActivity::class.java)

            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_favorites)

        setSupportActionBar(favoritesToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.favoritesLiveData.observe(this, Observer {

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}