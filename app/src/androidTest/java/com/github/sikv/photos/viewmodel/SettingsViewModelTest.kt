package com.github.sikv.photos.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sikv.photos.BuildConfig
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        viewModel = SettingsViewModel()
    }

    @Test
    fun init_triggersShowAppVersionEvent() {
        viewModel.showAppVersionEvent.observeForever { }
        val value = viewModel.showAppVersionEvent.value?.getContentIfNotHandled()

        Assert.assertFalse(value.isNullOrBlank())
    }

    @Test
    fun showAppVersionEvent_returnsCorrectValue() {
        viewModel.showAppVersionEvent.observeForever { }
        val value = viewModel.showAppVersionEvent.value?.getContentIfNotHandled()

        Assert.assertEquals(BuildConfig.VERSION_NAME, value)
    }
}