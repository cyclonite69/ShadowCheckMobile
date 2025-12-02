package com.shadowcheck.mobile

import android.location.Location
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.shadowcheck.mobile.data.WifiNetwork
import kotlin.math.abs

@Composable
fun ARNetworkView(
    networks: List<WifiNetwork>,
    currentLocation: Location?,
    targetBssid: String? = null,
    onBack: () -> Unit
) {
    var azimuth by remember { mutableStateOf(0f) }
    var pitch by remember { mutableStateOf(0f) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        CameraPreview()
        
        // AR overlay
        AROverlay(
            networks = networks,
            currentLocation = currentLocation,
            azimuth = azimuth,
            pitch = pitch
        )
        
        // Header
        Surface(
            modifier = Modifier.align(Alignment.TopCenter),
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "AR Network View",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(surfaceProvider)
                    }
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AROverlay(
    networks: List<WifiNetwork>,
    currentLocation: Location?,
    azimuth: Float,
    pitch: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        if (currentLocation == null) return@Canvas
        
        networks.forEach { network ->
            val results = FloatArray(2)
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                network.latitude,
                network.longitude,
                results
            )
            
            val distance = results[0]
            var bearing = results[1] - azimuth
            
            // Normalize bearing
            if (bearing < -180) bearing += 360
            if (bearing > 180) bearing -= 360
            
            // Only show networks in front of camera (±60 degrees)
            if (abs(bearing) < 60 && distance < 500) {
                val x = centerX + (bearing / 60f) * size.width
                val y = centerY - (pitch / 45f) * size.height * 0.5f
                
                val color = when {
                    network.signalLevel >= -60 -> Color.Green
                    network.signalLevel >= -75 -> Color.Yellow
                    else -> Color.Red
                }
                
                // Draw marker
                drawCircle(
                    color = color,
                    radius = 20f,
                    center = Offset(x, y),
                    alpha = 0.8f
                )
                
                // Draw inner dot
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = Offset(x, y)
                )
            }
        }
        
        // Draw crosshair
        drawLine(
            color = Color.White,
            start = Offset(centerX - 30, centerY),
            end = Offset(centerX + 30, centerY),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.White,
            start = Offset(centerX, centerY - 30),
            end = Offset(centerX, centerY + 30),
            strokeWidth = 2f
        )
    }
}
