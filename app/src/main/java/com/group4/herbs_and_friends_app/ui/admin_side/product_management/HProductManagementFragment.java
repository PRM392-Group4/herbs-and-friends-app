package com.group4.herbs_and_friends_app.ui.admin_side.product_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group4.herbs_and_friends_app.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HProductManagementFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    private HProductManagementVM hProductManagementVM;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_product_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hProductManagementVM = new ViewModelProvider(this).get(HProductManagementVM.class);
        // TODO: Observe LiveData from ViewModel and bind to UI components
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // ================================
    // === Methods
    // ================================
}
