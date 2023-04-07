package com.akexorcist.filewritercompat

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.io.ByteArrayOutputStream

object BitmapHelper {
    private const val DEFAULT_TEXT_SIZE = 32f
    private const val DEFAULT_MARGIN_END = 20f
    private const val DEFAULT_MARGIN_BOTTOM = 2f

    fun addTimestamp(bitmap: Bitmap, timestamp: String) {
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = DEFAULT_TEXT_SIZE
        }
        val measuredTextWidth = paint.measureText(timestamp)
        val x = canvas.width - (measuredTextWidth + DEFAULT_MARGIN_END)
        val y = canvas.height - (DEFAULT_TEXT_SIZE + DEFAULT_MARGIN_BOTTOM)
        canvas.drawText(timestamp, x, y, paint)
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}
