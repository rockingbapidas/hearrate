package com.bapidas.heartrate.ui.fragment.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bapidas.heartrate.HeartApplication.Companion.get
import com.bapidas.heartrate.R
import com.bapidas.heartrate.core.HeartRate
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.databinding.HeartRateLayoutBinding
import com.bapidas.heartrate.di.component.DaggerFragmentComponent
import com.bapidas.heartrate.di.component.FragmentComponent
import com.bapidas.heartrate.di.module.FragmentModule
import com.bapidas.heartrate.ui.base.BaseFragment
import com.bapidas.heartrate.ui.fragment.presenter.HeartFragmentPresenter
import javax.inject.Inject

class HeartFragment : BaseFragment() {
    @Inject
    lateinit var heartRate: HeartRate

    @Inject
    lateinit var mDataManager: DataManager

    private var mFragmentComponent: FragmentComponent? = null
    private var mHeartRateLayoutBinding: HeartRateLayoutBinding? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mHeartRateLayoutBinding = DataBindingUtil.inflate(
            inflater, R.layout.heart_rate_layout,
            container, false
        )
        return mHeartRateLayoutBinding?.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun setFragmentComponent(context: Context) {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(FragmentModule(this))
                .appComponent(get(context).appComponent)
                .build()
        }
        mFragmentComponent?.inject(this)
    }

    override fun init() {
        val mHeartFragmentPresenter = HeartFragmentPresenter(
            activity,
            heartRate.heartSupport, mDataManager
        )
        mHeartRateLayoutBinding?.heartPresenter = mHeartFragmentPresenter
    }

    companion object {
        private val TAG = HeartFragment::class.java.simpleName
        fun newInstance(): HeartFragment {
            return HeartFragment()
        }
    }
}