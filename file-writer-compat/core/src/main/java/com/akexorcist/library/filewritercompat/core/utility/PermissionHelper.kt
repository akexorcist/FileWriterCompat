package com.akexorcist.library.filewritercompat.core.utility

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

internal object PermissionHelper {

    internal fun isWriteExternalStoragePermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
}