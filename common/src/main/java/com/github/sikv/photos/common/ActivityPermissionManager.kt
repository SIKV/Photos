package com.github.sikv.photos.common

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.UUID

class ActivityPermissionManager(
    private val activity: FragmentActivity
) : DefaultLifecycleObserver {

    private val key = UUID.randomUUID().toString()
    private lateinit var requestPermission: ActivityResultLauncher<String>

    private var onPermissionRequestResult: ((Boolean) -> Unit)? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        requestPermission = activity.activityResultRegistry
            .register(key, ActivityResultContracts.RequestPermission()) { granted ->
                onPermissionRequestResult?.invoke(granted)
            }
    }

    fun requestPermission(permission: String, onPermissionRequestResult: (Boolean) -> Unit) {
        this.onPermissionRequestResult = onPermissionRequestResult
        requestPermission.launch(permission)
    }
}
