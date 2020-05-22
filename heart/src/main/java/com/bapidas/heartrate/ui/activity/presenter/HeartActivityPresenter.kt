package com.bapidas.heartrate.ui.activity.presenter

import com.bapidas.heartrate.R
import com.bapidas.heartrate.ui.activity.ui.HeartActivity
import com.bapidas.heartrate.ui.adapter.PagerAdapter
import com.bapidas.heartrate.ui.fragment.ui.HeartFragment
import com.bapidas.heartrate.ui.fragment.ui.HistoryFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

/**
 * Created by bapidas on 10/10/17.
 */
class HeartActivityPresenter(private val heartActivity: HeartActivity) {
    private val TAG = HeartActivityPresenter::class.java.simpleName
    private var mPagerAdapter: PagerAdapter? = null
    fun setup() {
        initToolBar()
        initTabPager()
    }

    private fun initToolBar() {
        heartActivity.setSupportActionBar(heartActivity.mBinding?.toolbar)
        heartActivity.mActionBar = heartActivity.supportActionBar
        if (heartActivity.mActionBar != null) {
            heartActivity.mActionBar?.title = "Heart Rate"
            heartActivity.mActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun initTabPager() {
        mPagerAdapter =
            PagerAdapter(heartActivity.supportFragmentManager)
        mPagerAdapter?.addFragment(
            HeartFragment.newInstance(),
            heartActivity.resources.getString(R.string.calculate)
        )
        mPagerAdapter?.addFragment(
            HistoryFragment.newInstance(),
            heartActivity.resources.getString(R.string.history)
        )
        heartActivity.mBinding?.viewpager?.adapter = mPagerAdapter
        heartActivity.mBinding?.viewpager?.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(
                heartActivity.mBinding?.tabLayout
            )
        )
        heartActivity.mBinding?.tabLayout?.setupWithViewPager(heartActivity.mBinding?.viewpager)
        heartActivity.mBinding?.tabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                heartActivity.mBinding?.viewpager?.currentItem = tab.position
                mPagerAdapter?.getItem(tab.position)?.onResume()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}