package com.objdetector

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.widget.Button
import android.widget.Toast
import com.objdetector.deepmodel.DetectionResult
import com.objdetector.deepmodel.MobileNetObjDetector
import com.objdetector.customview.OverlayView
import com.objdetector.utils.ImageUtils
import java.io.IOException

class MainActivity : CameraActivity(), OnImageAvailableListener {
    private var sensorOrientation: Int? = null
    private var previewWidth = 0
    private var previewHeight = 0
    private var objectDetector: MobileNetObjDetector? = null
    private var imageBitmapForModel: Bitmap? = null
    private var rgbBitmapForCameraImage: Bitmap? = null
    private var computing = false
    private var imageTransformMatrix: Matrix? = null
    private var overlayView: OverlayView? = null
    
    // Detection mode flags
    private var isLiveMode = false
    private var shouldCapture = false
    
    private var btnLive: Button? = null
    private var btnCapture: Button? = null

    override fun onPreviewSizeChosen(previewSize: Size, rotation: Int) {
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            TEXT_SIZE_DIP, resources.displayMetrics
        )

        try {
            objectDetector = MobileNetObjDetector.create(assets)
            Log.i(LOGGING_TAG, "Model Initiated successfully.")
            Toast.makeText(applicationContext, "MobileNetObjDetector created", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "MobileNetObjDetector could not be created", Toast.LENGTH_SHORT).show()
            finish()
        }
        overlayView = findViewById(R.id.overlay)

        val screenOrientation = windowManager.defaultDisplay.rotation
        //Sensor orientation: 90, Screen orientation: 0
        sensorOrientation = rotation + screenOrientation
        Log.i(
            LOGGING_TAG, String.format(
                "Camera rotation: %d, Screen orientation: %d, Sensor orientation: %d",
                rotation, screenOrientation, sensorOrientation
            )
        )

        previewWidth = previewSize.width
        previewHeight = previewSize.height
        Log.i(LOGGING_TAG, "preview width: $previewWidth")
        Log.i(LOGGING_TAG, "preview height: $previewHeight")
        
        // Set preview size in overlay for proper coordinate mapping
        overlayView?.setPreviewSize(previewWidth, previewHeight, sensorOrientation!!)
        
        // create empty bitmap
        imageBitmapForModel = Bitmap.createBitmap(MODEL_IMAGE_INPUT_SIZE, MODEL_IMAGE_INPUT_SIZE, Config.ARGB_8888)
        rgbBitmapForCameraImage = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888)

        imageTransformMatrix = ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            MODEL_IMAGE_INPUT_SIZE, MODEL_IMAGE_INPUT_SIZE, sensorOrientation!!, true
        )
        
        // Setup buttons
        setupButtons()
    }
    
    private fun setupButtons() {
        btnLive = findViewById(R.id.btnLive)
        btnCapture = findViewById(R.id.btnCapture)
        
        btnLive?.setOnClickListener {
            isLiveMode = !isLiveMode
            if (isLiveMode) {
                btnLive?.text = "STOP LIVE"
                btnLive?.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                Toast.makeText(this, "Live detection started", Toast.LENGTH_SHORT).show()
            } else {
                btnLive?.text = "LIVE"
                btnLive?.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                overlayView?.setResults(null)
                Toast.makeText(this, "Live detection stopped", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnCapture?.setOnClickListener {
            if (!computing) {
                shouldCapture = true
                Toast.makeText(this, "Capturing and detecting...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onImageAvailable(reader: ImageReader) {
        var imageFromCamera: Image? = null

        try {
            imageFromCamera = reader.acquireLatestImage()
            if (imageFromCamera == null) {
                return
            }
            
            // Only process if in live mode or capture was requested
            if (!isLiveMode && !shouldCapture) {
                imageFromCamera.close()
                return
            }
            
            if (computing) {
                imageFromCamera.close()
                return
            }
            computing = true
            preprocessImageForModel(imageFromCamera)
            imageFromCamera.close()
        } catch (ex: Exception) {
            imageFromCamera?.close()
            Log.e(LOGGING_TAG, ex.message ?: "Unknown error")
        }

        runInBackground {
            val results = objectDetector!!.detectObjects(imageBitmapForModel!!)
            overlayView!!.setResults(results)

            requestRender()
            computing = false
            
            // If this was a capture request, reset the flag and show results
            if (shouldCapture) {
                shouldCapture = false
                runOnUiThread {
                    Toast.makeText(this, "Detection complete! Found ${results.filter { it.confidence > 0.5 }.size} objects", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun preprocessImageForModel(imageFromCamera: Image) {
        rgbBitmapForCameraImage!!.setPixels(
            ImageUtils.convertYUVToARGB(imageFromCamera, previewWidth, previewHeight),
            0, previewWidth, 0, 0, previewWidth, previewHeight
        )

        Canvas(imageBitmapForModel!!).drawBitmap(rgbBitmapForCameraImage!!, imageTransformMatrix!!, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        objectDetector?.close()
    }

    companion object {
        private const val MODEL_IMAGE_INPUT_SIZE = 300
        private val LOGGING_TAG = MainActivity::class.java.name
        private const val TEXT_SIZE_DIP = 10f
    }
}
