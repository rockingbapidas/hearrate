package com.vantagecircle.heartrate.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by bapidas on 08/11/17.
 */

public abstract class BaseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentComponent();
    }

    protected abstract void setFragmentComponent();

    protected abstract void init();
}
