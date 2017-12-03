package com.vantagecircle.heartrate.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SiD on 11/11/2017.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;

    HistoryViewHolder(View rowView) {
        super(rowView);
        binding = DataBindingUtil.bind(rowView);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
