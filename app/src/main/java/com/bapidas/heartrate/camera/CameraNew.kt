package com.bapidas.heartrate.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.*
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bapidas.heartrate.processing.ProcessingSupport
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by bapidas on 12/10/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class CameraNew(
    mContext: Context,
    processingSupport: ProcessingSupport?
) : CameraSupport {
    private val mContext: Context
    private var mCameraDevice: CameraDevice? = null
    private val mCameraManager: CameraManager
    private var mCaptureSession: CameraCaptureSession? = null
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private val mCameraOpenCloseLock =
        Semaphore(1)
    private var mImageReader: ImageReader? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mCameraId: String = ""
    private var mFlashSupported = false
    private var mWakeLock: WakeLock? = null
    private var mPreviewListener: PreviewListener? = null
    private val processingSupport: ProcessingSupport?
    override fun open(): CameraSupport {
        try {
            startBackgroundThread()
            setCameraOutputs()
            if (!mCameraOpenCloseLock.tryAcquire(
                    2500,
                    TimeUnit.MILLISECONDS
                )
            ) {
                throw RuntimeException("Time out waiting to acquire lock while camera opening.")
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                mWakeLock?.acquire(10 * 60 * 1000L)
                mCameraManager.openCamera(mCameraId, mStateCallback, mBackgroundHandler)
            } else {
                throw RuntimeException("Permission failed while access camera.")
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to camera opening.", e)
        }
        return this
    }

    override fun close() {
        try {
            mCameraOpenCloseLock.acquire()
            if (null != mCaptureSession) {
                mCaptureSession?.close()
                mCaptureSession = null
            }
            if (null != mCameraDevice) {
                mCameraDevice?.close()
                mCameraDevice = null
            }
            if (null != mImageReader) {
                mImageReader?.close()
                mImageReader = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(
                "Interrupted while trying to release and close camera.",
                e
            )
        } finally {
            mWakeLock?.release()
            mCameraOpenCloseLock.release()
        }
        stopBackgroundThread()
    }

    override fun addOnPreviewListener(callBack: PreviewListener) {
        mPreviewListener = callBack
    }

    override val isCameraInUse: Boolean
        get() = mCameraDevice != null

    private fun setCameraOutputs() {
        try {
            for (cameraId in mCameraManager.cameraIdList) {
                val mCharacteristics = mCameraManager
                    .getCameraCharacteristics(cameraId)
                val facing = mCharacteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }
                mImageReader = ImageReader.newInstance(
                    176, 144,
                    ImageFormat.YUV_420_888, 5
                )
                mImageReader?.setOnImageAvailableListener(
                    mOnImageAvailableListener,
                    mBackgroundHandler
                )
                val available =
                    mCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                mFlashSupported = available ?: false
                mCameraId = cameraId
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val imageSurface = mImageReader?.surface
            val surfaceList: MutableList<Surface?> =
                ArrayList()
            mPreviewRequestBuilder =
                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            imageSurface?.let { mPreviewRequestBuilder?.addTarget(it) }
            surfaceList.add(imageSurface)
            mCameraDevice?.createCaptureSession(surfaceList, mSessionStateCallBack, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper ?: Looper.getMainLooper())
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder?) {
        if (mFlashSupported) {
            requestBuilder?.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
        }
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }
    }
    private val mSessionStateCallBack: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                try {
                    if (null == mCameraDevice) {
                        return
                    }
                    mCaptureSession = session
                    setAutoFlash(mPreviewRequestBuilder)
                    val mPreviewRequest = mPreviewRequestBuilder?.build()
                    mPreviewRequest?.let {
                        mCaptureSession?.setRepeatingRequest(
                            it,
                            null,
                            mBackgroundHandler
                        )
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }
    private val mOnImageAvailableListener =
        OnImageAvailableListener { reader -> //pixel calculation done here
            val image = reader.acquireLatestImage()
            if (image != null) {
                if (processingSupport != null && mPreviewListener != null) {
                    val data = processingSupport.yuvToNv(image)
                    mPreviewListener?.onPreviewData(data)
                    val value = processingSupport.yuvSpToRedAvg(
                        data.clone(),
                        image.width,
                        image.height
                    )
                    mPreviewListener?.onPreviewCount(value)
                }
                image.close()
            }
        }

    init {
        Log.e("TAG", "CameraNew Run")
        this.mContext = mContext
        mCameraManager =
            mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        this.processingSupport = processingSupport
        val powerManager =
            mContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "DoNotDimScreen " + System.currentTimeMillis()
        )
    }
}