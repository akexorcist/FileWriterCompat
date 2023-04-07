package com.akexorcist.library.filewritercompat.core.utility

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

internal object VersionHelper {

    internal fun isApi29OrLower() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    internal fun isAtLeastApi29() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    internal fun isAtLeastApi31() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}
