package com.github.sikv.photos.config

interface ConfigProvider {
    fun fetch(doAfter: () -> Unit)
    fun refresh()
    fun getConfig(config: Config): Boolean
}