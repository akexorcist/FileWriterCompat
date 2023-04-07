package com.akexorcist.library.filewritercompat.core.permission

import androidx.fragment.app.FragmentActivity
import com.akexorcist.library.filewritercompat.core.utility.PermissionHelper

interface StoragePermissionRequest {
    // This will be invoked on Android 10 or lower
    suspend fun requestWriteExternalStoragePermission(activity: FragmentActivity): Boolean
}

object NoOperationStoragePermissionRequest: StoragePermissionRequest {
    override suspend fun requestWriteExternalStoragePermission(activity: FragmentActivity): Boolean {
        return PermissionHelper.isWriteExternalStoragePermissionGranted(activity)
    }
}
