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
    private HeartActivity heartActivity;
    private PagerAdapter mPagerAdapter;
    private AlertDialog mAlertDialog;

    public HeartActivityPresenter(HeartActivity heartActivity) {
        this.heartActivity = heartActivity;
    }

    public void setup() {
        initToolBar();
        initTabPager();
    }

    private void initToolBar() {
        heartActivity.setSupportActionBar(heartActivity.mToolBar);
        heartActivity.mActionBar = heartActivity.getSupportActionBar();
        if (heartActivity.mActionBar != null) {
            heartActivity.mActionBar.setTitle("Heart Rate");
            heartActivity.mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initTabPager() {
        mPagerAdapter = new PagerAdapter(heartActivity.getSupportFragmentManager());
        mPagerAdapter.addFragment(HeartFragment.newInstance(),
                heartActivity.getResources().getString(R.string.calculate));
        mPagerAdapter.addFragment(HistoryFragment.newInstance(),
                heartActivity.getResources().getString(R.string.history));
        heartActivity.mViewPager.setAdapter(mPagerAdapter);
        heartActivity.mViewPager.addOnPageChangeListener(new TabLayout
                .TabLayoutOnPageChangeListener(heartActivity.mTabLayout));
        heartActivity.mTabLayout.setupWithViewPager(heartActivity.mViewPager);
        heartActivity.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                heartActivity.mViewPager.setCurrentItem(tab.getPosition());
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(heartActivity);
            View mView = LayoutInflater.from(heartActivity).inflate(R.layout.hint_diaog, null);
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
