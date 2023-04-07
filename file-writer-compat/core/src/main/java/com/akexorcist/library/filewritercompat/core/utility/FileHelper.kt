package com.akexorcist.library.filewritercompat.core.utility

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

internal object FileHelper {

    internal fun createMissingDirectory(directory: File): Boolean {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory.exists()
    }

    internal fun isValidFileNameWithExtension(nameWithExtension: String) = nameWithExtension.matches("^[\\w,\\s-]+\\.[A-Za-z]+\$".toRegex())

    internal fun isValidDirectoryType(directoryType: String) = mutableListOf(
        Environment.DIRECTORY_MUSIC,
        Environment.DIRECTORY_PODCASTS,
        Environment.DIRECTORY_RINGTONES,
        Environment.DIRECTORY_ALARMS,
        Environment.DIRECTORY_NOTIFICATIONS,
        Environment.DIRECTORY_PICTURES,
        Environment.DIRECTORY_MOVIES,
        Environment.DIRECTORY_DOWNLOADS,
        Environment.DIRECTORY_DCIM,
        Environment.DIRECTORY_DOCUMENTS,
    ).apply {
        if (VersionHelper.isAtLeastApi29()) {
            add(Environment.DIRECTORY_AUDIOBOOKS)
        }
        if (VersionHelper.isAtLeastApi31()) {
            add(Environment.DIRECTORY_RECORDINGS)
        }
    }.contains(directoryType)

    internal fun writeFile(data: ByteArray, output: File) {
        if (!output.exists()) {
            output.createNewFile()
        }
        FileOutputStream(output).use {
            it.write(data)
        }
    }

    internal suspend fun scanFile(context: Context, file: File): Uri {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.toString()),
                arrayOf(mimeType)
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $file could not be scanned"))
                } else {
                    continuation.resume(scannedUri)
                }
            }
        }
    }
}
