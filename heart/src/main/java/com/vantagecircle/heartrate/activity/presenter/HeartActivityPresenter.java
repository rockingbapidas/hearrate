package com.vantagecircle.heartrate.activity.presenter;

import com.google.android.material.tabs.TabLayout;
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

    public HeartActivityPresenter(HeartActivity heartActivity) {
        this.heartActivity = heartActivity;
    }

    public void setup() {
        initToolBar();
        initTabPager();
    }

    private void initToolBar() {
        heartActivity.setSupportActionBar(heartActivity.mBinding.toolbar);
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
        heartActivity.mBinding.viewpager.setAdapter(mPagerAdapter);
        heartActivity.mBinding.viewpager.addOnPageChangeListener(new TabLayout
                .TabLayoutOnPageChangeListener(heartActivity.mBinding.tabLayout));
        heartActivity.mBinding.tabLayout.setupWithViewPager(heartActivity.mBinding.viewpager);
        heartActivity.mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                heartActivity.mBinding.viewpager.setCurrentItem(tab.getPosition());
                mPagerAdapter.getItem(tab.getPosition()).onResume();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
