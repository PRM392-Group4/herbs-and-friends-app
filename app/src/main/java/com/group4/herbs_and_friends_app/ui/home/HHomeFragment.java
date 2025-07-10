package com.group4.herbs_and_friends_app.ui.home;

import static com.group4.herbs_and_friends_app.utils.AppCts.VIEW_TYPE_LISTING;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.ui.home.adapter.HomeCategoryAdapter;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;
import com.group4.herbs_and_friends_app.utils.GridRowSpacingDecoration;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeFragment extends Fragment implements ProductAdapter.ProductActionListener {

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
        hHomeVM = new ViewModelProvider(requireActivity()).get(HHomeVM.class);

        setActionBar();
        setCategoryAdapter();
        setProductAdapter();

        fetchData();

        // Navigate to product list when 'See More' is clicked
        binding.homeProductSeeMore.setOnClickListener(v -> {
            hHomeVM.setParamsLive(new Params()); // Set empty params
            NavHostFragment.findNavController(this).navigate(
                    HHomeFragmentDirections.homeToProductList()
            );
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

        // Handle search when user presses the "Search" on keyboard
        actionbarBinding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        // Handle search when user presses search icon
        actionbarBinding.tilSearch.setEndIconOnClickListener(v -> {
            performSearch();
            hideKeyboard(actionbarBinding.etSearch);
        });
    }

    private void performSearch() {
        Editable editable = binding.includeActionbarHome.etSearch.getText();
        if(editable == null) return;

        String search = editable.toString().trim();
        if (!search.isEmpty()) {
            Params params = new Params();
            params.setSearch(search);
            hHomeVM.setParamsLive(params);
            NavHostFragment.findNavController(this).navigate(
                    HHomeFragmentDirections.homeToProductList()
            );
        }
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void setCategoryAdapter() {
        binding.homeCategoryRv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        binding.homeCategoryRv.addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 3));

        categoryAdapter = new HomeCategoryAdapter(requireContext(), category -> {
            if(!category.getId().equals(getString(R.string.all_cate_id))) {
                List<String> categoryIds = new ArrayList<>();
                categoryIds.add(category.getId());

                // Include child categories
                if(category.getChildCategories() != null) {
                    List<String> childIds = category.getChildCategories()
                            .stream()
                            .map(Category::getId)
                            .toList();
                    categoryIds.addAll(childIds);
                }

                Params params = new Params();
                params.setCategoryIds(categoryIds);
                hHomeVM.setParamsLive(params);
            } else {
                // If selected 'All' then set empty params
                hHomeVM.setParamsLive(new Params());
            }

            NavHostFragment.findNavController(this).navigate(
                    HHomeFragmentDirections.homeToProductList()
            );
        });

        binding.homeCategoryRv.setAdapter(categoryAdapter);
    }

    private void setProductAdapter() {
        binding.homeProductRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        binding.homeProductRv.addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 2));

        productAdapter = new ProductAdapter(requireContext(), this, VIEW_TYPE_LISTING);

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

    @Override
    public void onProductDetailCLick(String productId) {
        // Set productId into arguments and navigate to product detail fragment
        NavHostFragment.findNavController(HHomeFragment.this).navigate(
                HHomeFragmentDirections.homeToProductDetail(productId)
        );
    }

    @Override
    public void onProductEditClick(String productId) {

    }

    @Override
    public void onProductDeleteClick(String productId, String productName) {

    }
}
