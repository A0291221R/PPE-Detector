package com.example.ppe_detector

import android.content.Context
import android.widget.Toast

interface OnDetectorParameterUpdateListener {
    fun onMaxObjectsDetectionChanged(value: Int)
    fun onConfidenceThresholdChanged(value: Float)
    fun onIoUThresholdChanged(value: Float)
    fun onBoundingBoxStrokeWidthChanged(value: Float)
    fun onModelSelected(model: String)
}

class DetectorParamUpdateHandler: OnDetectorParameterUpdateListener {
    private var context: Context
    private var detector: Detector
    private var overlay: OverlayView

    constructor(context: Context, detector: Detector, overlay: OverlayView) {
        this.context = context
        this.detector = detector
        this.overlay = overlay
    }
    override fun onMaxObjectsDetectionChanged(value: Int) {
        detector.setMaxObjectDetected(value)
    }

    override fun onConfidenceThresholdChanged(value: Float) {
        detector.setConfidenceThreshold(value)
    }

    override fun onIoUThresholdChanged(value: Float) {
        detector.setIOUThreshold(value)
    }

    override fun onBoundingBoxStrokeWidthChanged(value: Float) {
        overlay.setStrokeWidth(value)
    }

    override fun onModelSelected(model: String) {
        detector.restart(false)
    }
}