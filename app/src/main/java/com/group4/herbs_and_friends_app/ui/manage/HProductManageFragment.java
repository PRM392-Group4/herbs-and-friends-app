package com.group4.herbs_and_friends_app.ui.manage;

import static com.group4.herbs_and_friends_app.utils.AppCts.VIEW_TYPE_MANAGE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.group4.herbs_and_friends_app.databinding.FragmentHProductManageBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHFilterSheetBinding;
import com.group4.herbs_and_friends_app.ui.base.ProductFilterBaseFragment;
import com.group4.herbs_and_friends_app.ui.home.adapter.ProductAdapter;
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

    // ================================
    // === Specific Product Adapter Setup for this Fragment (LinearLayoutManager and specific listener type)
    // ================================
    @Override
    protected void setupProductAdapter() {
        getProductRecyclerView().setLayoutManager(new LinearLayoutManager(requireContext()));

        // productAdapter field is inherited from base
        productAdapter = new ProductAdapter(requireContext(), this, VIEW_TYPE_MANAGE);
        getProductRecyclerView().setAdapter(productAdapter);
    }

    // ================================
    // === ProductActionListener Callbacks (specific to this fragment's actions)
    // ================================
    @Override
    public void onProductDetailCLick(String productId) {
    }

    @Override
    public void onProductEditClick(String productId) {

    }

    @Override
    public void onProductDeleteClick(String productId, String productName) {
    }

    // ================================
    // === Navigation for Add Product
    // ================================
    private void navigateToAddProduct() {
    }
}
