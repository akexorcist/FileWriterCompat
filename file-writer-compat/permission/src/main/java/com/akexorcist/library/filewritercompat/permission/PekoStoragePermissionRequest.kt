package com.akexorcist.library.filewritercompat.permission

import android.Manifest
import androidx.fragment.app.FragmentActivity
import com.akexorcist.library.filewritercompat.core.permission.StoragePermissionRequest
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.allGranted


object PekoStoragePermissionRequest : StoragePermissionRequest {

    override suspend fun requestWriteExternalStoragePermission(activity: FragmentActivity): Boolean {
        PermissionRequester.initialize(activity.applicationContext)
        return PermissionRequester.instance().request(Manifest.permission.WRITE_EXTERNAL_STORAGE).allGranted()
    }
}
