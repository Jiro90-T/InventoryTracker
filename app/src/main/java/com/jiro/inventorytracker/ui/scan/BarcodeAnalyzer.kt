package com.jiro.inventorytracker.ui.scan

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * CameraX ImageAnalysis.Analyzer that runs ML Kit barcode detection on each frame.
 *
 * Calls [onDetected] exactly once per unique value while the same code stays in frame,
 * then goes quiet until a new value appears. This prevents the Add screen from being
 * triggered dozens of times per second.
 */
class BarcodeAnalyzer(
    private val onDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
    )

    @Volatile
    private var lastEmittedValue: String? = null

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val input = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        scanner.process(input)
            .addOnSuccessListener { barcodes ->
                val first = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }
                if (first != null) {
                    val value = first.rawValue!!
                    if (value != lastEmittedValue) {
                        lastEmittedValue = value
                        onDetected(value)
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}
