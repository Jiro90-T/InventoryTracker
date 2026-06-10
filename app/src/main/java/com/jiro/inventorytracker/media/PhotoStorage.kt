package com.jiro.inventorytracker.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoStorage {

    private const val DIR = "item_photos"

    fun photosDir(context: Context): File {
        val dir = File(context.filesDir, DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** Creates a destination file under app-internal storage for a future camera capture. */
    fun newCameraFile(context: Context): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(photosDir(context), "IMG_$ts.jpg")
    }

    /**
     * Copies the content at [source] into app-internal storage and returns the absolute
     * file path. We copy (rather than store the content URI) so the photo survives the
     * picker permission lifecycle.
     */
    fun importFromUri(context: Context, source: Uri): String? {
        return try {
            val dest = newCameraFile(context)
            context.contentResolver.openInputStream(source).use { input ->
                FileOutputStream(dest).use { output ->
                    input?.copyTo(output)
                }
            }
            dest.absolutePath
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    /** Wraps [file] in a FileProvider URI for the camera intent. */
    fun fileProviderUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
}
