package ru.anddever.androidimagecompressor

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import ru.anddever.androidimagecompressor.databinding.ActivityMainBinding
import java.io.*
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
                    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    File.createTempFile(
                        "JPEG_${timeStamp}_",
                        ".jpg",
                        storageDir
                    ).apply {
                        var currentPhotoPath: String = absolutePath
                        val byteArray = ByteArray(imageFile!!.length().toInt())
                        val bis = BufferedInputStream(FileInputStream(imageFile))
                        val dis = DataInputStream(bis)
                        dis.readFully(byteArray)
                        val outputStream = FileOutputStream(this)
                        outputStream.write(byteArray)
                        if (imageFile != null) {
                            Picasso.get().load(imageFile!!).into(binding!!.imageView)
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