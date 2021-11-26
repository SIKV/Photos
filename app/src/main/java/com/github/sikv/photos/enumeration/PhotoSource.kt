package com.github.sikv.photos.enumeration

enum class PhotoSource(
    val id: Int,
    val title: String,
    val url: String
) {
    UNSPECIFIED(0, "", ""),
    PEXELS(1, "Pexels", "pexels.com"),
    UNSPLASH(2, "Unsplash", "unsplash.com"),
    PIXABAY(3, "Pixabay", "pixabay.com");

    companion object {
        fun findById(id: Int): PhotoSource? {
            return values().firstOrNull { it.id == id }
        }
    }
}
