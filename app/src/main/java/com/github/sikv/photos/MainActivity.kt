package com.github.sikv.photos

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.github.sikv.photos.adapter.PhotoAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mainRecycler.adapter = PhotoAdapter()

        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                recyclerView?.computeVerticalScrollOffset()?.let {
                    if (it > 0) {
                        ViewCompat.setElevation(mainAppBarLayout, 10.0f)
                    } else {
                        ViewCompat.setElevation(mainAppBarLayout, 0.0f)
                    }
                }
            }
        })
    }
}
