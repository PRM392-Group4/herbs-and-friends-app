package com.group4.herbs_and_friends_app.ui.admin_side.profile_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group4.herbs_and_friends_app.databinding.FragmentHProfileManagementBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HProfileManagementFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHProfileManagementBinding binding;
    private HProfileManagementVM hProfileManagementVM;

    // ================================
    // === Lifecycle
    // ================================
    public static HProfileManagementFragment newInstance() {
        return new HProfileManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHProfileManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hProfileManagementVM = new ViewModelProvider(this).get(HProfileManagementVM.class);
        // TODO: Observe LiveData from viewModel and bind to UI components
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Methods
    // ================================
    // Add additional private/helper methods here
}
