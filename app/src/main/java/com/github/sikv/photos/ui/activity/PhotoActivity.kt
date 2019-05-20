package com.github.sikv.photos.ui.activity

import android.annotation.TargetApi
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.text.style.ClickableSpan
import android.transition.ChangeBounds
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.fragment.SetWallpaperBottomSheetDialogFragment
import com.github.sikv.photos.util.CustomWallpaperManager
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.viewmodel.PhotoViewModel
import com.github.sikv.photos.viewmodel.PhotoViewModelFactory
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : BaseActivity(), SensorEventListener {

    companion object {
        private const val FAVORITE_ANIMATION_DURATION = 200L

        private const val EXTRA_PHOTO = "photo"

        fun startActivity(activity: Activity, transitionView: View, photo: Photo) {
            val intent = Intent(activity, PhotoActivity::class.java)
            intent.putExtra(EXTRA_PHOTO, photo)

            val transitionName = activity.getString(R.string.transition_photo)

            val options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity, transitionView, transitionName)

            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }

    private lateinit var viewModel: PhotoViewModel

    private lateinit var sensorManager: SensorManager
    private lateinit var gravitySensor: Sensor

    private var lastGravity0 = 0.0
    private var lastGravity1 = 0.0

    private var favoriteMenuItemIcon: Int? = null

    private val setWallpaperBottomSheet = SetWallpaperBottomSheetDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)
        setTransitionAnimationDuration()

        val photo: Photo = intent.getParcelableExtra(EXTRA_PHOTO)

        viewModel = ViewModelProviders.of(this, PhotoViewModelFactory(application, photo))
                .get(PhotoViewModel::class.java)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        init()

        observePhotoLoading()
        observeEvents()
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()

        hideViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        favoriteMenuItemIcon?.let {
            menu?.findItem(R.id.itemFavorite)?.setIcon(it)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.itemFavorite -> {
                viewModel.favorite()

                val itemView = findViewById<View>(R.id.itemFavorite)

                val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)

                scaleAnimation.duration = FAVORITE_ANIMATION_DURATION
                itemView.startAnimation(scaleAnimation)

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

    private fun init() {
        setSupportActionBar(photoToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        photoImageView.post {
            val extraOutOfScreen = 250

            photoImageView.layoutParams.width = photoImageView.measuredWidth + extraOutOfScreen
            photoImageView.layoutParams.height = photoImageView.measuredHeight + extraOutOfScreen
        }

        initListeners()

        adjustMargins()
    }

    private fun showPhoto(photo: Photo) {
        val authorName = photo.getPhotographerName()
        val source = photo.getSource()

        photoAuthorText.text = String.format(getString(R.string.photo_by_s_on_s), authorName, source)

        Utils.makeUnderlineBold(photoAuthorText, arrayOf(authorName, source))

        Utils.makeClickable(photoAuthorText, arrayOf(authorName, source),
                arrayOf(
                        object : ClickableSpan() {
                            override fun onClick(p0: View?) {
                                viewModel.openAuthorUrl()
                            }
                        },
                        object : ClickableSpan() {
                            override fun onClick(p0: View?) {
                                viewModel.openPhotoSource()
                            }
                        }
                ))
    }

    private fun initListeners() {
        photoSetWallpaperButton.setOnClickListener {
            setWallpaperBottomSheet.show(supportFragmentManager, setWallpaperBottomSheet.tag)
        }

        photoShareButton.setOnClickListener {
            startActivity(viewModel.createShareIntent())
        }

        photoDownloadButton.setOnClickListener {
        }

        setWallpaperBottomSheet.callback = object : SetWallpaperBottomSheetDialogFragment.Callback {
            override fun setHomeScreen() {
                setWallpaperBottomSheet.dismiss()
                viewModel.setWallpaper(this@PhotoActivity, CustomWallpaperManager.Which.HOME)
            }

            override fun setLockScreen() {
                setWallpaperBottomSheet.dismiss()
                viewModel.setWallpaper(this@PhotoActivity, CustomWallpaperManager.Which.LOCK)

            }

            override fun setHomeAndLockScreen() {
                setWallpaperBottomSheet.dismiss()
                viewModel.setWallpaper(this@PhotoActivity, CustomWallpaperManager.Which.BOTH)
            }
        }
    }

    private fun observePhotoLoading() {
        viewModel.loadPhoto(Glide.with(this)).observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                photoImageView.setImageBitmap(it)
            }
        })
    }

    private fun observeEvents() {
        viewModel.photoReadyEvent.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { photo ->
                showPhoto(photo)
            }
        })

        viewModel.favoriteChangedEvent.observe(this, Observer {
            favoriteMenuItemIcon = if (it?.getContentIfNotHandled() == true) {
                R.drawable.ic_favorite_white_24dp
            } else {
                R.drawable.ic_favorite_border_white_24dp
            }

            invalidateOptionsMenu()
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setTransitionAnimationDuration() {
        val duration = 220L

        val changeBounds = ChangeBounds()
        changeBounds.duration = duration

        window.sharedElementEnterTransition = changeBounds
    }

    private fun adjustMargins() {
        val photoAuthorTextLayoutParams = photoAuthorText.layoutParams

        if (photoAuthorTextLayoutParams is ViewGroup.MarginLayoutParams) {
            photoAuthorTextLayoutParams.bottomMargin += Utils.navigationBarHeight(this)
        }
    }

    private fun hideViews() {
        photoToolbar.visibility = View.INVISIBLE
        photoAuthorText.visibility = View.INVISIBLE
        photoSetWallpaperButton.visibility = View.INVISIBLE
        photoShareButton.visibility = View.INVISIBLE
        photoDownloadButton.visibility = View.INVISIBLE
    }
}