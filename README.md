![Android Kotlin](https://img.shields.io/badge/Android-Kotlin-6C3FD1.svg?style=flat&logo=android)
![Minimum SDK Version](https://img.shields.io/badge/API-21+-brightgreen)
![Apache 2.0](https://img.shields.io/badge/License-Apache%202-brightgreen)

[![Maven Central - Core](https://img.shields.io/maven-central/v/com.akexorcist.filewritercompat/core?color=brightgreen&label=Core)](https://search.maven.org/artifact/com.akexorcist/round-corner-progress-bar)
[![Maven Central - Permission](https://img.shields.io/maven-central/v/com.akexorcist.filewritercompat/core?color=brightgreen&label=Permission)](https://search.maven.org/artifact/com.akexorcist/round-corner-progress-bar)

# File Writer Compat

Android file writing helper library for API Level 21+

# Download

**Gradle**

```groovy
implementation 'com.akexorcist.filewritercompat:core:1.1.0'

// If you want to handle the runtime permission requesting from the library
implementation 'com.akexorcist.filewritercompat:permission:1.1.0'
```

# Feature

- Write files to these directories:
  - Internal app-specific file (`/data/data/<package_name>/files/`)
  - Internal app-specific cache (`/data/data/<package_name>/cache/`)
  - External app-specific file (`/<external_storage>/Android/data/<package_name>/files/`)
  - External app-specific cache (`/<external_storage>/Android/data/<package_name>/cache/`)
  - External shareable file (`/<external_storage>/<directory_type>/`)
- Support for API level 21 - 33
- Auto-create subdirectories
- Built-in permission handling (additional dependency is required)
- Auto media scanner triggers when saving a file in an external shareable directory

# Usage

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
val executor: InternalAppSpecificFile.Executor = FileWriterCompat.Builder
    .createInternalAppSpecificFile(/* ... */)
    .build()
val result: FileRequest<Uri, InternalAppSpecificFile.ErrorReason> = executor.write(/* ... */)

// Internal app-specific cache
val executor: InternalAppSpecificCache.Executor = FileWriterCompat.Builder
    .createInternalAppSpecificCache(/* ... */)
    .build()
val result: FileRequest<Uri, InternalAppSpecificCache.ErrorReason> = executor.write(/* ... */)

// External app-specific file
val executor: ExternalAppSpecificFile.Executor = FileWriterCompat.Builder
    .createExternalAppSpecificFile(/* ... */)
    .build()
val result: FileRequest<Uri, ExternalAppSpecificFile.ErrorReason> = executor.write(/* ... */)

// External app-specific cache
val executor: ExternalAppSpecificCache.Executor = FileWriterCompat.Builder
    .createExternalAppSpecificCache(/* ... */)
    .build()
val result: FileRequest<Uri, ExternalAppSpecificCache.ErrorReason> = executor.write(/* ... */)

// External shareable file
val executor: ExternalShareableFile.Executor = FileWriterCompat.Builder
    .createExternalShareableFile(/* ... */)
    .build()
val result: FileRequest<Uri, ExternalShareableFile.ErrorReason> = executor.write(/* ... */)
```

### Internal app-specific file's parameter

**Requires**

- Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

- Child path (String) - e.g, `"sample/exported"`

### Internal app-specific cache's parameter

**Requires**

- Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

- Child path (String) - e.g, `"sample/exported"`

### External app-specific file's parameter

**Requires**

- Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

- Directory type (String) - e.g, `Environment.DIRECTORY_PICTURES`
- Child path (String) - e.g, `"sample/exported"`

### External app-specific cache's parameter

**Requires**

- Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

- Child path (String) - e.g, `"sample/exported"`

### External shareable file's parameter

**Requires**

- Directory type (String) - e.g, `Environment.DIRECTORY_PICTURES`
- Filename with extension (String) - e.g, `"image.jpg"`

**Optional**

- Child path (String) - e.g, `"sample/exported"`
- Storage permission request handing (StoragePermissionRequest) - e.g, `NoOperationStoragePermissionRequest`

## Write external storage permission in external shareable file

The `Manifest.permission.WRITE_EXTERNAL_STORAGE_PERMISSION` must be granted when running on Android 10 or lower. No permission is required for Android 11 or higher.

There are two solutions to handle the runtime permission requesting in this library:

1. Call the runtime permission requesting before calling this library when running on Android 10 or lower.
2. Create a custom `StoragePermissionRequest` and inject it in `ExternalShareableFile.Builder`.

```kotlin
val customStoragePermissionRequest: StoragePermissionRequest = /* ... */
val executor: ExternalShareableFile.Executor = FileWriterCompat.Builder.createExternalShareableFile(/* ... */)
    .setStoragePermissionRequest(customStoragePermissionRequest)
    .build()
```

There are two built-in storage permission requests depending on what you want:

- `NoOperationStoragePermissionRequest` - No operation, just check the permission.
- `PekoStoragePermissionRequest` - Request the permission with [Peko library](https://github.com/deva666/Peko)
  - `com.akexorcist.filewritercompat:permission:<latest_version` is required.

You can customize this operation by your own:

```kotlin
object CustomStoragePermissionRequest: StoragePermissionRequest {
    override suspend fun requestWriteExternalStoragePermission(activity: FragmentActivity): Boolean {
        /* Any operation with boolean result */
    }
}
```

## Data type

There are 4 overloaded methods for the write method:

```kotlin
suspend fun write(activity: FragmentActivity, data: ByteArray): FileResult<Uri, ErrorReaon>
suspend fun write(activity: FragmentActivity, data: String): FileResult<Uri, ErrorReaon>
suspend fun write(activity: FragmentActivity, data: Any): FileResult<Uri, ErrorReaon>
suspend fun <DATA> write(activity: FragmentActivity, data: DATA, writer: (DATA, File) -> Unit): FileResult<Uri, ErrorReaon>
```

Additionally, you can create your custom data writer function for any data type.

For example:

```kotlin
val customWriter = { data: Int, file: File ->
  if (!file.exists()) {
    file.createNewFile()
  }
  val fos = FileOutputStream(file)
  ObjectOutputStream(fos).use {
    it.writeInt(data)
  }
}

val data: Int = 1024
val activity: FragmentActivity
val executor: WriterExecutor<Uri, ErrorReason> = /* ... */
val result: FileResult<Uri, ErrorReason> = executor.write(activity, data, customWriter)
```

## Error handling

When the `write` method is called, the result from this method will be `FileResult<Uri, ErrorReason>`.

The `ErrorReason` depends on what kind of directory you are using. For example, the result will be `FileResult<Uri, ExternalShareableFile.ErrorReason>` when you create the file writer with `ExternalShareableFile.Builder`.

```kotlin
val executor: ExternalShareableFile.Executor = /* ... */
val result: FileResult<Uri, ExternalShareableFile.ErrorReason> = executor.write(/* ... */)
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

## Troubleshooting

### Minimum SDK version cannot be smaller than version 23 declared in library `[com.markodevcic:peko:<version>]`

This can happen when you use `com.akexorcist.filewritercompat:permission`, as the Peko library has a minimum SDK version at 23.

To solve this problem, declare the `<uses-sdk>` tag in your Android manifest to force usage:

```xml
<application>

  <uses-sdk tools:overrideLibrary="com.markodevcic.peko"/>

  <!-- ... -->
</application>
```

# Licence

Copyright 2023 Akexorcist

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in
compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.
