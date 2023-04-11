package com.akexorcist.library.filewritercompat.core

import com.akexorcist.library.filewritercompat.core.utility.FileHelper
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

object DefaultFileWriter {
    val ByteArrayWriter = { data: ByteArray, file: File ->
        FileHelper.writeFile(data, file)
    }

    val StringWriter = { data: String, file: File ->
        if (!file.exists()) {
            file.createNewFile()
        }
        val fos = FileOutputStream(file)
        ObjectOutputStream(fos).use {
            it.writeBytes(data)
        }
    }

    val AnyWriter = { data: Any, file: File ->
        if (!file.exists()) {
            file.createNewFile()
        }
        val fos = FileOutputStream(file)
        ObjectOutputStream(fos).use {
            it.writeObject(data)
        }
    }
}
