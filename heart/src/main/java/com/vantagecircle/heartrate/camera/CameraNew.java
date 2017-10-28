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

    public CameraNew(Context context, ProcessingSupport processingSupport) {
        Log.e("TAG", "CameraNew Run");
        this.mContext = context;
        this.mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.processingSupport = processingSupport;
    }

    @Override
    public CameraSupport open() {
        try {
            startBackgroundThread();
            setCameraOutputs();
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                mCameraManager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
            } else {
                throw new RuntimeException("Access camera need permission.");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
        return this;
    }

    @Override
    public void close() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
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
                mImageReader = ImageReader.newInstance(176, 144, ImageFormat.NV21, 5);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
                Boolean available = mCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;
                mCameraId = cameraId;
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
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(imageSurface);
            surfaceList.add(imageSurface);
            mCameraDevice.createCaptureSession(surfaceList, mSessionStateCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
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
                mCaptureSession.setRepeatingRequest(mPreviewRequest, null, mBackgroundHandler);
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
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);

                //byte[] data = processingSupport.YUV_420_888toNV21(image);

                int value = processingSupport.YUV420SPtoRedAvg(data, image.getWidth(), image.getHeight());
                mCameraCallBack.onFrameCallback(value);
                image.close();
            }
        }
    };
}
