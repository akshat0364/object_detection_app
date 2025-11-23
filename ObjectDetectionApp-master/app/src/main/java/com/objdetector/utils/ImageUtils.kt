package com.objdetector.utils

import android.graphics.Matrix
import android.media.Image
import java.nio.ByteBuffer
import kotlin.math.abs

object ImageUtils {
    private const val kMaxChannelValue = 262143

    fun convertYUVToARGB(image: Image, previewWidth: Int, previewHeight: Int): IntArray {
        val planes = image.planes
        val yuvBytes = fillBytes(planes)
        return convertYUV420ToARGB8888(
            yuvBytes[0], yuvBytes[1], yuvBytes[2], previewWidth,
            previewHeight, planes[0].rowStride, planes[1].rowStride, planes[1].pixelStride
        )
    }

    private fun fillBytes(planes: Array<Image.Plane>): Array<ByteArray> {
        val yuvBytes = Array(3) { ByteArray(0) }
        for (i in planes.indices) {
            val buffer: ByteBuffer = planes[i].buffer
            if (yuvBytes[i].isEmpty()) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i])
        }
        return yuvBytes
    }

    private fun convertYUV420ToARGB8888(
        yData: ByteArray, uData: ByteArray, vData: ByteArray, width: Int, height: Int,
        yRowStride: Int, uvRowStride: Int, uvPixelStride: Int
    ): IntArray {
        val out = IntArray(width * height)
        var i = 0
        for (y in 0 until height) {
            val pY = yRowStride * y
            val uvRowStart = uvRowStride * (y shr 1)
            var pU = uvRowStart
            var pV = uvRowStart

            for (x in 0 until width) {
                val uvOffset = (x shr 1) * uvPixelStride
                out[i++] = YUV2RGB(
                    convertByteToInt(yData, pY + x),
                    convertByteToInt(uData, pU + uvOffset),
                    convertByteToInt(vData, pV + uvOffset)
                )
            }
        }
        return out
    }

    private fun convertByteToInt(arr: ByteArray, pos: Int): Int {
        return arr[pos].toInt() and 0xFF
    }

    private fun YUV2RGB(nY: Int, nU: Int, nV: Int): Int {
        var y = nY - 16
        var u = nU - 128
        var v = nV - 128
        if (y < 0) y = 0

        var r = 1192 * y + 1634 * v
        var g = 1192 * y - 833 * v - 400 * u
        var b = 1192 * y + 2066 * u

        r = r.coerceIn(0, kMaxChannelValue)
        g = g.coerceIn(0, kMaxChannelValue)
        b = b.coerceIn(0, kMaxChannelValue)

        r = (r shr 10) and 0xff
        g = (g shr 10) and 0xff
        b = (b shr 10) and 0xff

        return -0x1000000 or (r shl 16) or (g shl 8) or b
    }

    fun getTransformationMatrix(
        srcWidth: Int, srcHeight: Int,
        dstWidth: Int, dstHeight: Int,
        applyRotation: Int, maintainAspectRatio: Boolean
    ): Matrix {
        val matrix = Matrix()

        if (applyRotation != 0) {
            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        val transpose = (abs(applyRotation) + 90) % 180 == 0

        val inWidth = if (transpose) srcHeight else srcWidth
        val inHeight = if (transpose) srcWidth else srcHeight

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            val scaleFactorX = dstWidth / inWidth.toFloat()
            val scaleFactorY = dstHeight / inHeight.toFloat()

            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                val scaleFactor = scaleFactorX.coerceAtLeast(scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY)
            }
        }

        if (applyRotation != 0) {
            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }

        return matrix
    }
}
