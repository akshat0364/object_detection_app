package com.objdetector.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.objdetector.deepmodel.DetectionResult
import java.util.LinkedList

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint
    private val callbacks: MutableList<DrawCallback> = LinkedList()
    private var results: List<DetectionResult>? = null
    private var colors: List<Int>? = null
    private var previewWidth = 0
    private var previewHeight = 0
    private var sensorOrientation = 0
    
    // Color mapping based on recyclability categories
    private val objectColorMap = mapOf(
        // BLUE - Recyclable Dry & Paper
        "book" to Pair("Blue (Paper)", Color.BLUE),
        "newspaper" to Pair("Blue (Paper)", Color.BLUE),
        
        // GREEN - Glass
        "bottle" to Pair("Green (Glass)", Color.GREEN),
        "wine glass" to Pair("Green (Glass)", Color.GREEN),
        
        // YELLOW - Metal
        "fork" to Pair("Yellow (Metal)", Color.YELLOW),
        "knife" to Pair("Yellow (Metal)", Color.YELLOW),
        "spoon" to Pair("Yellow (Metal)", Color.YELLOW),
        "scissors" to Pair("Yellow (Metal)", Color.YELLOW),
        "refrigerator" to Pair("Yellow (Metal)", Color.YELLOW),
        "microwave" to Pair("Yellow (Metal)", Color.YELLOW),
        "oven" to Pair("Yellow (Metal)", Color.YELLOW),
        "toaster" to Pair("Yellow (Metal)", Color.YELLOW),
        
        // BROWN - Organic
        "banana" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "apple" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "sandwich" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "orange" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "broccoli" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "carrot" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "hot dog" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "pizza" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "donut" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "cake" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "potted plant" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "bird" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "cat" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "dog" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "horse" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "sheep" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "cow" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "elephant" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "bear" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "zebra" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "giraffe" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        
        // RED - E-Waste
        "tv" to Pair("Red (E-Waste)", Color.RED),
        "laptop" to Pair("Red (E-Waste)", Color.RED),
        "mouse" to Pair("Red (E-Waste)", Color.RED),
        "remote" to Pair("Red (E-Waste)", Color.RED),
        "keyboard" to Pair("Red (E-Waste)", Color.RED),
        "cell phone" to Pair("Red (E-Waste)", Color.RED),
        "hair drier" to Pair("Red (E-Waste)", Color.RED),
        "toothbrush" to Pair("Red (E-Waste)", Color.RED),
        "clock" to Pair("Red (E-Waste)", Color.RED),
        
        // ORANGE - Plastic
        "cup" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "bowl" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "handbag" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "backpack" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "umbrella" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "suitcase" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "frisbee" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "sports ball" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "kite" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "skateboard" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "surfboard" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "tennis racket" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "vase" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "teddy bear" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        
        // General items (categorized by most common material)
        "person" to Pair("Brown (Organic)", Color.rgb(139, 69, 19)),
        "bicycle" to Pair("Yellow (Metal)", Color.YELLOW),
        "car" to Pair("Yellow (Metal)", Color.YELLOW),
        "motorcycle" to Pair("Yellow (Metal)", Color.YELLOW),
        "airplane" to Pair("Yellow (Metal)", Color.YELLOW),
        "bus" to Pair("Yellow (Metal)", Color.YELLOW),
        "train" to Pair("Yellow (Metal)", Color.YELLOW),
        "truck" to Pair("Yellow (Metal)", Color.YELLOW),
        "boat" to Pair("Yellow (Metal)", Color.YELLOW),
        "traffic light" to Pair("Red (E-Waste)", Color.RED),
        "fire hydrant" to Pair("Yellow (Metal)", Color.YELLOW),
        "stop sign" to Pair("Yellow (Metal)", Color.YELLOW),
        "parking meter" to Pair("Yellow (Metal)", Color.YELLOW),
        "bench" to Pair("Blue (Paper)", Color.BLUE),
        "tie" to Pair("Blue (Paper)", Color.BLUE),
        "skis" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "snowboard" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "baseball bat" to Pair("Blue (Paper)", Color.BLUE),
        "baseball glove" to Pair("Blue (Paper)", Color.BLUE),
        "chair" to Pair("Blue (Paper)", Color.BLUE),
        "couch" to Pair("Blue (Paper)", Color.BLUE),
        "bed" to Pair("Blue (Paper)", Color.BLUE),
        "dining table" to Pair("Blue (Paper)", Color.BLUE),
        "toilet" to Pair("Orange (Plastic)", Color.rgb(255, 165, 0)),
        "sink" to Pair("Yellow (Metal)", Color.YELLOW)
    )

    init {
        paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.0f
        paint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            16f, resources.displayMetrics
        )
    }
    
    fun getColorForObject(objectName: String): Pair<String, Int> {
        return objectColorMap[objectName.lowercase()] ?: Pair("Gray (Other)", Color.GRAY)
    }

    fun setPreviewSize(previewWidth: Int, previewHeight: Int, sensorOrientation: Int) {
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight
        this.sensorOrientation = sensorOrientation
    }

    fun addCallback(callback: DrawCallback) {
        callbacks.add(callback)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        for (callback in callbacks) {
            callback.drawCallback(canvas)
        }

        results?.let { resultsList ->
            var debugCount = 0
            for (i in resultsList.indices) {
                if (resultsList[i].confidence > 0.5) {
                    val box = reCalcSize(resultsList[i].location)
                    val objectName = resultsList[i].title
                    val colorInfo = getColorForObject(objectName)
                    val colorName = colorInfo.first
                    val colorValue = colorInfo.second
                    
                    val title = "$objectName - $colorName ${String.format("%.0f", resultsList[i].confidence * 100)}%"
                    
                    // Debug first box only
                    if (debugCount == 0) {
                        android.util.Log.d("OverlayView", "=== FIRST DETECTION ===")
                        android.util.Log.d("OverlayView", "Original: ${resultsList[i].location}")
                        android.util.Log.d("OverlayView", "Transformed: $box")
                        android.util.Log.d("OverlayView", "Object: $objectName, Color: $colorName")
                        android.util.Log.d("OverlayView", "View: ${width}x${height}, Preview: ${previewWidth}x${previewHeight}, Rotation: $sensorOrientation")
                        debugCount++
                    }
                    
                    // Draw box with the object's color
                    paint.color = colorValue
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 4.0f
                    canvas.drawRect(box, paint)
                    
                    // Draw text with background for readability
                    paint.style = Paint.Style.FILL
                    paint.color = Color.BLACK
                    paint.alpha = 180
                    val textWidth = paint.measureText(title)
                    canvas.drawRect(box.left - 2, box.top - 35, box.left + textWidth + 4, box.top - 2, paint)
                    
                    // Draw text in object's color
                    paint.alpha = 255
                    paint.color = colorValue
                    paint.strokeWidth = 2.0f
                    canvas.drawText(title, box.left, box.top - 10, paint)
                }
            }
        }
    }

    fun setResults(results: List<DetectionResult>?) {
        this.results = results
        postInvalidate()
    }

    interface DrawCallback {
        fun drawCallback(canvas: Canvas?)
    }

    private fun reCalcSize(rect: RectF): RectF {
        if (previewWidth == 0 || previewHeight == 0) {
            return rect
        }

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        
        // Model outputs in 300x300, preview is 1080x1080
        // Preview fills screen (1080x2245) by scaling to width
        val scale = viewWidth / INPUT_SIZE.toFloat()
        
        // Preview (1080x1080) scaled to screen width (1080) = 1080x1080 on screen
        // This is centered vertically in the 2245 height
        val scaledPreviewHeight = INPUT_SIZE * scale
        val offsetY = (viewHeight - scaledPreviewHeight) / 2.0f
        
        // Direct mapping
        val left = rect.left * scale
        val top = rect.top * scale + offsetY
        val right = rect.right * scale
        val bottom = rect.bottom * scale + offsetY
        
        return RectF(left, top, right, bottom)
    }

    companion object {
        private const val INPUT_SIZE = 300
    }
}
