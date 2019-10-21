package com.github.sikv.photos.di.component

import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.di.module.ApiClientModule
import com.github.sikv.photos.di.module.RetrofitModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, ApiClientModule::class])
interface ApiClientComponent {

    fun inject(apiClient: ApiClient)
}