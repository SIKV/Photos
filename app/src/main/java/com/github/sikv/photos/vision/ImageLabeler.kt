package com.github.sikv.photos.vision

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageLabeler @Inject constructor(
        private val glide: RequestManager
) {

    fun processImage(imageUrl: String, completion: (List<String>) -> Unit) {
        glide.asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        val image = FirebaseVisionImage.fromBitmap(bitmap)

                        FirebaseVision.getInstance()
                                .onDeviceImageLabeler
                                .processImage(image)
                                .addOnSuccessListener { labels ->
                                    completion(labels.map { it.text })
                                }.addOnFailureListener {
                                    completion(emptyList())
                                }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        completion(emptyList())
                    }
                })
    }
}