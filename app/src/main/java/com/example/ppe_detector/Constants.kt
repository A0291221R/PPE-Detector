package com.example.ppe_detector

object Constants {
    object yolov8n {
        const val MODEL_PATH = "yolov8n.tflite"
        val LABELS_PATH: String = "yolov8n.txt" // provide your labels.txt file if the metadata not present in the model
    }
    object EfficientNet {
        const val MODEL_PATH = "EfficientNet.tflite"
        val LABELS_PATH: String = "EfficientNet.txt" // provide your labels.txt file if the metadata not present in the model
    }

    object MobileNetV2 {
        const val MODEL_PATH = "MobileNetV2.tflite"
        val LABELS_PATH: String = "MobileNetV2.txt" // provide your labels.txt file if the metadata not present in the model
    }
}
