package com.group4.herbs_and_friends_app.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.enums.SortOptions;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeProductListBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHFilterSheetBinding;
import com.group4.herbs_and_friends_app.ui.home.adapter.CategoryAdapter;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;
import com.group4.herbs_and_friends_app.utils.GridRowSpacingDecoration;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeProductListFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHHomeProductListBinding binding;
    private HHomeVM hHomeVM;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private Params params;
    private List<String> selectedCategoryIds = new ArrayList<>();

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

        hHomeVM = new ViewModelProvider(requireActivity()).get(HHomeVM.class);

        setActionBar();
        setProductAdapter();
        setCategoryAdapter();

        initParams();
        fetchData();

        binding.btnProductFilter.setOnClickListener(v -> showCategoryFilter());
        binding.btnPriceSort.setOnClickListener(v -> showSortPriceMenu());
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

        // Navigate back to home fragment
        actionbarBinding.btnBack.setOnClickListener(v -> {
            if(params != null) params.clear();
            hHomeVM.setParamsLive(null);
            NavHostFragment.findNavController(this).navigate(
                    HHomeProductListFragmentDirections.productListToHome()
            );
        });

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
        Editable editable = binding.includeActionbarProductList.etSearch.getText();
        if(editable == null) return;

        String search = editable.toString().trim();
        if (!search.isEmpty()) {
            if(params == null) params = new Params();
            params.setSearch(search);
            hHomeVM.setParamsLive(params);
        }
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void setProductAdapter() {
        binding.productRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        binding.productRv.addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 2));

        productAdapter = new ProductAdapter(requireContext(), productId -> {
            // Set productId into arguments and navigate to product detail fragment
            NavHostFragment.findNavController(this).navigate(
                    HHomeProductListFragmentDirections.productListToProductDetail(productId)
            );
        });

        binding.productRv.setAdapter(productAdapter);
    }

    private void setCategoryAdapter() {
        binding.includeCategoryFilter.filtersRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new CategoryAdapter(requireContext(), category -> {
            if (category.isChecked()) {
                selectedCategoryIds.add(category.getId());
                if(category.getChildCategories() != null) {
                    selectedCategoryIds.addAll(category.getChildCategories()
                            .stream()
                            .map(Category::getId)
                            .toList());
                }
            } else {
                selectedCategoryIds.remove(category.getId());

                // If child category, remove parent from selection
                if (category.isChildCategory()) selectedCategoryIds.remove(category.getCategoryParentId());

                // If parent category, remove all child categories
                if (category.getChildCategories() != null) {
                    selectedCategoryIds.removeAll(category.getChildCategories()
                            .stream()
                            .map(Category::getId)
                            .toList());
                }
            }
        });
        binding.includeCategoryFilter.filtersRv.setAdapter(categoryAdapter);
    }

    private void initParams() {
        if(hHomeVM.getParamsLive() != null && hHomeVM.getParamsLive().getValue() != null) {
            params = hHomeVM.getParamsLive().getValue();

            // Populate search bar
            if (params.getSearch() != null)
                binding.includeActionbarProductList.etSearch.setText(params.getSearch());

            // Set selected category
            if (params.getCategoryIds() != null && !params.getCategoryIds().isEmpty()) {
                selectedCategoryIds.addAll(params.getCategoryIds());
                binding.btnProductFilter.setText(R.string.btn_product_filter_set);
            }
        } else {
            // Empty params to show all products
            params = new Params();
            hHomeVM.setParamsLive(params);
        }
    }

    private void fetchData() {
        // Observe products with params - this will automatically update when params change
        hHomeVM.getProductsWithParamsLive().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.setProductList(products);
            }
        });

        // Observe categories for filter
        hHomeVM.getAllCategoriesLive().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryAdapter.setCategoryList(categories);
                if (!selectedCategoryIds.isEmpty()) {
                    categoryAdapter.setSelectedCategories(selectedCategoryIds);
                }
            }
        });
    }

    private void showCategoryFilter() {
        ViewHFilterSheetBinding filterSheetBinding = binding.includeCategoryFilter;
        View filterSheet = filterSheetBinding.filterSheet;
        filterSheet.setVisibility(View.VISIBLE);
        filterSheet.post(() -> {
            float width = filterSheet.getWidth();
            filterSheet.setTranslationX(-width);
            filterSheet.animate()
                    .translationX(0f)
                    .setDuration(300)
                    .start();
        });

        filterSheetBinding.btnApplyFilter.setOnClickListener(v -> applyFilter(filterSheet));

        filterSheetBinding.btnClearFilter.setOnClickListener(v -> clearFilter(filterSheet));

        filterSheetBinding.btnCloseFilter.setOnClickListener(v -> closeFilter(filterSheet));
    }

    private void applyFilter(View filterSheet) {
        if(params == null) params = new Params();
        params.setCategoryIds(selectedCategoryIds.isEmpty() ? null : selectedCategoryIds);
        hHomeVM.setParamsLive(params);

        binding.btnProductFilter.setText(R.string.btn_product_filter_set);

        hideFilterSheet(filterSheet);
    }

    private void clearFilter(View filterSheet) {
        categoryAdapter.clearSelectedCategories();
        selectedCategoryIds.clear();

        if(params == null) params = new Params();
        params.setCategoryIds(null);
        hHomeVM.setParamsLive(params);

        binding.btnProductFilter.setText(R.string.btn_filter_placeholder_txt);

        hideFilterSheet(filterSheet);
    }

    private void closeFilter(View filterSheet) {
        clearFilter(filterSheet);
        hideFilterSheet(filterSheet);
    }

    private void hideFilterSheet(View filterSheet) {
        filterSheet.animate()
                .translationX(-filterSheet.getWidth())
                .setDuration(300)
                .withEndAction(() -> filterSheet.setVisibility(View.GONE))
                .start();
    }

    private void showSortPriceMenu() {
        PopupMenu sortPriceMenu = new PopupMenu(requireContext(), binding.btnPriceSort);
        sortPriceMenu.getMenuInflater().inflate(R.menu.view_h_sort_price_menu, sortPriceMenu.getMenu());

        sortPriceMenu.setOnMenuItemClickListener(sortOption -> {
            binding.btnPriceSort.setText(sortOption.getTitle());

            if(params == null) params = new Params();

            if(sortOption.getItemId() == R.id.sort_price_default) {
                params.setSort(SortOptions.PRICE_DEFAULT);
            } else if(sortOption.getItemId() == R.id.sort_price_asc) {
                params.setSort(SortOptions.PRICE_ASC);
            } else if(sortOption.getItemId() == R.id.sort_price_desc) {
                params.setSort(SortOptions.PRICE_DESC);
            }

            hHomeVM.setParamsLive(params);
            return true;
        });

        sortPriceMenu.show();
    }
}