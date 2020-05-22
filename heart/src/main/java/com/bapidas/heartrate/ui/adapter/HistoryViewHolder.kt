package com.bapidas.heartrate.ui.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by SiD on 11/11/2017.
 */
class HistoryViewHolder(rowView: View) :
    RecyclerView.ViewHolder(rowView) {
    val binding: ViewDataBinding? = DataBindingUtil.bind(rowView)
}