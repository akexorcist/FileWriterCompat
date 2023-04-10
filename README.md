[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.akexorcist.filewritercompat/core/badge.svg)](https://search.maven.org/artifact/com.akexorcist.filewritercompat/core)
![Minimum SDK Version](https://img.shields.io/badge/minSdkVersion-23-brightgreen)

File Writer Compat
==============================
Android file writing helper library for API Level 23+

Download
===============================
**Gradle**

```groovy
implementation 'com.akexorcist.filewritercompat:core:1.0.0'

// If you want to handle the runtime permission requesting from the library 
implementation 'com.akexorcist.filewritercompat:permission:1.0.0'
```

Feature
===========================

* Write files to these directories:
    * Internal app-specific file (`/data/data/<package_name>/files/`)
    * Internal app-specific cache (`/data/data/<package_name>/cache/`)
    * External app-specific file (`/<external_storage>/Android/data/<package_name>/files/`)
    * External app-specific cache (`/<external_storage>/Android/data/<package_name>/cache/`)
    * External shareable file (`/<external_storage>/<directory_type>/`)
* Support for API level 23 - 33
* Auto-create subdirectories
* Built-in permission handling (additional dependency is required)
* Auto media scanner triggers when saving a file in an external shareable location

Usage
===========================

## Common usage

```kotlin
suspend fun saveFileToInternalAppSpecificFile(
    activity: FragmentActivity,
    data: ByteArray,
    filename: String
) {
    val writer = FileWriterCompat.Builder.createInternalAppSpecificFile(
        fileNameWithExtension = filename,
    )
        // (Optional) Auto-create sub directory if not exist
        // or skip this method to store the file in parent directory
        .setChildPath("sample/exported")
        .build()
    val result: FileResult<Uri, InternalAppSpecificFile.ErrorReason> = writer.write(activity, data)
}
```

## All supported directories

```kotlin
// Internal app-specific file
val fileWriter: InternalAppSpecificFile.Writer = FileWriterCompat.Builder
    .createInternalAppSpecificFile(/* ... */)
    .build()
val result: FileRequest<Uri, InternalAppSpecificFile.ErrorReason> = fileWriter.write(/* ... */)

// Internal app-specific cache
val fileWriter: InternalAppSpecificCache.Writer = FileWriterCompat.Builder
    .createInternalAppSpecificCache(/* ... */)
    .build()
val result: FileRequest<Uri, InternalAppSpecificCache.ErrorReason> = fileWriter.write(/* ... */)

// External app-specific file
val fileWriter: ExternalAppSpecificFile.Writer = FileWriterCompat.Builder
    .createExternalAppSpecificFile(/* ... */)
    .build()
val result: FileRequest<Uri, ExternalAppSpecificFile.ErrorReason> = fileWriter.write(/* ... */)

// External app-specific cache
val fileWriter: ExternalAppSpecificCache.Writer = FileWriterCompat.Builder
    .createExternalAppSpecificCache(/* ... */)
    .build()
val result: FileRequest<Uri, ExternalAppSpecificCache.ErrorReason> = fileWriter.write(/* ... */)

// External shareable file
val fileWriter: ExternalShareableFile.Writer = FileWriterCompat.Builder
    .createExternalShareableFile(/* ... */)
    .build()
val result: FileRequest<Uri, ExternalShareableFile.ErrorReason> = fileWriter.write(/* ... */)
```

### Internal app-specific file's parameter

**Requires**

* Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

* Child path (String) - e.g, `"sample/exported"`

### Internal app-specific cache's parameter

**Requires**

* Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

* Child path (String) - e.g, `"sample/exported"`

### External app-specific file's parameter

**Requires**

* Directory type (String) - e.g, `Environment.DIRECTORY_PICTURES`
* Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

* Child path (String) - e.g, `"sample/exported"`

### External app-specific cache's parameter

**Requires**

* Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

* Child path (String) - e.g, `"sample/exported"`

### External shareable file's parameter

**Requires**

* Directory type (String) - e.g, `Environment.DIRECTORY_PICTURES`
* Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

* Child path (String) - e.g, `"sample/exported"`
* Storage permission request handing (StoragePermissionRequest) - e.g, `NoOperationStoragePermissionRequest`

## Write external storage permission in external shareable file

The `Manifest.permission.WRITE_EXTERNAL_STORAGE_PERMISSION` must be granted when running on Android 10 or lower. No permission is required for Android 11 or higher.

There are two solutions to handle the runtime permission requesting in this library:
1. Call the runtime permission requesting before calling this library when running on Android 10 or lower.
2. Create a custom `StoragePermissionRequest` and inject it in `ExternalShareableFile.Builder`.

```kotlin
val customStoragePermissionRequest: StoragePermissionRequest = /* ... */ 
val fileWriter: ExternalShareableFile.Writer = FileWriterCompat.Builder.createExternalShareableFile(/* ... */)
    .setStoragePermissionRequest(customStoragePermissionRequest)
    .build()
```

There are two built-in storage permission requests depending on what you want:
* `NoOperationStoragePermissionRequest` - No operation, just check the permission.
* `PekoStoragePermissionRequest` - Request the permission with [Peko library](https://github.com/deva666/Peko)
  * `com.akexorcist.filewritercompat:permission:<latest_version` is required.

You can customize this operation by your own:

```kotlin
object CustomStoragePermissionRequest: StoragePermissionRequest {
    override suspend fun requestWriteExternalStoragePermission(activity: FragmentActivity): Boolean {
        /* Any operation with boolean result */
    }
}
```

## Error handling
When the `write` method is called, the result from this method will be `FileResult<Uri, ErrorReason>`.

The `ErrorReason` depends on what kind of directory you are using. For example, the result will be `FileResult<Uri, ExternalShareableFile.ErrorReason>` when you create the file writer with `ExternalShareableFile.Builder`.

```kotlin
val fileWriter: ExternalShareableFile.Writer = /* ... */
val result: FileResult<Uri, ExternalShareableFile.ErrorReason> = fileWriter.write(/* ... */)
when (result) {
    is FileResult.Success<Uri> -> { 
        /* File was saved */ 
        val uri: Uri = result.result
    }
    is FileResult.Error<ExternalShareableFile.ErrorReason> -> { 
        /* File was not saved for some reason */
        when (result.reason) {
            is ExternalShareableFile.ErrorReason.WriteExternalStoragePermissionDenied -> {
                /* WRITER_EXTERNAL_STORAGE permission was denied */ 
            }
            is ExternalShareableFile.ErrorReason.InvalidDirectoryType -> {
                /* Invalid directory type parameter */
            }
            /* ... */
        }
    }
}
```

Each type of directory has a different error reason.

Licence
===========================
Copyright 2023 Akexorcist

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in
compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.
