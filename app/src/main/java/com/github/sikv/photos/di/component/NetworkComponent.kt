package com.github.sikv.photos.di.component

import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.di.module.ApiModule
import com.github.sikv.photos.di.module.RetrofitModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, ApiModule::class])
interface NetworkComponent {

    @Component.Factory
    interface Factory {
        fun create(): NetworkComponent
    }

    fun inject(apiClient: ApiClient)
}