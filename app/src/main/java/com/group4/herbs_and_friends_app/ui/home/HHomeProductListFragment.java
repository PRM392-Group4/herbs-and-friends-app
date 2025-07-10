package com.group4.herbs_and_friends_app.ui.home;

import static com.group4.herbs_and_friends_app.utils.AppCts.VIEW_TYPE_LISTING;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import com.google.android.material.button.MaterialButton;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.data.model.enums.SortOptions;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeProductListBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHFilterSheetBinding;
import com.group4.herbs_and_friends_app.ui.base.ProductFilterBaseFragment;
import com.group4.herbs_and_friends_app.ui.home.adapter.CategoryAdapter;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;
import com.group4.herbs_and_friends_app.utils.GridRowSpacingDecoration;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeProductListFragment extends ProductFilterBaseFragment<HHomeVM> implements ProductAdapter.ProductActionListener {
    private FragmentHHomeProductListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHHomeProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupProductAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================================
    //    Abstract Method Implementations from Base
    // ================================================
    // Provide the specific UI elements from this fragment's binding
    @Override
    protected RecyclerView getProductRecyclerView() {
        return binding.productRv;
    }

    @Override
    protected MaterialButton getFilterButton() {
        return binding.btnProductFilter;
    }

    @Override
    protected MaterialButton getSortButton() {
        return binding.btnPriceSort;
    }

    @Override
    protected ViewHActionbarBinding getActionBarBinding() {
        return binding.includeActionbarProductList;
    }

    @Override
    protected ViewHFilterSheetBinding getFilterSheetBinding() {
        return binding.includeCategoryFilter;
    }

    // Provide the specific ViewModel instance for this fragment
    @Override
    protected HHomeVM getConcreteViewModel() {
        return new ViewModelProvider(requireActivity()).get(HHomeVM.class);
    }

    // Provide how to get/set params and data from HHomeVM
    @Override
    protected LiveData<Params> getParamsLiveFromVM(HHomeVM vm) {
        return vm.getParamsLive();
    }

    @Override
    protected void setParamsLiveToVM(HHomeVM vm, Params params) {
        vm.setParamsLive(params);
    }

    @Override
    protected LiveData<List<Product>> getProductsWithParamsLiveFromVM(HHomeVM vm) {
        return vm.getProductsWithParamsLive();
    }

    @Override
    protected LiveData<List<Category>> getAllCategoriesLiveFromVM(HHomeVM vm) {
        return vm.getAllCategoriesLive();
    }

    // Provide the NavController for navigation specific to this fragment
    @Override
    protected NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    // ================================
    //      Product Adapter Setup
    // ================================
    @Override
    protected void setupProductAdapter() {
        getProductRecyclerView().setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.grid_row_spacing);
        getProductRecyclerView().addItemDecoration(new GridRowSpacingDecoration(rowSpacing, 2));

        // productAdapter field is inherited from base
        productAdapter = new ProductAdapter(requireContext(), this, VIEW_TYPE_LISTING);
        getProductRecyclerView().setAdapter(productAdapter);
    }

    // =========================================
    //      ProductActionListener Callbacks
    // =========================================
    @Override
    public void onProductDetailCLick(String productId) {
        NavHostFragment.findNavController(this).navigate(
            HHomeProductListFragmentDirections.productListToProductDetail(productId)
        );
    }

    @Override
    public void onProductEditClick(String productId) {
        // Not applicable for customer list, implementation remains empty
    }

    @Override
    public void onProductDeleteClick(String productId, String productName) {
        // Not applicable for customer list, implementation remains empty
    }
}