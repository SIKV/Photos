package com.github.sikv.photos.ui.activity

import android.animation.LayoutTransition
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
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
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.enumeration.SetWallpaperState
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.ViewUtils
import com.github.sikv.photos.viewmodel.PhotoViewModel
import com.github.sikv.photos.viewmodel.PhotoViewModelFactory
import kotlinx.android.synthetic.main.activity_photo.*

class PhotoActivity : BaseActivity(), SensorEventListener {

    companion object {
        private const val KEY_PHOTO = "key_photo"

        fun startActivity(activity: Activity?, transitionView: View, photo: Photo) {
            activity?.let {
                val intent = Intent(activity, PhotoActivity::class.java)
                intent.putExtra(KEY_PHOTO, photo)

                val transitionName = activity.getString(R.string.transition_photo)

                val options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(activity, transitionView, transitionName)

                ActivityCompat.startActivity(activity, intent, options.toBundle())
            }
        }
    }

    private val viewModel: PhotoViewModel by lazy {
        val photo: Photo = intent.getParcelableExtra(KEY_PHOTO)

        val viewModelFactory = PhotoViewModelFactory(application, photo)

        ViewModelProvider(this, viewModelFactory).get(PhotoViewModel::class.java)
    }

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

        init()
        setListeners()
        adjustMargins()

        observe()
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
//        stopParallax()

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
                ViewUtils.favoriteAnimation(findViewById<View>(R.id.itemFavorite))
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
        val source = photo.getPhotoSource()

        authorText.text = String.format(getString(R.string.photo_by_s_on_s), authorName, source)

        Utils.makeUnderlineBold(authorText, arrayOf(authorName, source))

        Utils.makeClickable(authorText, arrayOf(authorName, source),
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

        viewModel.downloadPhotoInProgressLiveData.observe(this, Observer { downloading ->
            setWallpaperButton.visibility = if (downloading) View.GONE else View.VISIBLE
        })

        viewModel.downloadPhotoStateChangedLiveData.observe(this, Observer { state ->
            when (state) {
                DownloadPhotoState.PHOTO_READY -> {
                    viewModel.setWallpaper()
                }

                else -> { }
            }
        })

        viewModel.setWallpaperStateChangedEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { state ->
                when (state) {
                    SetWallpaperState.FAILURE -> {
                        postMessage(R.string.error_setting_wallpaper)
                    }

                    else -> { }
                }
            }
        })
    }

    private fun init() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        photoImageView.post {
            val extraOutOfScreen = 250

            photoImageView.layoutParams.width = photoImageView.measuredWidth + extraOutOfScreen
            photoImageView.layoutParams.height = photoImageView.measuredHeight + extraOutOfScreen
        }
    }

    private fun setListeners() {
        setWallpaperButton.setOnClickListener {
            viewModel.setWallpaper(this@PhotoActivity)
        }

        shareButton.setOnClickListener {
            startActivity(viewModel.createShareIntent())
        }
    }

    private fun startParallax() {
        sensorManager?.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun stopParallax() {
        sensorManager?.unregisterListener(this)
    }

    private fun adjustMargins() {
        val photoAuthorTextLayoutParams = authorText.layoutParams

        if (photoAuthorTextLayoutParams is ViewGroup.MarginLayoutParams) {
            photoAuthorTextLayoutParams.bottomMargin += Utils.navigationBarHeight(this)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun tweakTransitions() {
        val duration = 220L

        val changeBounds = ChangeBounds()
        changeBounds.duration = duration

        window.sharedElementEnterTransition = changeBounds

        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition?) { }

            override fun onTransitionEnd(transition: Transition?) {
                rootLayout.layoutTransition = LayoutTransition()
            }

            override fun onTransitionResume(transition: Transition?) { }

            override fun onTransitionPause(transition: Transition?) { }

            override fun onTransitionCancel(transition: Transition?) { }
        })
    }
}