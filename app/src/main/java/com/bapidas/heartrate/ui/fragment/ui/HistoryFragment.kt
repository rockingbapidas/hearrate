package com.bapidas.heartrate.ui.fragment.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bapidas.heartrate.HeartApplication.Companion.get
import com.bapidas.heartrate.R
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.databinding.HistoryLayoutBinding
import com.bapidas.heartrate.di.component.DaggerFragmentComponent
import com.bapidas.heartrate.di.component.FragmentComponent
import com.bapidas.heartrate.di.module.FragmentModule
import com.bapidas.heartrate.ui.base.BaseFragment
import com.bapidas.heartrate.ui.fragment.presenter.HistoryFragmentPresenter
import javax.inject.Inject

class HistoryFragment : BaseFragment() {
    private var mUnBinder: HistoryLayoutBinding? = null
    private var mFragmentComponent: FragmentComponent? = null
    private var mHistoryFragmentPresenter: HistoryFragmentPresenter? = null

    @Inject
    lateinit var mDataManager: DataManager

    override fun setFragmentComponent(context: Context) {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(FragmentModule(this))
                .appComponent(get(context).appComponent)
                .build()
        }
        mFragmentComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUnBinder = DataBindingUtil.inflate(
            inflater, R.layout.history_layout,
            container, false
        )
        return mUnBinder.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun init() {
        mHistoryFragmentPresenter = HistoryFragmentPresenter(
            activity, mDataManager, mUnBinder.recyclerView
        )
        mHistoryFragmentPresenter?.initialize()
    }

    override fun onResume() {
        super.onResume()
        if (mHistoryFragmentPresenter != null) {
            mHistoryFragmentPresenter?.initialize()
        }
    }

    companion object {
        private val TAG = HistoryFragment::class.java.simpleName
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }
}