package com.vantagecircle.heartrate.activity.presenter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.ui.MainActivity;
import com.vantagecircle.heartrate.adapter.PagerAdapter;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.fragment.ui.HistoryFragment;

/**
 * Created by bapidas on 10/10/17.
 */

public class MainActivityPresenter extends BaseObservable implements MainActivityListener {
    private static final String TAG = MainActivityPresenter.class.getSimpleName();
    private MainActivity mMainActivity;
    private ActionBar mActionBar;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private AlertDialog mAlertDialog;
    private PagerAdapter mPagerAdapter;
    private MainActivityListener mainActivityListener;

    public MainActivityPresenter(MainActivity mMainActivity) {
        Log.d(TAG, "MainActivityPresenter");
        this.mMainActivity = mMainActivity;
        setMainActivityListener(this);
    }

    public void init() {
        mPagerAdapter = new PagerAdapter(mMainActivity.getSupportFragmentManager());
        mPagerAdapter.addFragment(HeartFragment.newInstance(), mMainActivity.getResources().getString(R.string.calculate));
        mPagerAdapter.addFragment(HistoryFragment.newInstance(), mMainActivity.getResources().getString(R.string.history));
    }

    @Bindable
    public MainActivityListener getMainActivityListener() {
        return mainActivityListener;
    }

    private void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
        notifyPropertyChanged(BR.mainActivityListener);
    }

    @BindingAdapter("setToolbar")
    public static void setToolbar(Toolbar mToolbar, MainActivityListener mainActivityListener) {
        Log.d(TAG, "setToolbar");
        if (mToolbar != null) {
            mainActivityListener.getToolbar(mToolbar);
        }
    }

    @BindingAdapter("setTabLayout")
    public static void setTabLayout(TabLayout mTabLayout, MainActivityListener mainActivityListener) {
        Log.d(TAG, "setTabLayout");
        if (mTabLayout != null) {
            mainActivityListener.getTabLayout(mTabLayout);
        }
    }

    @BindingAdapter("setViewPager")
    public static void setViewPager(ViewPager mViewPager, MainActivityListener mainActivityListener) {
        Log.d(TAG, "setViewPager");
        if (mViewPager != null) {
            mainActivityListener.getViewPager(mViewPager);
        }
    }

    @Override
    public void getToolbar(Toolbar mToolbar) {
        this.mToolbar = mToolbar;
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle("Heart Rate");
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void getTabLayout(TabLayout mTabLayout) {
        this.mTabLayout = mTabLayout;
        this.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mPagerAdapter.getItem(tab.getPosition()).onResume();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mPagerAdapter.getItem(tab.getPosition()).onPause();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void getViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
        this.mViewPager.setAdapter(mPagerAdapter);
        this.mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.mTabLayout));
        this.mTabLayout.setupWithViewPager(this.mViewPager);
    }

    public void showHintDialog() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mMainActivity);
            View mView = LayoutInflater.from(mMainActivity).inflate(R.layout.hint_diaog, null);
            Button btnOk = mView.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            dialog.setView(mView);
            mAlertDialog = dialog.create();
            mAlertDialog.show();
        }
    }
}
