package com.group4.herbs_and_friends_app.ui.manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.group4.herbs_and_friends_app.R;

public class HProductManageContainerFragment extends Fragment {
    public HProductManageContainerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_product_manage_container, container, false);
    }
}
