package com.objdetector

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.WindowManager
import android.widget.Toast
import com.objdetector.customview.OverlayView

abstract class CameraActivity : Activity(), OnImageAvailableListener {
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_camera)

        if (hasPermission()) {
            setFragment()
        } else {
            requestPermission()
        }
    }

    @Synchronized
    override fun onResume() {
        super.onResume()

        handlerThread = HandlerThread("inference")
        handlerThread?.start()
        handler = Handler(handlerThread!!.looper)
    }

    @Synchronized
    override fun onPause() {
        if (!isFinishing) {
            finish()
        }

        handlerThread?.quitSafely()
        try {
            handlerThread?.join()
            handlerThread = null
            handler = null
        } catch (ex: InterruptedException) {
            Log.e(LOGGING_TAG, "Exception: " + ex.message)
        }

        super.onPause()
    }

    @Synchronized
    protected fun runInBackground(runnable: Runnable) {
        handler?.post(runnable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    setFragment()
                } else {
                    Toast.makeText(
                        this,
                        "Camera permission is required to use this app. Please grant permission in Settings.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(
                    this@CameraActivity,
                    "Camera permission is required for this app",
                    Toast.LENGTH_LONG
                ).show()
            }
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                PERMISSIONS_REQUEST
            )
        }
    }

    protected fun setFragment() {
        val cameraConnectionFragment = CameraConnectionFragment()
        cameraConnectionFragment.addConnectionListener(object : CameraConnectionFragment.ConnectionListener {
            override fun onPreviewSizeChosen(size: Size, cameraRotation: Int) {
                this@CameraActivity.onPreviewSizeChosen(size, cameraRotation)
            }
        })
        cameraConnectionFragment.addImageAvailableListener(this)

        fragmentManager
            .beginTransaction()
            .replace(R.id.container, cameraConnectionFragment)
            .commit()
    }

    fun requestRender() {
        val overlay = findViewById<OverlayView>(R.id.overlay)
        overlay?.postInvalidate()
    }

    fun addCallback(callback: OverlayView.DrawCallback) {
        val overlay = findViewById<OverlayView>(R.id.overlay)
        overlay?.addCallback(callback)
    }

    protected abstract fun onPreviewSizeChosen(size: Size, rotation: Int)

    companion object {
        private const val LOGGING_TAG = "objdetector"
        private const val PERMISSIONS_REQUEST = 1
    }
}
