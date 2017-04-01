package com.example.andrzejzuzak.visiondummyapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer

class DetectionFromPhotoActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    var detectionResultTextView: TextView? = null

    companion object {
        @JvmField val EXTRA_IMAGE = "extra.image"
        @JvmField val TAG = DetectionFromPhotoActivity::class.java.simpleName

        fun createIntentWithData(context: Context, dataPath: String): Intent {
            val intent = Intent(context, DetectionFromPhotoActivity::class.java)
            val bundle = Bundle()

            bundle.putString(EXTRA_IMAGE, dataPath)
            intent.putExtras(bundle)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection_from_photo)
        detectionResultTextView = findViewById(R.id.detectionResultTextView) as? TextView
        extractDataFromExtras()
        processImage()
    }

    private fun processImage() {
        val textRecognizer =
                TextRecognizer.Builder(applicationContext).build()

        if(!textRecognizer.isOperational) {
            Log.e(TAG, "TextRecognizer not operational")
        } else {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val textBlocks = textRecognizer.detect(frame)
            val stringBuilder = StringBuilder()

            for(i in 0 until textBlocks.size()) {
                val textBlock = textBlocks.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append('\n')
            }

            detectionResultTextView?.text = stringBuilder.toString()
        }
    }

    private fun extractDataFromExtras() {
        val path = intent.extras.getString(EXTRA_IMAGE)
        bitmap = BitmapFactory.decodeFile(path)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

}
