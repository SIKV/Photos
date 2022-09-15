package com.github.sikv.photos.recommendations.service

import com.github.sikv.photos.common.PhotoLoader
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageLabelerService @Inject constructor(
    private val photoLoader: PhotoLoader
) {
    suspend fun processImage(imageUrl: String): List<String> {
        val photo = photoLoader.load(imageUrl) ?: return emptyList()

        return suspendCoroutine { c ->
            val image = InputImage.fromBitmap(photo, 0)

            ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener { labels ->
                    c.resume(labels.map { it.text })
                }.addOnFailureListener {
                    c.resume(emptyList())
                }
        }
    }
}
