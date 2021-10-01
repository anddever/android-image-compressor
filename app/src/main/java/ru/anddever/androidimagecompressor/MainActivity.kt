package ru.anddever.androidimagecompressor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import androidx.databinding.DataBindingUtil
import ru.anddever.androidimagecompressor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
    }

    fun onPickProblemImageClick(view: View) {
        Toast.makeText(this, "test", Toast.LENGTH_LONG).show()
    }
}