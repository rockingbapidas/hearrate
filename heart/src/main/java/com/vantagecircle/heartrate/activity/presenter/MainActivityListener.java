package com.vantagecircle.heartrate.activity.presenter;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

/**
 * Created by SiD on 12/3/2017.
 */

public interface MainActivityListener {
    void getToolbar(Toolbar mToolbar);
    void getTabLayout(TabLayout mTabLayout);
    void getViewPager(ViewPager mViewPager);
}
