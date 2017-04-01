package com.example.andrzejzuzak.visiondummyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

class LiveDetectionActivity : AppCompatActivity(), SurfaceHolder.Callback, Detector.Processor<TextBlock> {

    var surfaceView: SurfaceView? = null
    var textView: TextView? = null
    var cameraSource: CameraSource? = null

    companion object {
        val REQUEST_CAMERA_PERMISSION_ID = 1001
        val TAG = LiveDetectionActivity.javaClass.simpleName

        fun createIntent(context: Context): Intent {
            return Intent(context, LiveDetectionActivity::class.java)
        }
    }

    override fun receiveDetections(detections: Detector.Detections<TextBlock>?) {
        val items = detections?.detectedItems

        if(items?.size() != 0) {
            textView?.post {
                val sb = StringBuilder(30)

                for(i in 0 until items?.size()!!) {
                    val textBlock = items?.valueAt(i)
                    sb.append(textBlock.value)
                    sb.append('\n')
                }

                textView?.text = sb.toString()
            }
        }
    }

    override fun release() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_detection)

        surfaceView = findViewById(R.id.surfaceView) as SurfaceView?
        textView = findViewById(R.id.textView) as TextView?

        val textRecognizer = TextRecognizer.Builder(applicationContext).build()

        if(!textRecognizer.isOperational) {
            Log.e(TAG, "Text Recognizer not operational")
        } else {
            cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build()

            surfaceView?.holder?.addCallback(this)

            textRecognizer.setProcessor(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_CAMERA_PERMISSION_ID ->
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return
                        }

                        try {
                            cameraSource?.start(surfaceView?.holder)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        cameraSource?.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION_ID)
            }

            cameraSource?.start(surfaceView?.holder)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}
