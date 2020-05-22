package com.bapidas.heartrate.ui.fragment.presenter

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.ui.adapter.HistoryAdapter

/**
 * Created by bapidas on 08/11/17.
 */
class HistoryFragmentPresenter(
    private val mContext: FragmentActivity?,
    private val mDataManger: DataManager,
    private val mRecyclerView: RecyclerView?
) {
    fun initialize() {
        val mLinearLayoutManager = LinearLayoutManager(mContext)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView?.layoutManager = mLinearLayoutManager
        mRecyclerView?.scrollToPosition(0)
        val mArrayList = mDataManger.history
        if (mArrayList.size > 0) {
            val mHistoryAdapter = HistoryAdapter(mArrayList)
            mRecyclerView?.adapter = mHistoryAdapter
        }
    }

    companion object {
        private val TAG = HistoryFragmentPresenter::class.java.simpleName
    }

}