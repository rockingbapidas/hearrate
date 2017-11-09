package com.vantagecircle.heartrate.activity.presenter;

import android.support.design.widget.TabLayout;

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
        PagerAdapter adapter = new PagerAdapter(heartActivity
                .getSupportFragmentManager());
        adapter.addFragment(HeartFragment.newInstance(),
                heartActivity.getResources().getString(R.string.calculate));
        adapter.addFragment(HistoryFragment.newInstance(),
                heartActivity.getResources().getString(R.string.history));
        heartActivity.mViewPager.setAdapter(adapter);
        heartActivity.mViewPager.addOnPageChangeListener(new TabLayout
                .TabLayoutOnPageChangeListener(heartActivity.mTabLayout));
        heartActivity.mTabLayout.setupWithViewPager(heartActivity.mViewPager);
        heartActivity.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                heartActivity.mViewPager.setCurrentItem(tab.getPosition());
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
