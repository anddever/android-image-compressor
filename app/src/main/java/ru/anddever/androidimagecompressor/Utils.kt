package ru.anddever.androidimagecompressor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

class Utils {

    companion object {
        fun copyStreamToFile(context: Context, uri: Uri): File {
            val outputFile = File.createTempFile("temp", null)
            context.contentResolver.openInputStream(uri)?.use { input ->
                val outputStream = FileOutputStream(outputFile)
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val byteCount = input.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                }
            }
            return outputFile
        }

        fun getAlbumDir(context: Context): File? {
            var storageDir: File? = null
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                storageDir = if (Build.VERSION.SDK_INT >= 29) {
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                } else {
                    File(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        ), context.getString(R.string.app_name)
                    )
                }
                if (!storageDir!!.mkdirs()) {
                    if (!storageDir.exists()) {
                        return null
                    }
                }
            }
            return storageDir
        }

        fun addPicToGallery(context: Context, mCurrentPhotoPath: String) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(mCurrentPhotoPath)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        }
    }
}