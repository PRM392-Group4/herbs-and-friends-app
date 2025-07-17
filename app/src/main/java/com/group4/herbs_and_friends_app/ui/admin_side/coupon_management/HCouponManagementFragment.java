package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group4.herbs_and_friends_app.databinding.FragmentHCouponManagementBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCouponManagementFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    private FragmentHCouponManagementBinding binding;
    private HCouponManagementVM hCouponManagementVM;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHCouponManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // ================================
    // === Methods
    // ================================

}