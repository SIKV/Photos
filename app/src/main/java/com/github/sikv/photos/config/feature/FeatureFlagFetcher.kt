package com.github.sikv.photos.config.feature

import androidx.appcompat.app.AppCompatActivity
import com.github.sikv.photos.service.PreferencesService
import com.github.sikv.photos.ui.dialog.FullScreenLoadingDialog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlagFetcher @Inject constructor(
    private val repository: FeatureFlagRepository,
    private val preferencesService: PreferencesService
) {
    fun fetch(activity: AppCompatActivity, doAfter: () -> Unit) {
        if (!preferencesService.isFeatureFlagsFetched()) {
            val dialog = FullScreenLoadingDialog()
            dialog.show(activity.supportFragmentManager, null)

            repository.fetch {
                dialog.dismiss()
                doAfter()
                preferencesService.setFeatureFlagsFetched()
            }
        } else {
            doAfter()
            repository.refresh()
        }
    }
}
