package com.jiro.inventorytracker.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onBarcodeDetected: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Single-thread executor owned by this composable's lifecycle.
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan barcode") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hasPermission) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PreviewView(ctx).also { previewView ->
                            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    update = { previewView ->
                        CameraXSetup.bind(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            cameraExecutor = cameraExecutor,
                            onBarcode = { code -> onBarcodeDetected(code) }
                        )
                    }
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Point camera at a barcode")
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Camera permission is required to scan barcodes.")
                }
            }
        }
    }
}
