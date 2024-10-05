package com.example.ppe_detector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import com.example.ppe_detector.OnDetectorParameterUpdateListener
import com.example.ppe_detector.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider

class DetectorSettingBottomSheet(private val parameterUpdateListener: OnDetectorParameterUpdateListener) : BottomSheetDialogFragment() {

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
//            Toast.makeText(context, "Max Objects Detection: $newValue", Toast.LENGTH_SHORT).show()
        }

        // Confidence Threshold Slider
        val confidenceThresholdSlider = view.findViewById<Slider>(R.id.confidenceThresholdSlider)
        val confidenceThresholdValue = view.findViewById<TextView>(R.id.confidenceThresholdValue)
        confidenceThresholdSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toFloat()
            confidenceThresholdValue.text =  String.format("%.2f", value)
            parameterUpdateListener.onConfidenceThresholdChanged(newValue)
//            Toast.makeText(context, "Confidence Threshold: $newValue", Toast.LENGTH_SHORT).show()
        }

        // IoU Threshold Slider
        val iouThresholdSlider = view.findViewById<Slider>(R.id.iouThresholdSlider)
        val iouThresholdValue = view.findViewById<TextView>(R.id.iouThresholdValue)
        iouThresholdSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toFloat()
            iouThresholdValue.text = String.format("%.2f", value)
            parameterUpdateListener.onIoUThresholdChanged(newValue)
//            Toast.makeText(context, "IoU Threshold: $newValue", Toast.LENGTH_SHORT).show()
        }

        // Bounding Box Stroke Width Slider
        val boundingBoxStrokeWidthSlider = view.findViewById<Slider>(R.id.boundingBoxStrokeWidthSlider)
        val boundingBoxStrokeWidthValue = view.findViewById<TextView>(R.id.boundingBoxStrokeWidthValue)
        boundingBoxStrokeWidthSlider.addOnChangeListener { _, value, _ ->
            val newValue = value.toFloat()
            boundingBoxStrokeWidthValue.text = String.format("%.2f", value)
            parameterUpdateListener.onBoundingBoxStrokeWidthChanged(newValue)
//            Toast.makeText(context, "Bounding Box Stroke Width: $newValue", Toast.LENGTH_SHORT).show()
        }

        // Model Selection Radio Group
        val modelSelectionGroup = view.findViewById<RadioGroup>(R.id.modelSelectionGroup)
        modelSelectionGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedModel = when (checkedId) {
                R.id.yolov8nRadioButton -> "Yolov8n"
                R.id.mobilenetV2RadioButton -> "MobileNetV2"
                R.id.efficientNetRadioButton -> "EfficientNet"
                else -> ""
            }
            parameterUpdateListener.onModelSelected(selectedModel)
//            Toast.makeText(context, "Selected Model: $selectedModel", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        val TAG: Any = "Bottom Sheet"
    }
}
