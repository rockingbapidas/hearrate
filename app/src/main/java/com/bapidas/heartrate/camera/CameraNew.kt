package com.bapidas.heartrate.camera

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.bapidas.heartrate.processing.ProcessingSupport
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Updated to use CameraX for modern, lifecycle-aware camera handling.
 * This implementation is more robust and handles device compatibility better than the direct Camera2 usage.
 */
class CameraNew(
    private val context: Context,
    private val processingSupport: ProcessingSupport?
) : CameraSupport {

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraExecutor: ExecutorService? = null
    
    private var mPreviewListener: PreviewListener? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    
    private var _isCameraInUse = false
    override val isCameraInUse: Boolean
        get() = _isCameraInUse

    init {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "HeartRate:CameraWakeLock"
        )
    }

    @OptIn(ExperimentalGetImage::class)
    override fun open(): CameraSupport {
        if (_isCameraInUse) return this
        
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                // Use a small resolution for pulse detection to save processing power
                imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(176, 144))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    
                imageAnalysis?.setAnalyzer(cameraExecutor!!) { imageProxy ->
                    val image = imageProxy.image
                    if (image != null && processingSupport != null && mPreviewListener != null) {
                        try {
                            val data = processingSupport.toNv21(image)
                            mPreviewListener?.onPreviewData(data)
                            
                            val value = processingSupport.calculateAverageRed(
                                data.clone(),
                                image.width,
                                image.height
                            )
                            mPreviewListener?.onPreviewCount(value)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during image processing", e)
                        }
                    }
                    imageProxy.close()
                }

                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    ProcessLifecycleOwner.get(),
                    cameraSelector,
                    imageAnalysis
                )
                
                // Enable Torch - essential for heart rate measurement via finger
                camera?.cameraControl?.enableTorch(true)
                
                if (mWakeLock?.isHeld == false) {
                    mWakeLock?.acquire(10 * 60 * 1000L)
                }
                _isCameraInUse = true
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind CameraX use cases", e)
                _isCameraInUse = false
            }
            
        }, ContextCompat.getMainExecutor(context))
        
        return this
    }

    override fun close() {
        try {
            camera?.cameraControl?.enableTorch(false)
            cameraProvider?.unbindAll()
            cameraExecutor?.shutdown()
            
            camera = null
            imageAnalysis = null
            cameraExecutor = null
            
            if (mWakeLock?.isHeld == true) {
                mWakeLock?.release()
            }
            _isCameraInUse = false
        } catch (e: Exception) {
            Log.e(TAG, "Error while closing camera", e)
        }
    }

    override fun addOnPreviewListener(callBack: PreviewListener) {
        mPreviewListener = callBack
    }

    companion object {
        private const val TAG = "CameraNew"
    }
}
