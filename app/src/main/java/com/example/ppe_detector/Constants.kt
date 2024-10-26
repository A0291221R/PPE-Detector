package com.example.ppe_detector

object Constants {
    object yolov8n {
        const val MODEL_PATH = "yolov8n.tflite"
        val LABELS_PATH: String = "yolov8n.txt" // provide your labels.txt file if the metadata not present in the model
    }
    object EfficientNet {
        const val MODEL_PATH = "yolov8m.tflite"
        val LABELS_PATH: String = "yolov8m.txt" // provide your labels.txt file if the metadata not present in the model
    }

    object MobileNetV2 {
        const val MODEL_PATH = "yolov8s.tflite"
        val LABELS_PATH: String = "yolov8s.txt" // provide your labels.txt file if the metadata not present in the model
    }
}
