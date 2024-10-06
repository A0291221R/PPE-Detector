package com.example.ppe_detector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider

class DetectorSettingBottomSheet(private val parameterUpdateListener: DetectorParamUpdateHandler) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false)

        // Max Object Detection Slider
        val maxObjectDetectionSlider = view.findViewById<Slider>(R.id.maxObjectDetectionSlider)
        val maxObjectDetectionValue = view.findViewById<TextView>(R.id.maxObjectDetectionValue)
        maxObjectDetectionSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toInt()
            maxObjectDetectionValue.text = newValue.toString()
            parameterUpdateListener.onMaxObjectsDetectionChanged(newValue)
        }

        // Confidence Threshold Slider
        val confidenceThresholdSlider = view.findViewById<Slider>(R.id.confidenceThresholdSlider)
        val confidenceThresholdValue = view.findViewById<TextView>(R.id.confidenceThresholdValue)
        confidenceThresholdSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toFloat()
            confidenceThresholdValue.text =  String.format("%.2f", value)
            parameterUpdateListener.onConfidenceThresholdChanged(newValue)
        }

        // IoU Threshold Slider
        val iouThresholdSlider = view.findViewById<Slider>(R.id.iouThresholdSlider)
        val iouThresholdValue = view.findViewById<TextView>(R.id.iouThresholdValue)
        iouThresholdSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toFloat()
            iouThresholdValue.text = String.format("%.2f", value)
            parameterUpdateListener.onIoUThresholdChanged(newValue)
        }

        // Bounding Box Stroke Width Slider
        val boundingBoxStrokeWidthSlider = view.findViewById<Slider>(R.id.boundingBoxStrokeWidthSlider)
        val boundingBoxStrokeWidthValue = view.findViewById<TextView>(R.id.boundingBoxStrokeWidthValue)
        boundingBoxStrokeWidthSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toFloat()
            boundingBoxStrokeWidthValue.text = String.format("%.2f", value)
            parameterUpdateListener.onBoundingBoxStrokeWidthChanged(newValue)
        }

        // Model Selection Radio Group
        val modelSelectionGroup = view.findViewById<RadioGroup>(R.id.modelSelectionGroup)

        modelSelectionGroup.check(
            when (parameterUpdateListener.getLastSelectedModel()) {
                "Yolov8n" ->  R.id.yolov8nRadioButton
                "MobileNetV2" ->  R.id.mobilenetV2RadioButton
                "EfficientNet" ->  R.id.efficientNetRadioButton
                else -> R.id.yolov8nRadioButton
            }
        )

        modelSelectionGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedModel = when (checkedId) {
                R.id.yolov8nRadioButton -> "Yolov8n"
                R.id.mobilenetV2RadioButton -> "MobileNetV2"
                R.id.efficientNetRadioButton -> "EfficientNet"
                else -> ""
            }
            parameterUpdateListener.onModelSelected(selectedModel)
        }

        return view
    }

    companion object {
        val TAG: Any = "Bottom Sheet"
    }
}
