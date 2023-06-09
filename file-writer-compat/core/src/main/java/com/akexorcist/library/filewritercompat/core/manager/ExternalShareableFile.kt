package com.akexorcist.library.filewritercompat.core.manager

import android.net.Uri
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import com.akexorcist.library.filewritercompat.core.WriteExecutor
import com.akexorcist.library.filewritercompat.core.permission.StoragePermissionRequest
import com.akexorcist.library.filewritercompat.core.FileResult
import com.akexorcist.library.filewritercompat.core.utility.FileHelper
import com.akexorcist.library.filewritercompat.core.utility.PermissionHelper
import com.akexorcist.library.filewritercompat.core.utility.VersionHelper
import java.io.File

class ExternalShareableFile {
    sealed class ErrorReason {
        @Suppress("unused")
        class InvalidFileNameWithExtension(
            val fileNameWithExtension: String
        ) : ErrorReason()

        @Suppress("unused")
        class InvalidDirectoryType(
            val directoryType: String
        ) : ErrorReason()

        @Suppress("unused")
        class CannotCreateDirectory(
            val directory: String
        ) : ErrorReason()

        object WriteExternalStoragePermissionDenied : ErrorReason()

        @Suppress("unused")
        class CannotWriteFile(
            val exception: Exception
        ) : ErrorReason()

        @Suppress("unused")
        class CannotScanFile(
            val filePath: String
        ) : ErrorReason()
    }

    class Builder(
        private var directoryType: String,
        private var fileNameWithExtension: String,
        private var storagePermissionRequest: StoragePermissionRequest,
        private var childPath: String?,
    ) {
        fun setChildPath(childPath: String) = this.apply {
            this.childPath = childPath
        }

        fun setStoragePermissionRequest(storagePermissionRequest: StoragePermissionRequest) = this.apply {
            this.storagePermissionRequest = storagePermissionRequest
        }

        fun build(): WriteExecutor<Uri, ErrorReason> = Executor(
            directoryType = directoryType,
            childPath = childPath ?: "",
            fileNameWithExtension = fileNameWithExtension,
            storagePermissionRequest = storagePermissionRequest,
        )
    }

    class Executor(
        private val directoryType: String,
        private val childPath: String,
        private val fileNameWithExtension: String,
        private val storagePermissionRequest: StoragePermissionRequest,
    ) : WriteExecutor<Uri, ErrorReason> {
        override suspend fun <DATA> write(activity: FragmentActivity, data: DATA, writer: suspend (DATA, File) -> Unit): FileResult<Uri, ErrorReason> {
            if (!FileHelper.isValidFileNameWithExtension(fileNameWithExtension)) {
                return FileResult.Error(ErrorReason.InvalidFileNameWithExtension(fileNameWithExtension))
            }
            if (!FileHelper.isValidDirectoryType(directoryType)) {
                return FileResult.Error(ErrorReason.InvalidDirectoryType(directoryType))
            }
            if (VersionHelper.isAtLeastApi23() &&
                VersionHelper.isApi29OrLower() &&
                PermissionHelper.isWriteExternalStoragePermissionDenied(activity)
            ) {
                storagePermissionRequest.requestWriteExternalStoragePermission(activity)
                if (PermissionHelper.isWriteExternalStoragePermissionDenied(activity))
                    return FileResult.Error(ErrorReason.WriteExternalStoragePermissionDenied)
            }
            val directory = File(Environment.getExternalStoragePublicDirectory(directoryType), childPath)
            val isDirectoryExist = FileHelper.createMissingDirectory(directory)
            if (!isDirectoryExist) {
                return FileResult.Error(ErrorReason.CannotCreateDirectory(directory.absolutePath))
            }
            val file = File(directory, fileNameWithExtension)
            try {
                writer(data, file)
            } catch (e: Exception) {
                return FileResult.Error(ErrorReason.CannotWriteFile(e))
            }
            val uri = try {
                FileHelper.scanFile(activity, file)
            } catch (e: Exception) {
                return FileResult.Error(ErrorReason.CannotScanFile(file.absolutePath))
            }
            return FileResult.Success(uri)
        }
    }
}
