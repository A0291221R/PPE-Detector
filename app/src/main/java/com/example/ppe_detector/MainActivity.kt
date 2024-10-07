package com.example.ppe_detector

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ppe_detector.Constants.yolov8n
import com.example.ppe_detector.Constants.EfficientNet
import com.example.ppe_detector.Constants.MobileNetV2
import com.example.ppe_detector.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), Detector.DetectorListener, OnActorUpdateListener {
    private lateinit var binding: ActivityMainBinding
    private val isFrontCamera = false

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var detector: Detector? = null

    private lateinit var fab: FloatingActionButton
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var  detectorParamUpdateListener: DetectorParamUpdateHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraExecutor.execute {
            detector = Detector(baseContext, yolov8n.MODEL_PATH, yolov8n.LABELS_PATH, this) {
                toast(it)
            }
            detectorParamUpdateListener = DetectorParamUpdateHandler(baseContext, detector!!, this)
            var bottomSheetFragment = DetectorSettingBottomSheet(detectorParamUpdateListener)
            val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
            fab.setOnClickListener{
                // Handle the click event here
                if (!bottomSheetFragment.isAdded) {
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                }
            }
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        bindListeners()
    }
    private fun bindListeners() {
//        binding.apply {
//            isGpu.setOnCheckedChangeListener { buttonView, isChecked ->
//                cameraExecutor.submit {
//                    detector?.restart(isGpu = isChecked)
//                }
//                if (isChecked) {
//                    buttonView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.orange))
//                } else {
//                    buttonView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.gray))
//                }
//            }
//        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider  = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview =  Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                if (isFrontCamera) {
                    postScale(
                        -1f,
                        1f,
                        imageProxy.width.toFloat(),
                        imageProxy.height.toFloat()
                    )
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                matrix, true
            )

            detector?.detect(rotatedBitmap)
        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

            preview?.surfaceProvider = binding.viewFinder.surfaceProvider
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
    }

    private fun toast(message: String) {
        runOnUiThread {
            Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector?.close()
        cameraExecutor.shutdown()
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()){
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf (
            Manifest.permission.CAMERA
        ).toTypedArray()
    }

    override fun onEmptyDetect() {
        runOnUiThread {
            binding.overlay.clear()
        }
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            val fps: Float = if (inferenceTime > 0) 1000f / inferenceTime else 0.0f
            binding.msTv.text =  "${inferenceTime} ms"
            binding.fpsTv.text = String.format("%.1f FPS", fps)
            binding.overlay.apply {
                setResults(boundingBoxes)
                invalidate()
            }
        }
    }

    override fun onModelSelected(model: String) {
        val modelPath:String =  when (model) {
            "Yolov8n" ->  yolov8n.MODEL_PATH
            "MobileNetV2" ->  MobileNetV2.MODEL_PATH
            "EfficientNet" ->  EfficientNet.MODEL_PATH
            else -> yolov8n.MODEL_PATH
        }

        val filePath: String =  when (model) {
            "Yolov8n" ->  yolov8n.LABELS_PATH
            "MobileNetV2" ->  MobileNetV2.LABELS_PATH
            "EfficientNet" ->  EfficientNet.LABELS_PATH
            else -> yolov8n.LABELS_PATH
        }

        binding.msTv.text = "--.- ms"
        binding.fpsTv.text = "--.- FPS"
        cameraExecutor.submit {
            detector?.reload(modelPath, filePath)
            binding.inferenceModel.text = model
        }
    }

    override fun onBoundingBoxStrokeWidthChanged(value: Float) {
        binding.overlay.setStrokeWidth(value)
    }
}

