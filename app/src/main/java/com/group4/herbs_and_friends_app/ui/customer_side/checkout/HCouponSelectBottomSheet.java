package com.group4.herbs_and_friends_app.ui.customer_side.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.group4.herbs_and_friends_app.databinding.BottomSheetHCouponSelectBinding;
import com.group4.herbs_and_friends_app.ui.admin_side.coupon_management.adapters.HCouponSelectionAdapter;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCouponSelectBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetHCouponSelectBinding binding;
    private HCouponSelectionAdapter adapter;
    private HCouponSelectVM viewModel;
    private HCouponSelectionAdapter.IOnCouponSelectedListener listener;

    public void setOnCouponSelectedListener(HCouponSelectionAdapter.IOnCouponSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetHCouponSelectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HCouponSelectVM.class);

        adapter = new HCouponSelectionAdapter(new ArrayList<>(), coupon -> {
            if (listener != null) {
                listener.onCouponSelected(coupon);
            }
            dismiss();
        });

        binding.rvCouponSelect.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCouponSelect.setAdapter(adapter);

        viewModel.getCouponListLive().observe(getViewLifecycleOwner(), coupons -> {
            adapter.setCouponList(coupons);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 