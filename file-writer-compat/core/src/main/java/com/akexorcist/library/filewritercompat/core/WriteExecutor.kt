package com.akexorcist.library.filewritercompat.core

import androidx.fragment.app.FragmentActivity
import java.io.File

interface WriteExecutor<SUCCESS, ERROR> {
    suspend fun <DATA> write(activity: FragmentActivity, data: DATA, writer: suspend (DATA, File) -> Unit): FileResult<SUCCESS, ERROR>

    suspend fun write(activity: FragmentActivity, data: ByteArray): FileResult<SUCCESS, ERROR> {
        return write(activity, data, DefaultFileWriter.ByteArrayWriter)
    }

    suspend fun write(activity: FragmentActivity, data: String): FileResult<SUCCESS, ERROR> {
        return write(activity, data, DefaultFileWriter.StringWriter)
    }

    suspend fun write(activity: FragmentActivity, data: Any): FileResult<SUCCESS, ERROR> {
        return write(activity, data, DefaultFileWriter.AnyWriter)
    }
}
