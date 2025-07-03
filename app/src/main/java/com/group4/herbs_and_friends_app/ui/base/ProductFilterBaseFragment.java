package com.group4.herbs_and_friends_app.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.data.model.enums.SortOptions;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHFilterSheetBinding;
import com.group4.herbs_and_friends_app.ui.home.adapter.CategoryAdapter;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class ProductFilterBaseFragment<P extends ViewModel> extends Fragment {
    // Common Fields
    protected ProductAdapter productAdapter;
    protected CategoryAdapter categoryAdapter;
    protected Params params;
    protected List<String> selectedCategoryIds = new ArrayList<>();
    protected P viewModel;

    // Abstract methods to be implemented by child fragments
    protected abstract RecyclerView getProductRecyclerView();
    protected abstract MaterialButton getFilterButton();
    protected abstract MaterialButton getSortButton();
    protected abstract ViewHActionbarBinding getActionBarBinding();
    protected abstract ViewHFilterSheetBinding getFilterSheetBinding();
    protected abstract P getConcreteViewModel();
    protected abstract LiveData<Params> getParamsLiveFromVM(P vm);
    protected abstract void setParamsLiveToVM(P vm, Params params);
    protected abstract LiveData<List<Product>> getProductsWithParamsLiveFromVM(P vm);
    protected abstract LiveData<List<Category>> getAllCategoriesLiveFromVM(P vm);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the specific ViewModel provided by the child fragment
        viewModel = getConcreteViewModel();

        // Setup common UI components
        setActionBar();
        setupCategoryAdapter();
        initParams();
        fetchData();

        // Set common listeners
        getFilterButton().setOnClickListener(v -> showCategoryFilter());
        getSortButton().setOnClickListener(v -> showSortPriceMenu());
    }

    // Common Methods (moved from child fragments)
    protected void setActionBar() {
        ViewHActionbarBinding actionbarBinding = getActionBarBinding();

        actionbarBinding.btnBack.setOnClickListener(v -> getNavController().popBackStack());

        actionbarBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Replace end icon of search bar upon text input changes
                if(s != null && !s.toString().trim().isEmpty()) {
                    actionbarBinding.tilSearch.setEndIconDrawable(R.drawable.ic_cancel);
                } else actionbarBinding.tilSearch.setEndIconDrawable(R.drawable.ic_search);
            }
        });

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

        actionbarBinding.tilSearch.setEndIconOnClickListener(v -> {
            Editable editable = actionbarBinding.etSearch.getText();
            if (editable != null && !editable.toString().trim().isEmpty()) {
                // Clear and reload all products
                actionbarBinding.etSearch.setText("");
                if(params == null) params = new Params();
                params.setSearch(null);
                setParamsLiveToVM(viewModel, params); // Reload with no search
            } else {
                performSearch();
            }
            hideKeyboard(actionbarBinding.etSearch);
        });
    }

    protected void performSearch() {
        Editable editable = getActionBarBinding().etSearch.getText();
        if(editable == null) return;

        String search = editable.toString().trim();
        if (!search.isEmpty()) {
            if(params == null) params = new Params();
            params.setSearch(search);
            setParamsLiveToVM(viewModel, params);
        }
    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    protected void setupCategoryAdapter() {
        getFilterSheetBinding().filtersRv.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                if (category.isChildCategory()) selectedCategoryIds.remove(category.getCategoryParentId());
                if (category.getChildCategories() != null) {
                    selectedCategoryIds.removeAll(category.getChildCategories()
                            .stream()
                            .map(Category::getId)
                            .toList());
                }
            }
        });
        getFilterSheetBinding().filtersRv.setAdapter(categoryAdapter);
    }

    protected void initParams() {
        LiveData<Params> vmParamsLive = getParamsLiveFromVM(viewModel);
        if(vmParamsLive != null && vmParamsLive.getValue() != null) {
            params = vmParamsLive.getValue();
            if (params.getSearch() != null)
                getActionBarBinding().etSearch.setText(params.getSearch());
            if (params.getCategoryIds() != null && !params.getCategoryIds().isEmpty()) {
                selectedCategoryIds.addAll(params.getCategoryIds());
                getFilterButton().setText(R.string.btn_product_filter_set);
            }
        } else {
            params = new Params();
            setParamsLiveToVM(viewModel, params);
        }
    }

    protected void fetchData() {
        getProductsWithParamsLiveFromVM(viewModel).observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.setProductList(products);
            }
        });

        getAllCategoriesLiveFromVM(viewModel).observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryAdapter.setCategoryList(categories);
                if (!selectedCategoryIds.isEmpty()) {
                    categoryAdapter.setSelectedCategories(selectedCategoryIds);
                }
            }
        });
    }

    protected void showCategoryFilter() {
        ViewHFilterSheetBinding filterSheetBinding = getFilterSheetBinding();
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

    protected void applyFilter(View filterSheet) {
        if(params == null) params = new Params();
        params.setCategoryIds(selectedCategoryIds.isEmpty() ? null : selectedCategoryIds);
        setParamsLiveToVM(viewModel, params);

        getFilterButton().setText(R.string.btn_product_filter_set);
        hideFilterSheet(filterSheet);
    }

    protected void clearFilter(View filterSheet) {
        categoryAdapter.clearSelectedCategories();
        selectedCategoryIds.clear();

        if(params == null) params = new Params();
        params.setCategoryIds(null);
        setParamsLiveToVM(viewModel, params);

        getFilterButton().setText(R.string.btn_filter_placeholder_txt);
        hideFilterSheet(filterSheet);
    }

    protected void closeFilter(View filterSheet) {
        clearFilter(filterSheet);
        hideFilterSheet(filterSheet);
    }

    protected void hideFilterSheet(View filterSheet) {
        filterSheet.animate()
                .translationX(-filterSheet.getWidth())
                .setDuration(300)
                .withEndAction(() -> filterSheet.setVisibility(View.GONE))
                .start();
    }

    protected void showSortPriceMenu() {
        PopupMenu sortPriceMenu = new PopupMenu(requireContext(), getSortButton());
        sortPriceMenu.getMenuInflater().inflate(R.menu.view_h_sort_price_menu, sortPriceMenu.getMenu());

        sortPriceMenu.setOnMenuItemClickListener(sortOption -> {
            getSortButton().setText(sortOption.getTitle());

            if(params == null) params = new Params();

            if(sortOption.getItemId() == R.id.sort_price_default) {
                params.setSort(SortOptions.PRICE_DEFAULT);
            } else if(sortOption.getItemId() == R.id.sort_price_asc) {
                params.setSort(SortOptions.PRICE_ASC);
            } else if(sortOption.getItemId() == R.id.sort_price_desc) {
                params.setSort(SortOptions.PRICE_DESC);
            }

            setParamsLiveToVM(viewModel, params);
            return true;
        });

        sortPriceMenu.show();
    }

    protected abstract NavController getNavController();

    protected abstract void setupProductAdapter();
}

