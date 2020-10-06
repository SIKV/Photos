package com.github.sikv.photos.vision

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bumptech.glide.Glide
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageLabelerTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var imageLabeler: ImageLabeler

    @Before
    fun setup() {
        val glide = Glide.with(InstrumentationRegistry.getInstrumentation().targetContext)
        imageLabeler = ImageLabeler(glide)
    }

    @Test
    fun processImage_emptyImageUrl_returnsEmptyList() {
        val result = runBlocking { imageLabeler.processImage("") }

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun processImage_blankImageUrl_returnsEmptyList() {
        val result = runBlocking { imageLabeler.processImage(" ") }

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun processImage_realImageUrl_returnsNotEmptyList() {
        val imageUrl = "https://avatars1.githubusercontent.com/u/11236380?s=460&u=929986d53f30f7c7aa3d624037298404d86a3b3a&v=4"
        val result = runBlocking { imageLabeler.processImage(imageUrl) }

        Assert.assertTrue(result.isNotEmpty())
    }
}