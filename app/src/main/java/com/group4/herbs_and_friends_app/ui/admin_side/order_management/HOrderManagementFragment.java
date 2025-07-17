package com.group4.herbs_and_friends_app.ui.admin_side.order_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group4.herbs_and_friends_app.databinding.FragmentHOrderManagementBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HOrderManagementFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    private FragmentHOrderManagementBinding binding;
    private HOrderManagementVM hOrderManagementVM;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHOrderManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hOrderManagementVM = new ViewModelProvider(this).get(HOrderManagementVM.class);
        // TODO: observe ViewModel LiveData and bind to UI
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Methods
    // ================================
}