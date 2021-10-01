package ru.anddever.androidimagecompressor

import android.content.Context
import android.net.Uri
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
    }
}