package com.group4.herbs_and_friends_app.ui.admin_side.product_management;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.FragmentHProductManagementBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHFilterSheetBinding;
import com.group4.herbs_and_friends_app.ui.admin_side.product_management.adapters.ProductManagementAdapter;
import com.group4.herbs_and_friends_app.ui.base.product.BaseProductFilterFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HProductManagementFragment extends BaseProductFilterFragment<HProductManagementVM> implements ProductManagementAdapter.ProductActionListener {
    private FragmentHProductManagementBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHProductManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupProductAdapter();

        // Specific to manage fragment: Add Product FAB listener
        binding.btnAddProduct.setOnClickListener(v -> navigateToAddProduct());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // =======================================
    // === Abstract Method Implementations
    // =======================================
    // Provide the specific UI elements from this fragment's binding
    @Override
    protected RecyclerView getProductRecyclerView() {
        return binding.productManageRv;
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
        return binding.includeActionbarProductManage;
    }

    @Override
    protected ViewHFilterSheetBinding getFilterSheetBinding() {
        return binding.includeCategoryFilter;
    }

    // Provide the specific ViewModel instance for this fragment
    @Override
    protected HProductManagementVM getConcreteViewModel() {
        return new ViewModelProvider(requireActivity()).get(HProductManagementVM.class);
    }

    // Provide how to get/set params and data from HProductManageVM
    @Override
    protected LiveData<Params> getParamsLiveFromVM(HProductManagementVM vm) {
        return vm.getParamsLive();
    }

    @Override
    protected void setParamsLiveToVM(HProductManagementVM vm, Params params) {
        vm.setParamsLive(params);
    }

    @Override
    protected LiveData<List<Product>> getProductsWithParamsLiveFromVM(HProductManagementVM vm) {
        return vm.getProductsWithParamsLive();
    }

    @Override
    protected LiveData<List<Category>> getAllCategoriesLiveFromVM(HProductManagementVM vm) {
        return vm.getAllCategoriesLive();
    }

    // Provide the NavController for navigation specific to this fragment
    @Override
    protected NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    // ======================================================
    // === Specific Product Adapter Setup for this Fragment
    // ======================================================
    @Override
    protected void setupProductAdapter() {
        getProductRecyclerView().setLayoutManager(new LinearLayoutManager(requireContext()));

        // productAdapter field is inherited from base
        productAdapter = new ProductManagementAdapter(requireContext(), this);
        getProductRecyclerView().setAdapter(productAdapter);
    }

    // =====================================
    // === ProductActionListener Callbacks
    // =====================================
    @Override
    public void onProductDetailCLick(String productId) {
    }

    @Override
    public void onProductEditClick(String productId) {
        NavHostFragment.findNavController(HProductManagementFragment.this).navigate(
                HProductManagementFragmentDirections.productListToProductForm(productId)
        );
    }

    @Override
    public void onProductDeleteClick(String productId, String productName) {
        // Show dialog box and wait for user confirmation then delete product
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa '" + productName + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    getConcreteViewModel()
                            .deleteProduct(productId)
                            .observe(getViewLifecycleOwner(), success -> {
                                if (Boolean.TRUE.equals(success)) {
                                    // re-load the list so the item disappears
                                    if (params != null) setParamsLiveToVM(viewModel, params);
                                } else {
                                    Toast.makeText(requireContext(),
                                            "Xóa thất bại",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    // ================================
    // === Navigation for Add Product
    // ================================
    private void navigateToAddProduct() {
        NavHostFragment.findNavController(HProductManagementFragment.this).navigate(
                HProductManagementFragmentDirections.productListToProductForm(null)
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        // force a fresh load of products
        if (params != null) {
            setParamsLiveToVM(viewModel, params);
        }
    }
}
