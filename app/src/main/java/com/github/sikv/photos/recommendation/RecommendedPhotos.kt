package com.github.sikv.photos.recommendation

import com.github.sikv.photos.model.Photo

data class RecommendedPhotos(
        val photos: List<Photo>,
        val moreAvailable: Boolean,
        val reset: Boolean
)