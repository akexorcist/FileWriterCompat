package com.akexorcist.library.filewritercompat.core.manager

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.akexorcist.library.filewritercompat.core.FileWriter
import com.akexorcist.library.filewritercompat.core.FileResult
import com.akexorcist.library.filewritercompat.core.utility.FileHelper
import java.io.File

class InternalAppSpecificFile {
    sealed class ErrorReason {

        @Suppress("unused")
        class InvalidFileNameWithExtension(
            val fileNameWithExtension: String
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
        private var fileNameWithExtension: String,
        private var childPath: String?,
    ) {
        fun setChildPath(childPath: String) = this.apply {
            this.childPath = childPath
        }

        fun build(): FileWriter<Uri, ErrorReason> = Writer(
            childPath = childPath ?: "",
            fileNameWithExtension = fileNameWithExtension,
        )
    }

    class Writer(
        private var fileNameWithExtension: String,
        private var childPath: String,
    ) : FileWriter<Uri, ErrorReason> {
        override suspend fun write(activity: FragmentActivity, data: ByteArray): FileResult<Uri, ErrorReason> {
            if (!FileHelper.isValidFileNameWithExtension(fileNameWithExtension)) {
                return FileResult.Error(ErrorReason.InvalidFileNameWithExtension(fileNameWithExtension))
            }
            val directory = File(activity.filesDir, childPath)
            val isDirectoryExist = FileHelper.createMissingDirectory(directory)
            if (!isDirectoryExist) {
                return FileResult.Error(ErrorReason.CannotCreateDirectory(directory.absolutePath))
            }
            val file = File(directory, fileNameWithExtension)
            return try {
                FileHelper.writeFile(data, file)
                FileResult.Success(Uri.fromFile(file))
            } catch (e: Exception) {
                FileResult.Error(ErrorReason.CannotWriteFile(e))
            }
        }
    }
}
