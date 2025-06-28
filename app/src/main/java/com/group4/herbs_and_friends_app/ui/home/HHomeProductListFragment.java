package com.group4.herbs_and_friends_app.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeProductListBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;
import com.group4.herbs_and_friends_app.utils.GridRowSpacingDecoration;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeProductListFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHHomeProductListBinding binding;
    private HHomeVM hHomeVM;
    private ProductAdapter adapter;

    // ================================
    // === Lifecycle
    // ================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHHomeProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar();
        setProductAdapter();

        hHomeVM = new ViewModelProvider(this).get(HHomeVM.class);
        fetchData();

        binding.btnProductFilter.setOnClickListener(v -> {
            // TODO: Display sheet/drawer of categories, fetch data after applying filter
        });

        binding.btnPriceSort.setOnClickListener(v -> {
            // TODO: Display sort options, fetch data after applying filter
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Methods
    // ================================

    private void setActionBar() {
        ViewHActionbarBinding actionbarBinding = binding.includeActionbarProductList;
        actionbarBinding.btnBack.setVisibility(View.VISIBLE);

        // Navigate back to home fragment
        actionbarBinding.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        actionbarBinding.tilSearch.setEndIconOnClickListener(v -> {
            //TODO: Handle product search
        });
    }

    private void setProductAdapter() {
        binding.productRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        binding.productRv.addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 2));

        adapter = new ProductAdapter(requireContext(), productId -> {
            // Set productId into arguments and navigate to product detail fragment
            NavHostFragment.findNavController(this).navigate(
                    HHomeProductListFragmentDirections.productListToProductDetail(productId)
            );
        });

        binding.productRv.setAdapter(adapter);
    }

    private void fetchData() {
        hHomeVM.getAllProductsLive().observe(getViewLifecycleOwner(), products -> {
            adapter.setProductList(products);
        });
    }
}