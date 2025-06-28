package com.group4.herbs_and_friends_app.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.ui.home.adapter.HomeCategoryAdapter;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;
import com.group4.herbs_and_friends_app.utils.GridRowSpacingDecoration;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHHomeBinding binding;
    private HHomeVM hHomeVM;
    private HomeCategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    // ================================
    // === Lifecycle
    // ================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar();
        setCategoryAdapter();
        setProductAdapter();

        hHomeVM = new ViewModelProvider(this).get(HHomeVM.class);
        fetchData();

        // Navigate to product list when 'See More' is clicked
        binding.homeProductSeeMore.setOnClickListener(v -> {
            NavHostFragment.findNavController(HHomeFragment.this)
                    .navigate(R.id.home_to_productList);
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
        ViewHActionbarBinding actionbarBinding = binding.includeActionbarHome;
        actionbarBinding.btnBack.setVisibility(View.GONE);
        actionbarBinding.tilSearch.setEndIconOnClickListener(v -> {
            Editable editable = actionbarBinding.etSearch.getText();
            if(editable == null) return;

            String search = editable.toString().trim();
            if(search.isEmpty()) return;

            // Set search into arguments and navigate to product list fragment
            // TODO: Replace with shared VM filters
            NavHostFragment.findNavController(this).navigate(
                    HHomeFragmentDirections.homeToProductList(null, search)
            );
        });
    }

    private void setCategoryAdapter() {
        binding.homeCategoryRv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        binding.homeCategoryRv.addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 3));

        categoryAdapter = new HomeCategoryAdapter(requireContext(), categoryId -> {
            // Set categoryId into arguments and navigate to product list fragment
            // TODO: Replace with shared VM filters
            NavHostFragment.findNavController(this).navigate(
                    HHomeFragmentDirections.homeToProductList(categoryId, null)
            );
        });

        binding.homeCategoryRv.setAdapter(categoryAdapter);
    }

    private void setProductAdapter() {
        binding.homeProductRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        binding.homeProductRv.addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 2));

        productAdapter = new ProductAdapter(requireContext(), productId -> {
            // Set productId into arguments and navigate to product detail fragment
            NavHostFragment.findNavController(this).navigate(
                    HHomeFragmentDirections.homeToProductDetail(productId)
            );
        });

        binding.homeProductRv.setAdapter(productAdapter);
    }

    private void fetchData() {
        hHomeVM.getParentCategoriesLive().observe(getViewLifecycleOwner(), homeCategories -> {
            categoryAdapter.setCategoryList(homeCategories);
        });

        hHomeVM.getAllProductsLive().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProductList(products);
        });
    }
}
