package com.akexorcist.library.filewritercompat.core.manager

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.akexorcist.library.filewritercompat.core.WriteExecutor
import com.akexorcist.library.filewritercompat.core.FileResult
import com.akexorcist.library.filewritercompat.core.utility.FileHelper
import java.io.File

class ExternalAppSpecificFile {
    sealed class ErrorReason {
        @Suppress("unused")
        class InvalidFileNameWithExtension(
            val fileNameWithExtension: String
        ) : ErrorReason()

        @Suppress("unused")
        class InvalidDirectoryType(
            val directoryType: String?
        ) : ErrorReason()

        @Suppress("unused")
        class CannotCreateDirectory(
            val directory: String
        ) : ErrorReason()

        @Suppress("unused")
        class CannotWriteFile(
            val exception: Exception
        ) : ErrorReason()
    }

    class Builder(
        private var directoryType: String?,
        private var fileNameWithExtension: String,
        private var childPath: String?,
    ) {
        fun setDirectoryType(type: String?) = this.apply {
            this.directoryType = type
        }

        fun setChildPath(childPath: String) = this.apply {
            this.childPath = childPath
        }

        fun build(): WriteExecutor<Uri, ErrorReason> = Executor(
            directoryType = directoryType,
            childPath = childPath ?: "",
            fileNameWithExtension = fileNameWithExtension,
        )
    }

    class Executor(
        private val directoryType: String?,
        private val childPath: String,
        private val fileNameWithExtension: String,
    ) : WriteExecutor<Uri, ErrorReason> {
        override suspend fun <DATA> write(activity: FragmentActivity, data: DATA, writer: (DATA, File) -> Unit): FileResult<Uri, ErrorReason> {
            if (!FileHelper.isValidFileNameWithExtension(fileNameWithExtension)) {
                return FileResult.Error(ErrorReason.InvalidFileNameWithExtension(fileNameWithExtension))
            }
            if (!FileHelper.isValidDirectoryType(directoryType)) {
                return FileResult.Error(ErrorReason.InvalidDirectoryType(directoryType))
            }
            val directory = File(activity.getExternalFilesDir(directoryType), childPath)
            val isDirectoryExist = FileHelper.createMissingDirectory(directory)
            if (!isDirectoryExist) {
                return FileResult.Error(ErrorReason.CannotCreateDirectory(directory.absolutePath))
            }
            val file = File(directory, fileNameWithExtension)
            return try {
                writer(data, file)
                FileResult.Success(Uri.fromFile(file))
            } catch (e: Exception) {
                FileResult.Error(ErrorReason.CannotWriteFile(e))
            }
        }
    }
}
