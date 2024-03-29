package com.github.sikv.photos.domain

enum class PhotoSource(
    val id: Int,
    val title: String,
    val url: String
) {
    UNSPECIFIED(0, "", ""),
    PEXELS(1, "Pexels", "pexels.com"),
    UNSPLASH(2, "Unsplash", "unsplash.com"),
    PIXABAY(3, "Pixabay", "pixabay.com");
}
