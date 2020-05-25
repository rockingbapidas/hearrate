package com.bapidas.heartrate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapidas.heartrate.BR
import com.bapidas.heartrate.R
import com.bapidas.heartrate.data.model.HistoryModel
import java.util.*

/**
 * Created by SiD on 11/11/2017.
 */
class HistoryAdapter(private val arrayList: ArrayList<HistoryModel>) :
    RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_history_item, parent, false)
        return HistoryViewHolder(v)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val model = arrayList[position]
        holder.binding?.setVariable(BR.history, model)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}