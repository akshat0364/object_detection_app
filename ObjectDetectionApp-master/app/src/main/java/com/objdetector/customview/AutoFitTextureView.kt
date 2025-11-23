package com.objdetector.customview

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class AutoFitTextureView : TextureView {
    private var ratioWidth = 0
    private var ratioHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        ratioWidth = width
        ratioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == ratioWidth || 0 == ratioHeight) {
            setMeasuredDimension(width, height)
        } else {
            // Fill the entire screen, may crop edges but no black bars
            if (width < height * ratioWidth / ratioHeight) {
                setMeasuredDimension(height * ratioWidth / ratioHeight, height)
            } else {
                setMeasuredDimension(width, width * ratioHeight / ratioWidth)
            }
        }
    }
}
