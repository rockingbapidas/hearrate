package com.vantagecircle.heartrate.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

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
