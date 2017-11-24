package com.vantagecircle.heartrate.fragment.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
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
        askPermission();
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

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ToolsUtils.getInstance().isHasPermissions(this.getActivity(),
                    Manifest.permission.CAMERA,
                    Manifest.permission.BODY_SENSORS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.VIBRATE)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.BODY_SENSORS,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.VIBRATE}, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission(requestCode, grantResults);
    }

    private void checkPermission(int requestCode, int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this.getActivity(), "You have to give permission to access this window", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeartRateLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.heart_rate_layout, container, false);
        mPulseSupport.setSurfaceHolder(mHeartRateLayoutBinding.surfaceView.getHolder());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getActivity().getWindow().addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        }
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
        mHeartFragmentPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHeartFragmentPresenter.stop();
    }
}
