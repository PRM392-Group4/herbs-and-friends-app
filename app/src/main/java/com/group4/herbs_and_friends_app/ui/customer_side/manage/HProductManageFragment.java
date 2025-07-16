package com.group4.herbs_and_friends_app.ui.customer_side.manage;

import static com.group4.herbs_and_friends_app.utils.AppCts.VIEW_TYPE_MANAGE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.FragmentHProductManageBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHFilterSheetBinding;
import com.group4.herbs_and_friends_app.ui.base.ProductFilterBaseFragment;
import com.group4.herbs_and_friends_app.ui.customer_side.home.adapter.ProductAdapter;

import java.util.List;

public class HProductManageFragment extends ProductFilterBaseFragment<HProductManageVM> implements ProductAdapter.ProductActionListener {
    private FragmentHProductManageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHProductManageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupProductAdapter();

        // Specific to manage fragment: Add Product FAB listener
        binding.btnAddProduct.setOnClickListener(v -> navigateToAddProduct());
        
        // Setup spinner for switching between management types
        setupManageSpinner();
    }

    private void setupManageSpinner() {
        Spinner spinner = binding.spinnerManageType;
        
        // Create adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.manage_type_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        // Set default selection to "Quản lý cây" (index 0)
        spinner.setSelection(0);
        
        // Handle spinner selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Already on product management, do nothing
                        break;
                    case 1:
                        // Navigate to order management
                        navigateToOrderManagement();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void navigateToOrderManagement() {
        NavController navController = getNavController();
        navController.navigate(R.id.action_productManage_to_orderManage);
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
    protected HProductManageVM getConcreteViewModel() {
        return new ViewModelProvider(requireActivity()).get(HProductManageVM.class);
    }

    // Provide how to get/set params and data from HProductManageVM
    @Override
    protected LiveData<Params> getParamsLiveFromVM(HProductManageVM vm) {
        return vm.getParamsLive();
    }

    @Override
    protected void setParamsLiveToVM(HProductManageVM vm, Params params) {
        vm.setParamsLive(params);
    }

    @Override
    protected LiveData<List<Product>> getProductsWithParamsLiveFromVM(HProductManageVM vm) {
        return vm.getProductsWithParamsLive();
    }

    @Override
    protected LiveData<List<Category>> getAllCategoriesLiveFromVM(HProductManageVM vm) {
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
        productAdapter = new ProductAdapter(requireContext(), this, VIEW_TYPE_MANAGE);
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
        NavHostFragment.findNavController(HProductManageFragment.this).navigate(
                HProductManageFragmentDirections.productListToProductForm(productId)
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
        NavHostFragment.findNavController(HProductManageFragment.this).navigate(
            HProductManageFragmentDirections.productListToProductForm(null)
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
