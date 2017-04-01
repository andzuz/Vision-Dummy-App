package com.example.andrzejzuzak.visiondummyapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var liveDetectionButton: Button? = null
    var photoDetectionButton: Button? = null
    var imageImageView: ImageView? = null
    var file: Uri? = null

    companion object {
        val CAMERA_REQUEST_ID = 1
        val TAG = MainActivity::class.java.simpleName
        val DETECTION_FROM_PHOTO_REQ_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        liveDetectionButton = findViewById(R.id.liveDetectionButton) as Button?
        liveDetectionButton?.setOnClickListener {
            val intent = LiveDetectionActivity.createIntent(context = this)
            startActivity(intent)
        }

        photoDetectionButton = findViewById(R.id.photoDetectionButton) as Button?
        photoDetectionButton?.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        imageImageView = findViewById(R.id.imageImageView) as ImageView?
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                file = Uri.fromFile(getOutputMediaFile())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file)

                startActivityForResult(intent, CAMERA_REQUEST_ID)
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CAMERA_REQUEST_ID && resultCode == Activity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(file?.path)
            val intent = DetectionFromPhotoActivity.createIntentWithData(this, file?.path!!)

            imageImageView?.setImageBitmap(imageBitmap)
            startActivityForResult(intent, DETECTION_FROM_PHOTO_REQ_CODE)
        } else if(requestCode == DETECTION_FROM_PHOTO_REQ_CODE && resultCode == Activity.RESULT_OK) {
            val fileToDelete = File(file?.getPath())

            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    Log.i(TAG, "File deleted")
                } else {
                    Log.i(TAG, "File not deleted")
                }
            }
        }
    }

}
