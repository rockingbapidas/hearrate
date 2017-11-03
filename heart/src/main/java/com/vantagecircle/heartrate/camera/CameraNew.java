package com.vantagecircle.heartrate.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;

import com.vantagecircle.heartrate.processing.ProcessingSupport;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by bapidas on 12/10/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraNew implements CameraSupport {
    private Context mContext;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCaptureSession;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private ImageReader mImageReader;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private String mCameraId;
    private boolean mFlashSupported;
    private CameraCallBack mCameraCallBack;
    private ProcessingSupport processingSupport;
    private PowerManager.WakeLock mWakeLock;

    public CameraNew(Context context, ProcessingSupport processingSupport) {
        Log.e("TAG", "CameraNew Run");
        this.mContext = context;
        this.mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.processingSupport = processingSupport;
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            this.mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        }
    }

    @Override
    public CameraSupport open() {
        try {
            startBackgroundThread();
            setCameraOutputs();
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to acquire lock while camera opening.");
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                this.mWakeLock.acquire(10 * 60 * 1000L);
                this.mCameraManager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
            } else {
                throw new RuntimeException("Permission failed while access camera.");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to camera opening.", e);
        }
        return this;
    }

    @Override
    public void close() {
        try {
            this.mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                this.mCaptureSession.close();
                this.mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                this.mCameraDevice.close();
                this.mCameraDevice = null;
            }
            if (null != mImageReader) {
                this.mImageReader.close();
                this.mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to release and close camera.", e);
        } finally {
            this.mWakeLock.release();
            this.mCameraOpenCloseLock.release();
        }
        stopBackgroundThread();
    }

    @Override
    public void setPreviewCallBack(CameraCallBack callBack) {
        this.mCameraCallBack = callBack;
    }

    @Override
    public boolean isCameraInUse() {
        return mCameraDevice != null;
    }

    private void setCameraOutputs() {
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics mCharacteristics = mCameraManager
                        .getCameraCharacteristics(cameraId);
                Integer facing = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                this.mImageReader = ImageReader.newInstance(176, 144,
                        ImageFormat.YUV_420_888, 5);
                this.mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
                        mBackgroundHandler);
                Boolean available = mCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                this.mFlashSupported = available == null ? false : available;
                this.mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        try {
            Surface imageSurface = mImageReader.getSurface();
            List<Surface> surfaceList = new ArrayList<>();
            this.mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            this.mPreviewRequestBuilder.addTarget(imageSurface);
            surfaceList.add(imageSurface);
            this.mCameraDevice.createCaptureSession(surfaceList, mSessionStateCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        this.mBackgroundThread = new HandlerThread("CameraBackground");
        this.mBackgroundThread.start();
        this.mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        this.mBackgroundThread.quitSafely();
        try {
            this.mBackgroundThread.join();
            this.mBackgroundThread = null;
            this.mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (this.mFlashSupported) {
            requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        }
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    private CameraCaptureSession.StateCallback mSessionStateCallBack = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                if (null == mCameraDevice) {
                    return;
                }
                mCaptureSession = session;
                setAutoFlash(mPreviewRequestBuilder);
                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                        null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            //pixel calculation done here
            Image image = reader.acquireLatestImage();
            if (image != null) {
                byte[] data = processingSupport.YUV_420_888toNV21(image);
                int value = processingSupport.YUV420SPtoRedAvg(data, image.getWidth(), image.getHeight());
                mCameraCallBack.onFrameCallback(value);
                image.close();
            }
        }
    };
}
