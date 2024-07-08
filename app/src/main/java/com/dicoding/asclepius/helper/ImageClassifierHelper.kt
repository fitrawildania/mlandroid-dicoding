package com.dicoding.asclepius.helper

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import java.io.IOException

class ImageClassifierHelper(private val context: Context) {

    private lateinit var imageClassifier: ImageClassifier

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        try {
            val baseOptions = BaseOptions.builder().setNumThreads(4).build()
            val options = ImageClassifierOptions.builder()
                .setBaseOptions(baseOptions)
                .build()

            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "cancer_classification.tflite",
                options
            )
        } catch (e: IOException) {
            Log.e(TAG, "Error : ", e)
        }
    }

    fun classifyStaticImage(imageUri: Uri): List<Classifications>? {
        return try {
            val bitmap = getBitmapFromUri(imageUri)
            val tensorImage = TensorImage.fromBitmap(bitmap)
            imageClassifier.classify(tensorImage)
        } catch (e: IOException) {
            Log.e(TAG, "Error image: ", e)
            null
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }
}
