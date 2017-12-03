package com.vantagecircle.heartrate.activity.presenter;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.adapter.PagerAdapter;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.fragment.ui.HistoryFragment;

/**
 * Created by bapidas on 10/10/17.
 */

public class HeartActivityPresenter {
    private final String TAG = HeartActivityPresenter.class.getSimpleName();
    private HeartActivity mHeartActivity;
    private PagerAdapter mPagerAdapter;
    private AlertDialog mAlertDialog;

    public HeartActivityPresenter(HeartActivity mHeartActivity) {
        this.mHeartActivity = mHeartActivity;
    }

    public void setup() {
        initToolBar();
        initTabPager();
    }

    private void initToolBar() {
        mHeartActivity.setSupportActionBar(mHeartActivity.mToolBar);
        mHeartActivity.mActionBar = mHeartActivity.getSupportActionBar();
        if (mHeartActivity.mActionBar != null) {
            mHeartActivity.mActionBar.setTitle("Heart Rate");
            mHeartActivity.mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initTabPager() {
        mPagerAdapter = new PagerAdapter(mHeartActivity.getSupportFragmentManager());
        mPagerAdapter.addFragment(HeartFragment.newInstance(), mHeartActivity.getResources().getString(R.string.calculate));
        mPagerAdapter.addFragment(HistoryFragment.newInstance(), mHeartActivity.getResources().getString(R.string.history));
        mHeartActivity.mViewPager.setAdapter(mPagerAdapter);
        mHeartActivity.mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mHeartActivity.mTabLayout));
        mHeartActivity.mTabLayout.setupWithViewPager(mHeartActivity.mViewPager);
        mHeartActivity.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mHeartActivity.mViewPager.setCurrentItem(tab.getPosition());
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

    public void showHintDialog() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mHeartActivity);
            View mView = LayoutInflater.from(mHeartActivity).inflate(R.layout.hint_diaog, null);
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
