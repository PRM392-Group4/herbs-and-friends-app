package com.group4.herbs_and_friends_app.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.FragmentHHomeProductDetailBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarBinding;
import com.group4.herbs_and_friends_app.ui.cart.HCartVM;
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
    private HCartVM hCartVM;
    private CarouselAdapter carouselAdapter;
    private String productId;
    private int currentStock;
    private int quantity;
    private boolean loggedIn = false;

    private Product currentProduct;

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

        // Perform check to see if user is logged in
        checkLoggedIn();

        hHomeVM = new ViewModelProvider(requireActivity()).get(HHomeVM.class);
        if(loggedIn) hCartVM = new ViewModelProvider(requireActivity()).get(HCartVM.class);

        setActionBar();
        setCarouselAdapter();

        productId = HHomeProductDetailFragmentArgs.fromBundle(getArguments()).getProductId();
        fetchData(productId);

        // Setup Add To Cart button
        setupAddToCartButton();

        binding.btnFastCheckout.setOnClickListener(v -> {
            //TODO: Handle fast checkout
            if (!loggedIn) {
                Toast.makeText(requireContext(), "Hãy đăng nhập trước bạn nhé.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentStock == 0) {
                Toast.makeText(requireContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity <= 0) {
                Toast.makeText(requireContext(), "Số lượng không hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            } else if (quantity > currentStock){
                Toast.makeText(requireContext(), "Số lượng vượt quá hàng tồn kho.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (productId == null) {
                Toast.makeText(requireContext(), "Có lỗi xảy ra, thử lại sau",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass data via Bundle
            Bundle bundle = new Bundle();
            if (currentProduct != null ){
                bundle.putString("fastCheckoutItem_id", productId);
                bundle.putString("fastCheckoutItem_name", currentProduct.getName());
                bundle.putInt("fastCheckoutItem_quantity", quantity);
                bundle.putLong("fastCheckoutItem_unitPrice", currentProduct.getPrice());
                bundle.putString("fastCheckoutItem_imageUrl", currentProduct.getImageUrls().get(0));
            }

            // Navigate to HCheckoutFragment
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.productDetailToCheckout, bundle);
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

    private void checkLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            loggedIn = true;
        } else {
            binding.quantityLabel.setVisibility(View.GONE);
            binding.quantitySelector.setVisibility(View.GONE);
            binding.btnAddToCart.setVisibility(View.GONE);
            binding.btnFastCheckout.setVisibility(View.GONE);
            binding.productOutOfStock.setText("Hãy đăng nhập để mua sản phẩm");
            binding.productOutOfStock.setVisibility(View.VISIBLE);
        }
    }

    private void setupAddToCartButton() {
        binding.btnAddToCart.setOnClickListener(v -> {
            hCartVM.addOrUpdateItemToCart(productId, quantity).observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setActionBar() {
        ViewHActionbarBinding actionbarBinding = binding.includeActionbarProductDetail;

        // Navigate back to product list fragment
        actionbarBinding.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
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
        Editable editable = binding.includeActionbarProductDetail.etSearch.getText();
        if(editable == null) return;

        String search = editable.toString().trim();
        if (!search.isEmpty()) {
            Params params = new Params();
            params.setSearch(search);
            hHomeVM.setParamsLive(params);
            NavHostFragment.findNavController(this).navigate(
                    HHomeProductDetailFragmentDirections.productDetailToProductList()
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

    private void setCarouselAdapter() {
        binding.imageCarousel.setLayoutManager(new CarouselLayoutManager());
        carouselAdapter = new CarouselAdapter(requireContext());
        binding.imageCarousel.setAdapter(carouselAdapter);
        new CarouselSnapHelper().attachToRecyclerView(binding.imageCarousel);
    }

    private void fetchData(String productId) {
        hHomeVM.getSelectedProductLive(productId).observe(getViewLifecycleOwner(), product -> {
            if(product != null) {
                currentProduct = product;
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