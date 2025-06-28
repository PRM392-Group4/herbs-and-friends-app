package com.group4.herbs_and_friends_app.ui.home;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.chip.Chip;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeProductDetailBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.ui.home.adapter.CarouselAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeProductDetailFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHHomeProductDetailBinding binding;
    private HHomeVM hHomeVM;
    private CarouselAdapter carouselAdapter;
    private String productId;
    private int currentStock;
    private int quantity;

    // ================================
    // === Lifecycle
    // ================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHHomeProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar();
        setCarouselAdapter();

        productId = HHomeProductDetailFragmentArgs.fromBundle(getArguments()).getProductId();
        hHomeVM = new ViewModelProvider(this).get(HHomeVM.class);
        fetchData(productId);

        binding.btnAddToCart.setOnClickListener(v -> {
            //TODO: Add product to cart
        });

        binding.btnFastCheckout.setOnClickListener(v -> {
            //TODO: Handle fast checkout
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
        ViewHActionbarBinding actionbarBinding = binding.includeActionbarProductDetail;
        actionbarBinding.btnBack.setVisibility(View.VISIBLE);

        actionbarBinding.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        actionbarBinding.tilSearch.setEndIconOnClickListener(v -> {
            Editable editable = actionbarBinding.etSearch.getText();
            if(editable == null) return;

            String search = editable.toString().trim();
            if(search.isEmpty()) return;

            // Set search into arguments and navigate to product list fragment
            // TODO: Replace with shared VM filters
            NavHostFragment.findNavController(this).navigate(
                    HHomeProductDetailFragmentDirections.productDetailToProductList(null, search)
            );
        });
    }

    private void setCarouselAdapter() {
        binding.imageCarousel.setLayoutManager(new CarouselLayoutManager());
        carouselAdapter = new CarouselAdapter(requireContext());
        binding.imageCarousel.setAdapter(carouselAdapter);
        new CarouselSnapHelper().attachToRecyclerView(binding.imageCarousel);
    }

    private void fetchData(String productId) {
        hHomeVM.getSelectedProductLive(productId).observe(getViewLifecycleOwner(), product -> {
            if(product != null) {
                populateData(product);
            } else {
                productNotFoundView();
            }
        });
    }

    private void populateData(Product product) {
        carouselAdapter.setImageUrlList(product.getImageUrls());
        binding.name.setText(product.getName());
        binding.description.setText(product.getDescription());
        binding.price.setText(product.getPriceDisplay());
        binding.stock.setText(String.valueOf(product.getInStock()));

        // Set product tags into view
        binding.tags.removeAllViews(); // Clear old chips
        List<String> tags = product.getTags();
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                Chip chip = new Chip(requireContext());
                chip.setText(tag);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.h_info));
                chip.setChipStrokeColorResource(R.color.h_info);
                chip.setChipStartPadding(2);
                chip.setChipEndPadding(2);
                binding.tags.addView(chip);
            }
        }

        // Set current stock and check if out of stock
        currentStock = product.getInStock();
        checkOutOfStock();

        quantity = 1;
        quantitySelectorListener();
    }

    private void productNotFoundView() {
        binding.productDetailView.setVisibility(View.GONE);
        binding.productNotFound.setVisibility(View.VISIBLE);
    }

    private void checkOutOfStock() {
        if(currentStock == 0) {
            binding.quantityLabel.setVisibility(View.GONE);
            binding.quantitySelector.setVisibility(View.GONE);
            binding.btnAddToCart.setVisibility(View.GONE);
            binding.btnFastCheckout.setVisibility(View.GONE);
            binding.productOutOfStock.setVisibility(View.VISIBLE);
        }
    }

    private void quantitySelectorListener() {
        binding.btnQuantityAdd.setOnClickListener(v -> {
            if(quantity < currentStock) quantity++;
            binding.textQuantity.setText(String.valueOf(quantity));
        });

        binding.btnQuantityMinus.setOnClickListener(v -> {
            if(quantity > 1) quantity--;
            binding.textQuantity.setText(String.valueOf(quantity));
        });
    }
}