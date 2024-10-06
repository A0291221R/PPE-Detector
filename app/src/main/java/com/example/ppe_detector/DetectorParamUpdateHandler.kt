package com.example.ppe_detector

import android.content.Context

interface OnActorUpdateListener {
    fun onModelSelected(model: String)
    fun onBoundingBoxStrokeWidthChanged(value: Float)
}
interface OnDetectorParameterUpdateListener {
    fun onMaxObjectsDetectionChanged(value: Int)
    fun onConfidenceThresholdChanged(value: Float)
    fun onIoUThresholdChanged(value: Float)
}

class DetectorParamUpdateHandler: OnDetectorParameterUpdateListener, OnActorUpdateListener {
    private var context: Context
    private var detector: Detector
    private var actor: OnActorUpdateListener
    private var lastSelectedModel: String = ""

    constructor(context: Context, detector: Detector, actor: OnActorUpdateListener) {
        this.context = context
        this.detector = detector
        this.actor = actor
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
        actor.onBoundingBoxStrokeWidthChanged(value)
    }

    override fun onModelSelected(model: String) {
        if (lastSelectedModel != model) {
            lastSelectedModel = model
            actor.onModelSelected(model)
        }
    }

    fun getLastSelectedModel(): String {
        return lastSelectedModel
    }
}