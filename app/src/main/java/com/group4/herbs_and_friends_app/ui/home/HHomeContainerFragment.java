package com.group4.herbs_and_friends_app.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group4.herbs_and_friends_app.R;

public class HHomeContainerFragment extends Fragment {

    public HHomeContainerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_home_container, container, false);
    }
}