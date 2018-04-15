package com.example.andrzejzuzak.visiondummyapp.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.andrzejzuzak.visiondummyapp.LotterixApplication
import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.extensions.launchActivity
import com.example.andrzejzuzak.visiondummyapp.menu.MenuActivity
import com.example.andrzejzuzak.visiondummyapp.networking.PostResultRequestDto
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_ocr.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class OcrActivity : AppCompatActivity(), OcrPresentation {

    @Inject lateinit var presenter: OcrPresenter

    lateinit var file: Uri
    lateinit var bitmap: Bitmap

    companion object {
        const val CAMERA_REQ_CODE = 1
        const val CROP_REQ_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        (application as LotterixApplication).daggerGraph.inject(this)
        presenter.onCreate(this)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        initListeners()
    }

    private fun initListeners() {
        proceedButton.setOnClickListener {
            launchActivity<MenuActivity>()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    @SuppressLint("LongLogTag")
    private fun processImage() {
        val textRecognizer =
                TextRecognizer.Builder(applicationContext).build()

        if (!textRecognizer.isOperational) {
            Log.e(OcrActivity::class.java.simpleName,
                    "TextRecognizer not operational")
        } else {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val textBlocks = textRecognizer.detect(frame)
            val resultRows = arrayListOf<String>()
            val resultRowsSb = StringBuilder("Scanned numbers:\n")

            for (i in 0 until textBlocks.size()) {
                val textBlock = textBlocks.valueAt(i)
                val adjustedBlock = textBlock.value.replace("X","-")

                val adjustedBlockRows = adjustedBlock.split("\n")
                var adjustedBlockIndex = 1
                for (adjustedBlockRow in adjustedBlockRows) {
                    resultRowsSb.append("$adjustedBlockIndex: ")
                    adjustedBlockIndex++
                    resultRowsSb.append(adjustedBlockRow)
                    resultRowsSb.append('\n')
                }

                resultRows.add(textBlock.value)
            }

            val resultRequest = PostResultRequestDto(resultRows)
            presenter.postResult(resultRequest)
            detectionResultTextView.text = resultRowsSb.toString()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                file = Uri.fromFile(getOutputMediaFile())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file)

                startActivityForResult(intent, CAMERA_REQ_CODE)
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish()
            }
        }
    }

    override fun showMatchedCount(matchedCount: Int) {
        detectionResultTextView.text = "${detectionResultTextView.text.toString()}\n$matchedCount NUMBERS ARE MATCHING!"
        proceedButton.visibility = View.VISIBLE
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo")

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg")
    }

    private fun performCrop() {
        // call the standard crop action intent (the user device may not
        // support it)
        val cropIntent = Intent("com.android.camera.action.CROP")

        // indicate image type and Uri
        cropIntent.setDataAndType(file, "image/*")

        // set crop properties
        cropIntent.putExtra("crop", "true")

        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)

        // indicate output X and Y
        cropIntent.putExtra("outputX", 256)
        cropIntent.putExtra("outputY", 256)

        // retrieve data on return
        cropIntent.putExtra("return-data", true)

        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CAMERA_REQ_CODE && resultCode == Activity.RESULT_OK) {
            performCrop()
        } else if (requestCode == CROP_REQ_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                bitmap = BitmapFactory.decodeFile(data.data.path)
                processImage()
                deletePhotoFile()
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            deletePhotoFile()
            finish()
        }
    }

    private fun deletePhotoFile() {
        val fileToDelete = File(file.path)

        if (fileToDelete.exists()) {
            fileToDelete.delete()
        }
    }

}
