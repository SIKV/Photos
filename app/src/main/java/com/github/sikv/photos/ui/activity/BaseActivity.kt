package com.github.sikv.photos.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 105
    }

    private var doAfterWriteExternalStorageGranted: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.messageEvent.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun postMessage(@StringRes messageId: Int) {
        App.instance.postMessage(getString(messageId))
    }

    fun requestWriteExternalStoragePermission(doAfter: () -> Unit) {
        this.doAfterWriteExternalStorageGranted = doAfter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION)

        } else {
            doAfterWriteExternalStorageGranted?.invoke()
            doAfterWriteExternalStorageGranted = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doAfterWriteExternalStorageGranted?.invoke()
                doAfterWriteExternalStorageGranted = null
            } else {
                showPermissionNotGrantedDialog(R.string.grant_storage_permission_description)
            }
        }
    }

    private fun showPermissionNotGrantedDialog(@StringRes messageId: Int) {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_name)
                .setMessage(messageId)
                .setPositiveButton(R.string.open_settings) { _, _ ->
                    // TODO Implement
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
    }
}