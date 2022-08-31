package com.github.sikv.photos.manager

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*

class PermissionManager(
    private val fragment: Fragment
) : DefaultLifecycleObserver {

    private val key = UUID.randomUUID().toString()
    private lateinit var requestPermission: ActivityResultLauncher<String>

    private var onPermissionRequestResult: ((Boolean) -> Unit)? = null

    init {
        fragment.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        val activity = fragment.activity as AppCompatActivity

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
