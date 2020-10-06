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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ImageLabeler @Inject constructor(
        private val glide: RequestManager
) {

    suspend fun processImage(imageUrl: String): List<String> {
        return suspendCoroutine { continuation ->
            if (imageUrl.isBlank()) {
                continuation.resume(emptyList())
            }

            glide.asBitmap()
                    .load(imageUrl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            val image = FirebaseVisionImage.fromBitmap(bitmap)

                            FirebaseVision.getInstance()
                                    .onDeviceImageLabeler
                                    .processImage(image)
                                    .addOnSuccessListener { labels ->
                                        continuation.resume(labels.map { it.text })
                                    }.addOnFailureListener {
                                        continuation.resume(emptyList())
                                    }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            continuation.resume(emptyList())
                        }
                    })
        }
    }
}