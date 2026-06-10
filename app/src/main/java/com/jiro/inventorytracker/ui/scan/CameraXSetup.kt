package com.jiro.inventorytracker.ui.scan

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

object CameraXSetup {

    /**
     * Binds CameraX (Preview + ImageAnalysis with [BarcodeAnalyzer]) to the given [lifecycleOwner].
     *
     * The [cameraExecutor] is owned by the caller (e.g. a Composable's DisposableEffect) so
     * its lifetime can match the screen.
     */
    fun bind(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraExecutor: ExecutorService,
        onBarcode: (String) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(onBarcode)) }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
            } catch (t: Throwable) {
                // Camera binding can fail in emulator or on devices without a back camera.
                // Caller should render an error state via the preview view staying blank.
                t.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
