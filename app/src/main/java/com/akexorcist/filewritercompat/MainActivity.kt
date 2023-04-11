package com.akexorcist.filewritercompat

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.akexorcist.filewritercompat.databinding.ActivityMainBinding
import com.akexorcist.library.filewritercompat.core.FileWriterCompat
import com.akexorcist.library.filewritercompat.core.FileResult
import com.akexorcist.library.filewritercompat.permission.PekoStoragePermissionRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showSampleImage()

        binding.buttonInternalAppSpecificFile.setOnClickListener {
            lifecycleScope.launch {
                addTimestampAndSaveAsFile { data: ByteArray, fileName: String ->
                    FileWriterCompat.Builder.createInternalAppSpecificFile(
                        fileNameWithExtension = fileName,
                    )
                        .setChildPath("sample/exported")
                        .build()
                        .write(this@MainActivity, data)
                }
            }
        }

        binding.buttonInternalAppSpecificCache.setOnClickListener {
            lifecycleScope.launch {
                addTimestampAndSaveAsFile { data: ByteArray, fileName: String ->
                    FileWriterCompat.Builder.createInternalAppSpecificCache(
                        fileNameWithExtension = fileName,
                    )
                        .setChildPath("sample/exported")
                        .build()
                        .write(this@MainActivity, data)
                }
            }
        }

        binding.buttonExternalAppSpecificFile.setOnClickListener {
            lifecycleScope.launch {
                addTimestampAndSaveAsFile { data: ByteArray, fileName: String ->
                    FileWriterCompat.Builder.createExternalAppSpecificFile(
                        fileNameWithExtension = fileName,
                    )
                        .setDirectoryType(Environment.DIRECTORY_PICTURES)
                        .setChildPath("sample/exported")
                        .build()
                        .write(this@MainActivity, data)
                }
            }
        }

        binding.buttonExternalAppSpecificCache.setOnClickListener {
            lifecycleScope.launch {
                addTimestampAndSaveAsFile { data: ByteArray, fileName: String ->
                    FileWriterCompat.Builder.createExternalAppSpecificCache(
                        fileNameWithExtension = fileName,
                    )
                        .setChildPath("sample/exported")
                        .build()
                        .write(this@MainActivity, data)
                }
            }
        }

        binding.buttonExternalShareableFile.setOnClickListener {
            lifecycleScope.launch {
                addTimestampAndSaveAsFile { data: ByteArray, fileName: String ->
                    FileWriterCompat.Builder.createExternalShareableFile(
                        directoryType = Environment.DIRECTORY_PICTURES,
                        fileNameWithExtension = fileName,
                    )
                        .setChildPath("sample/exported")
                        .setStoragePermissionRequest(PekoStoragePermissionRequest)
                        .build()
                        .write(this@MainActivity, data)
                }
            }
        }
    }

    private fun showSampleImage() {
        val inputStream = assets.open("image_sample.png")
        binding.imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
    }

    private suspend fun <SUCCESS, ERROR> addTimestampAndSaveAsFile(onScreenshotTaken: suspend (data: ByteArray, filename: String) -> FileResult<SUCCESS, ERROR>) {
        val inputStream = assets.open("image_sample.png")
        val option = BitmapFactory.Options().apply {
            this.inMutable = true
        }
        val bitmap = BitmapFactory.decodeStream(inputStream, null, option) ?: return
        val pattern = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val timestamp = LocalDateTime.now().format(pattern)
        BitmapHelper.addTimestamp(bitmap, timestamp)
        val filename = "image_$timestamp.jpg"
        val result = onScreenshotTaken.invoke(bitmap.toByteArray(), filename)
        bitmap.recycle()
        Snackbar.make(binding.root, result.toString(), Snackbar.LENGTH_SHORT).show()
        Log.e(MainActivity::class.java.name, result.toString())
    }
}
