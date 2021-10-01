package ru.anddever.androidimagecompressor

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import ru.anddever.androidimagecompressor.Utils.Companion.addPicToGallery
import ru.anddever.androidimagecompressor.Utils.Companion.getAlbumDir
import ru.anddever.androidimagecompressor.databinding.ActivityMainBinding
import java.io.*
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    private var imageFile: File? = null
    private val getImageUri =
        this.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageFile = Utils.copyStreamToFile(this, uri)
                if (imageFile != null) {
                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    //val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val storageDir: File? = getAlbumDir(this)
                    File.createTempFile(
                        "JPEG_${timeStamp}_",
                        ".jpg",
                        storageDir
                    ).apply {
                        val currentPhotoPath: String = absolutePath
                        val byteArray = ByteArray(imageFile!!.length().toInt())
                        val bis = BufferedInputStream(FileInputStream(imageFile))
                        val dis = DataInputStream(bis)
                        dis.readFully(byteArray)
                        val outputStream = FileOutputStream(this)
                        outputStream.write(byteArray)
                        if (imageFile != null) {
                            Picasso.get().load(imageFile!!).into(binding!!.imageView)
                        }

                        if (Build.VERSION.SDK_INT >= 29) {
                            try {
                                val resolver = contentResolver
                                val values = ContentValues()
                                values.put(
                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                    currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/") + 1)
                                )
                                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                                values.put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    Environment.DIRECTORY_PICTURES +
                                            File.separator + this@MainActivity.getString(R.string.app_name)
                                )
                                val imageUri = resolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values
                                )
                                val fos =
                                    Objects.requireNonNull(imageUri)?.let {
                                        resolver.openOutputStream(
                                            it
                                        )
                                    }
                                Files.copy(File(currentPhotoPath).toPath(), fos)
                                Objects.requireNonNull(fos)
                                fos!!.flush()
                                fos.close()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            addPicToGallery(this@MainActivity, currentPhotoPath)
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding!!.activity = this
    }

    fun onPickProblemImageClick(view: View) {
        getImageUri.launch("image/*")
    }
}