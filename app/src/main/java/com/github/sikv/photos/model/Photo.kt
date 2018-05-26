package com.github.sikv.photos.model

data class Photo(
        val id: String,
        val width: Int,
        val height: Int,
        val color: String,
        val likes: Int,
        val description: String,
        val user: User,
        val urls: Urls,
        val links: Links
)