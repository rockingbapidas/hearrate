package com.bapidas.heartrate.camera

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PreviewCallback
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.Surface
import com.bapidas.heartrate.processing.ProcessingSupport

/**
 * Created by bapidas on 12/10/17.
 */
class CameraOld(
    mContext: Context,
    mProcessingSupport: ProcessingSupport?
) : CameraSupport {
    private val mContext: Context
    private var mCamera: Camera? = null
    private val mCameraId = 0
    private var mWakeLock: WakeLock? = null
    private val mObject = Any()
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mPreviewListener: PreviewListener? = null
    private val mProcessingSupport: ProcessingSupport?
    override fun open(): CameraSupport {
        if (mCamera == null) {
            try {
                mWakeLock?.acquire(10 * 60 * 1000L)
                mCamera = Camera.open(mCameraId)
                setCameraOutputs()
            } catch (e: Exception) {
                throw RuntimeException("Interrupted while trying to open camera.", e)
            }
        }
        return this
    }

    override fun close() {
        try {
            if (mCamera != null) {
                mWakeLock?.release()
                mCamera?.setPreviewCallbackWithBuffer(null)
                mCamera?.stopPreview()
                mCamera?.release()
                mCamera = null
                releaseSurface()
            }
        } catch (e: Exception) {
            throw RuntimeException("Interrupted while trying to close camera.", e)
        }
    }

    override fun addOnPreviewListener(callBack: PreviewListener) {
        mPreviewListener = callBack
    }

    override val isCameraInUse: Boolean
        get() = mCamera != null

    private fun setCameraOutputs() {
        try {
            if (mCamera == null) {
                return
            }
            val parameters = mCamera?.parameters
            parameters?.previewFormat = ImageFormat.NV21
            var rotation = 0
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(mCameraId, cameraInfo)
            when ((mContext as Activity).windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_90 -> rotation = 90
                Surface.ROTATION_180 -> rotation = 180
                Surface.ROTATION_270 -> rotation = 270
                Surface.ROTATION_0 -> {
                }
            }
            mCamera?.setDisplayOrientation(
                if (cameraInfo.facing == 1)
                    (360 - (rotation + cameraInfo.orientation) % 360) % 360
                else
                    (cameraInfo.orientation - rotation + 360) % 360
            )
            val size = getSmallestPreviewSize(parameters)
            if (size != null) {
                parameters?.setPreviewSize(size.width, size.height)
            }
            val supportedPreviewFpsRange: List<*>? =
                parameters?.supportedPreviewFpsRange
            if (supportedPreviewFpsRange != null) {
                val fpsRange = supportedPreviewFpsRange.size
                if (fpsRange > 0) {
                    val iArr = supportedPreviewFpsRange[fpsRange - 1] as IntArray?
                    if (iArr != null && iArr.size > 1) {
                        parameters.setPreviewFpsRange(iArr[0], iArr[1])
                    }
                }
            }
            val supportedFocusModes: List<*>? =
                parameters?.supportedFocusModes
            if (supportedFocusModes != null && supportedFocusModes
                    .contains(Camera.Parameters.FOCUS_MODE_INFINITY)
            ) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_INFINITY
            }
            var bitsPerPixel = ImageFormat.getBitsPerPixel(parameters?.previewFormat ?: 0)
            if (bitsPerPixel % 8 != 0) {
                bitsPerPixel = (bitsPerPixel / 8 + 1) * 8
            }
            if (size != null) {
                bitsPerPixel = bitsPerPixel * (size.width * size.height) / 8
            }
            val supportedFlashModes: List<*>? =
                parameters?.supportedFlashModes
            val isFlash =
                !(supportedFlashModes == null || supportedFlashModes.isEmpty()
                        || supportedFlashModes.size == 1 && (supportedFlashModes[0]
                        == Camera.Parameters.FLASH_MODE_OFF))
            if (isFlash) {
                parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            } else {
                parameters?.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }
            mCamera?.addCallbackBuffer(ByteArray(bitsPerPixel))
            mCamera?.setPreviewCallbackWithBuffer(mPreviewCallback)
            mCamera?.setPreviewTexture(createSurfaceTexture())
            mCamera?.parameters = parameters
            mCamera?.startPreview()
        } catch (e: Exception) {
            throw RuntimeException("Interrupted while trying to setup camera.", e)
        }
    }

    private fun createSurfaceTexture(): SurfaceTexture? {
        var surfaceTexture: SurfaceTexture?
        synchronized(mObject) {
            if (mSurfaceTexture != null) {
                mSurfaceTexture?.release()
                mSurfaceTexture = null
            }
            mSurfaceTexture = SurfaceTexture(0)
            surfaceTexture = mSurfaceTexture
        }
        return surfaceTexture
    }

    private fun releaseSurface() {
        synchronized(mObject) {
            if (mSurfaceTexture != null) {
                try {
                    val method =
                        mSurfaceTexture?.javaClass?.getMethod("release")
                    method?.invoke(mSurfaceTexture)
                } catch (th: Throwable) {
                    throw RuntimeException(
                        "Interrupted while trying " +
                                "to get surface method.", th.cause
                    )
                } finally {
                    mSurfaceTexture = null
                }
            }
        }
    }

    private fun getSmallestPreviewSize(parameters: Camera.Parameters?): Camera.Size? {
        var result: Camera.Size? = null
        if (parameters != null) {
            var nSize: Camera.Size?
            for (size in parameters.supportedPreviewSizes) {
                nSize = size
                if (nSize != null) {
                    val i2 = nSize.width + nSize.height
                    if (result != null) {
                        if (i2 < result.width + result.height) {
                            //TODO
                            //No need to any task here
                        }
                    }
                    result = nSize
                }
                nSize = result
                result = nSize
            }
        }
        return result
    }

    private val mPreviewCallback = PreviewCallback { data, cam -> //pixel calculation done here
        val size = cam.parameters.previewSize
        if (data != null && size != null) {
            mPreviewListener?.onPreviewData(data)
            if (mProcessingSupport != null) {
                val value =
                    mProcessingSupport.yuvSpToRedAvg(data.clone(), size.width, size.height)
                mPreviewListener?.onPreviewCount(value)
            }
            cam.addCallbackBuffer(data)
        }
    }

    init {
        Log.e("TAG", "CameraOld Run")
        this.mContext = mContext
        val powerManager =
            mContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK,
            "DoNotDimScreen " + System.currentTimeMillis()
        )
        this.mProcessingSupport = mProcessingSupport
    }
}