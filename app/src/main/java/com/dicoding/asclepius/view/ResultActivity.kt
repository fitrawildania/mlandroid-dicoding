package com.dicoding.asclepius.view

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import java.io.OutputStream

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("RESULT")
        val imageUri = intent.getParcelableExtra<Uri>("IMAGE_URI")

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }
        binding.resultText.text = result

        binding.btnSave.setOnClickListener {Save(imageUri, result)
        }
    }

    private fun Save(imageUri: Uri?, result: String?) {
        if (imageUri == null || result == null) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = (binding.resultImage.drawable as BitmapDrawable).bitmap
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "classified_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Asclepius")
        }

        val imageUri = contentResolver.insert(imageCollection, contentValues)
        imageUri?.let { uri ->
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream.use { stream ->
                if (stream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
        saveResultText(result)
    }

    private fun saveResultText(result: String) {
        val resultFileName = "classified_result_${System.currentTimeMillis()}.txt"
        val resultFile = getExternalFilesDir(null)?.resolve(resultFileName)

        resultFile?.bufferedWriter().use { writer ->
            writer?.write(result)
        }
        Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show()
    }
}