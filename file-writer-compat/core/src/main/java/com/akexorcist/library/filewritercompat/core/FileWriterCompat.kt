package com.akexorcist.library.filewritercompat.core

import com.akexorcist.library.filewritercompat.core.permission.NoOperationStoragePermissionRequest
import com.akexorcist.library.filewritercompat.core.manager.*

object FileWriterCompat {

    object Builder {
        fun createInternalAppSpecificFile(
            fileNameWithExtension: String,
        ) = InternalAppSpecificFile.Builder(
            childPath = null,
            fileNameWithExtension = fileNameWithExtension,
        )

        fun createInternalAppSpecificCache(
            fileNameWithExtension: String,
        ) = InternalAppSpecificCache.Builder(
            childPath = null,
            fileNameWithExtension = fileNameWithExtension,
        )

        fun createExternalAppSpecificFile(
            fileNameWithExtension: String,
        ) = ExternalAppSpecificFile.Builder(
            directoryType = null,
            childPath = null,
            fileNameWithExtension = fileNameWithExtension,
        )

        fun createExternalAppSpecificCache(
            fileNameWithExtension: String,
        ) = ExternalAppSpecificCache.Builder(
            childPath = null,
            fileNameWithExtension = fileNameWithExtension,
        )

        fun createExternalShareableFile(
            directoryType: String,
            fileNameWithExtension: String,
        ) = ExternalShareableFile.Builder(
            directoryType = directoryType,
            childPath = null,
            fileNameWithExtension = fileNameWithExtension,
            storagePermissionRequest = NoOperationStoragePermissionRequest,
        )
    }
}
