package com.vantagecircle.heartrate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.model.HistoryModel;

import java.util.ArrayList;

/**
 * Created by SiD on 11/11/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    private ArrayList<HistoryModel> arrayList;

    public HistoryAdapter(ArrayList<HistoryModel> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history_item, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        HistoryModel model = arrayList.get(position);
        holder.getBinding().setVariable(BR.history, model.getHeartRate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
