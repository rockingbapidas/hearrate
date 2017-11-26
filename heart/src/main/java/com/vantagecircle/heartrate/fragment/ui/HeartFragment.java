package com.vantagecircle.heartrate.fragment.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.component.DaggerFragmentComponent;
import com.vantagecircle.heartrate.core.PulseSupport;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.databinding.HeartRateLayoutBinding;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.component.FragmentComponent;
import com.vantagecircle.heartrate.module.FragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;
import com.vantagecircle.heartrate.utils.Constant;
import com.vantagecircle.heartrate.utils.ToolsUtils;

import javax.inject.Inject;

public class HeartFragment extends BaseFragment {
    private static final String TAG = HeartFragment.class.getSimpleName();
    @Inject
    PulseSupport mPulseSupport;
    @Inject
    DataManager mDataManager;

    private FragmentComponent mFragmentComponent;
    private HeartRateLayoutBinding mHeartRateLayoutBinding;
    private HeartFragmentPresenter mHeartFragmentPresenter;

    public static HeartFragment newInstance() {
        return new HeartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getActivity().getWindow().addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        }
    }

    @Override
    protected void setFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                    .fragmentModule(new FragmentModule(this))
                    .appComponent(HeartApplication.get(this.getContext()).getAppComponent())
                    .build();
        }
        mFragmentComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeartRateLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.heart_rate_layout, container, false);
        return mHeartRateLayoutBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mHeartFragmentPresenter = new HeartFragmentPresenter(this.getActivity(), mPulseSupport, mDataManager);
        mHeartRateLayoutBinding.setHeartPresenter(mHeartFragmentPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        askPermission();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ToolsUtils.getInstance().isHasPermissions(this.getActivity(), Manifest.permission.CAMERA,
                    Manifest.permission.BODY_SENSORS, Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.BODY_SENSORS,
                        Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE},Constant.REQUEST_ALL_PERMISSION);
            } else {
                mHeartFragmentPresenter.start(mHeartRateLayoutBinding.surfaceView.getHolder());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission(requestCode, grantResults);
    }

    private void checkPermission(int requestCode, int[] grantResults) {
        if (requestCode == Constant.REQUEST_ALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this.getActivity(), "Camera need permission", Toast.LENGTH_SHORT).show();
            } else {
                mHeartFragmentPresenter.start(mHeartRateLayoutBinding.surfaceView.getHolder());
            }
        }
    }
}
