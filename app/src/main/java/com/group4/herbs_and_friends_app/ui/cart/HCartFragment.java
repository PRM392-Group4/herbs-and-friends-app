package com.group4.herbs_and_friends_app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group4.herbs_and_friends_app.databinding.FragmentHCartBinding;
import com.group4.herbs_and_friends_app.ui.cart.adapter.HCartRecycleViewAdapter;
import com.group4.herbs_and_friends_app.ui.cart.viewholder.IViewHolderListeners;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCartFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    private FragmentHCartBinding binding;
    private HCartRecycleViewAdapter hCartRecycleViewAdapter;
    private HCartVM hCartVM;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup View Model
        hCartVM = new ViewModelProvider(this).get(HCartVM.class);

        // Setup Adapters
        setupAdapterAndRecyclerView();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Methods
    // ================================

    public void setupAdapterAndRecyclerView() {
        hCartRecycleViewAdapter = new HCartRecycleViewAdapter(new IViewHolderListeners() {

            @Override
            public void onItemModifyQuantity(String cartItemId, int quantity) {
                hCartVM.modifyQuantity(cartItemId, quantity);
            }
        });
        binding.rvCart.setAdapter(hCartRecycleViewAdapter);
    }


}