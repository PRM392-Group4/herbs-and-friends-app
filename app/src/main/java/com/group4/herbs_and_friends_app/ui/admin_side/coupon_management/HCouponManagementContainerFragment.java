package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group4.herbs_and_friends_app.R;

public class HCouponManagementContainerFragment extends Fragment {

    public HCouponManagementContainerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_coupon_management_container, container, false);
    }
}