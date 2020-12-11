package com.github.sikv.photos.ui.activity

import android.animation.LayoutTransition
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.text.style.ClickableSpan
import android.transition.ChangeBounds
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.favoriteAnimation
import com.github.sikv.photos.util.makeClickable
import com.github.sikv.photos.util.makeUnderlineBold
import com.github.sikv.photos.viewmodel.PhotoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_photo.*

@AndroidEntryPoint
class PhotoActivity : BaseActivity(), SensorEventListener {

    companion object {
        private const val TRANSITION_ANIMATION_DURATION = 250L

        fun startActivity(activity: Activity?, transitionView: View, photo: Photo) {
            activity?.let {
                val intent = Intent(activity, PhotoActivity::class.java).apply {
                    putExtra(Photo.KEY, photo)
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    val transitionName = activity.getString(R.string._transition_photo)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, transitionName)

                    ActivityCompat.startActivity(activity, intent, options.toBundle())
                } else {
                    activity.startActivity(intent)
                }
            }
        }
    }

    private val viewModel: PhotoViewModel by viewModels()

    // Parallax Effect
    private var sensorManager: SensorManager? = null
    private var gravitySensor: Sensor? = null

    private var lastGravity0 = 0.0
    private var lastGravity1 = 0.0

    private var favoriteMenuItemIcon: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)

        tweakTransitions()

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

        setListeners()
        setOnApplyWindowInsetsListeners()

        observe()
        observeGlobalMessageEvent()
    }

    override fun onResume() {
        super.onResume()

//        startParallax()
    }

    override fun onPause() {
        super.onPause()

//        stopParallax()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_photo, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        favoriteMenuItemIcon?.let {
            menu?.findItem(R.id.itemFavorite)?.setIcon(it)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemFavorite -> {
                viewModel.invertFavorite()
                findViewById<View>(R.id.itemFavorite).favoriteAnimation()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
            Utils.calculateP(
                    event.values[0].toDouble(), event.values[1].toDouble(), event.values[2].toDouble(),
                    photoImageView.x, photoImageView.y, photoImageView.z,

                    lastGravity0, lastGravity1)?.let { r ->

                photoImageView.x = r.first
                photoImageView.y = r.second

                lastGravity0 = r.third.first
                lastGravity1 = r.third.second
            }
        }
    }

    private fun showPhotoInfo(photo: Photo) {
        val authorName = photo.getPhotoPhotographerName()
        val source = photo.getPhotoSource().title

        authorText.text = String.format(getString(R.string.photo_by_s_on_s), authorName, source)

        authorText.makeUnderlineBold(arrayOf(authorName, source))

        authorText.makeClickable(arrayOf(authorName, source),
                arrayOf(
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                viewModel.openAuthorUrl()
                            }
                        },
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                viewModel.openPhotoSource()
                            }
                        }
                ))
    }

    private fun updateFavoriteMenuItemIcon(favorite: Boolean) {
        favoriteMenuItemIcon = if (favorite) {
            R.drawable.ic_favorite_red_24dp
        } else {
            R.drawable.ic_favorite_border_white_24dp
        }

        invalidateOptionsMenu()
    }

    private fun observe() {
        viewModel.showPhotoInfoEvent.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { photo ->
                showPhotoInfo(photo)
            }
        })

        viewModel.showPhotoEvent.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { photo ->
                photoImageView.setImageBitmap(photo)
            }
        })

        viewModel.favoriteInitEvent.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { favorite ->
                updateFavoriteMenuItemIcon(favorite)
            }
        })

        viewModel.favoriteChangedLiveData.observe(this, Observer {
            updateFavoriteMenuItemIcon(it)
        })
    }

    private fun setListeners() {
        setWallpaperButton.setOnClickListener {
            viewModel.setWallpaper(supportFragmentManager)
        }

        downloadButton.setOnClickListener {
            requestWriteExternalStoragePermission {
                viewModel.downloadPhotoAndSave()
            }
        }

        shareButton.setOnClickListener {
            startActivity(viewModel.createShareIntent())
        }
    }

    private fun setOnApplyWindowInsetsListeners() {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            view.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(contentLayout) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun startParallax() {
        sensorManager?.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun stopParallax() {
        sensorManager?.unregisterListener(this)
    }

    private fun observeGlobalMessageEvent() {
        App.instance.globalMessageEvent.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(rootLayout, message, Snackbar.LENGTH_SHORT)
                        .show()
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun tweakTransitions() {
        val changeBounds = ChangeBounds()
        changeBounds.duration = TRANSITION_ANIMATION_DURATION

        window.sharedElementEnterTransition = changeBounds

        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition?) {
                toolbar.visibility = View.INVISIBLE
                contentLayout.visibility = View.INVISIBLE
                overlayView.visibility = View.INVISIBLE
            }

            override fun onTransitionEnd(transition: Transition?) {
                rootLayout.layoutTransition = LayoutTransition()

                toolbar.visibility = View.VISIBLE
                contentLayout.visibility = View.VISIBLE
                overlayView.visibility = View.VISIBLE
            }

            override fun onTransitionResume(transition: Transition?) { }

            override fun onTransitionPause(transition: Transition?) { }

            override fun onTransitionCancel(transition: Transition?) { }
        })
    }
}