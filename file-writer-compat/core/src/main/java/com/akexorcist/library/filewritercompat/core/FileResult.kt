package com.akexorcist.library.filewritercompat.core

sealed class FileResult<out SUCCESS, out ERROR> {
    data class Success<SUCCESS>(
        val result: SUCCESS
    ) : FileResult<SUCCESS, Nothing>()

    data class Error<ERROR>(
        val reason: ERROR
    ) : FileResult<Nothing, ERROR>()
}
