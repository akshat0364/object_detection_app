package com.objdetector.deepmodel

import android.graphics.RectF

data class DetectionResult(
    val id: Int,
    val title: String,
    val confidence: Float,
    var location: RectF
) : Comparable<DetectionResult> {

    override fun compareTo(other: DetectionResult): Int {
        return other.confidence.compareTo(this.confidence)
    }

    override fun toString(): String {
        return "DetectionResult{" +
                "id=$id" +
                ", title='$title'" +
                ", confidence=$confidence" +
                ", location=$location" +
                '}'
    }
}
