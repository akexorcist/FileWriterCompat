package com.akexorcist.library.filewritercompat.core

import android.net.Uri
import androidx.fragment.app.FragmentActivity

interface WriteExecutor<SUCCESS : Uri, ERROR> {
    suspend fun write(activity: FragmentActivity, data: ByteArray): FileResult<Uri, ERROR>
}

