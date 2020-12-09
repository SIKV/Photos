package com.github.sikv.photos.enumeration

enum class PhotoSource(val id: Int, val title: String) {
    UNSPECIFIED(0, ""),
    PEXELS(1, "Pexels"),
    UNSPLASH(2, "Unsplash"),
    PIXABAY(3, "Pixabay");

    companion object {
        fun findById(id: Int): PhotoSource? {
            return values().firstOrNull { it.id == id }
        }
    }
}