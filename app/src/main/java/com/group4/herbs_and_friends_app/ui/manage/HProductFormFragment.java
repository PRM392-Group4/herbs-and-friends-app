package com.group4.herbs_and_friends_app.ui.manage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.FragmentHProductFormBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarWithoutSearchBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HProductFormFragment extends Fragment {
    private FragmentHProductFormBinding binding;
    private HProductManageVM viewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private final ArrayList<Uri> imageUris = new ArrayList<>();
    private final List<String> tags = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private String editingProductId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHProductFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(HProductManageVM.class);

        setupActionBar();
        setupCategorySpinner();
        setupTagInput();

        // Register image picker
        imagePickerLauncher = registerImagePicker();

        binding.btnAddImage.setOnClickListener(v -> openImagePicker());
        binding.btnCancel.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        binding.btnSave.setOnClickListener(v -> saveProduct());

        // If editing, load existing product
        if (getArguments() != null && getArguments().containsKey("productId")) {
            editingProductId = getArguments().getString("productId");
            viewModel.getSelectedProductLive(editingProductId)
                    .observe(getViewLifecycleOwner(), this::populateForm);
        }
    }

    @NonNull
    private ActivityResultLauncher<Intent> registerImagePicker() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                imageUris.add(data.getClipData().getItemAt(i).getUri());
                            }
                        } else if (data.getData() != null) {
                            imageUris.add(data.getData());
                        }
                        refreshImages();
                    }
                }
        );
    }

    private void setupActionBar() {
        ViewHActionbarWithoutSearchBinding ab = binding.includeActionBarProductForm;
        boolean isEdit = getArguments() != null && getArguments().containsKey("productId");
        ab.actionBarTitle.setText(isEdit ? "Chỉnh Sửa Thông Tin Cây" : "Thêm Cây");
        ab.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupCategorySpinner() {
        viewModel.getAllCategoriesLive().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList = categories;
                List<String> names = new ArrayList<>();
                for (Category c : categories) names.add(c.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(adapter);
            }
        });
    }

    private void setupTagInput() {
        binding.btnAddTag.setOnClickListener(v -> {
            String tag = binding.etTag.getText().toString().trim();
            if (!tag.isEmpty()) {
                tags.add(tag);
                Chip chip = new Chip(requireContext());
                chip.setText(tag);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(ic -> {
                    tags.remove(tag);
                    binding.chipGroupTags.removeView(ic);
                });
                binding.chipGroupTags.addView(chip);
                binding.etTag.setText("");
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    // Display picked images
    private void refreshImages() {
        binding.llImagesContainer.removeAllViews();
        for (Uri uri : new ArrayList<>(imageUris)) {
            // Create a frame layout to hold the image
            FrameLayout container = new FrameLayout(requireContext());
            FrameLayout.LayoutParams wrap = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            wrap.setMargins(8,8,8,8);
            container.setLayoutParams(wrap);

            // Add image view to the frame layout
            ImageView iv = new ImageView(requireContext());
            FrameLayout.LayoutParams ivParams = new FrameLayout.LayoutParams(200,200);
            iv.setLayoutParams(ivParams);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageURI(uri);
            container.addView(iv);

            // Show image remove button
            ImageButton removeBtn = new ImageButton(requireContext());
            FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(
                    48,48, Gravity.END|Gravity.TOP);
            removeBtn.setLayoutParams(btnParams);
            removeBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            removeBtn.setBackgroundColor(Color.TRANSPARENT);
            removeBtn.setOnClickListener(v -> {
                imageUris.remove(uri);
                refreshImages();
            });
            container.addView(removeBtn);

            binding.llImagesContainer.addView(container);
        }
    }

    private void populateForm(Product product) {
        binding.etName.setText(product.getName());
        binding.etPrice.setText(String.valueOf(product.getPrice()));
        binding.etDescription.setText(product.getDescription());
        binding.etStock.setText(String.valueOf(product.getInStock()));
        // Category
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId().equals(product.getCategoryId())) {
                binding.spinnerCategory.setSelection(i);
                break;
            }
        }
        // Tags
        binding.chipGroupTags.removeAllViews();
        tags.clear();
        if (product.getTags() != null) {
            for (String t : product.getTags()) {
                tags.add(t);
                Chip chip = new Chip(requireContext());
                chip.setText(t);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(ic -> {
                    tags.remove(t);
                    binding.chipGroupTags.removeView(ic);
                });
                binding.chipGroupTags.addView(chip);
            }
        }
        // Images
        imageUris.clear();
        if (product.getImageUrls() != null) {
            for (String url : product.getImageUrls()) {
                imageUris.add(Uri.parse(url));
            }
        }
        refreshImages();
    }

    private void saveProduct() {
        // Get input data
        String name = binding.etName.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String stockStr = binding.etStock.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();
        final String prodId = editingProductId != null ? editingProductId
                : UUID.randomUUID().toString();

        // Get validation state (description is optional)
        boolean valid = getDataAndValidate(name, priceStr, stockStr);

        // If invalid then return
        if (!valid) return;

        // If image uris list is empty then just post the product
        if (imageUris.isEmpty()) {
            postProduct(prodId, name, priceStr, desc, stockStr, new ArrayList<>());
            return;
        }

        // disable button and show loading state
        disableButtonAndShowLoading();
        
        // upload images via ViewModel
        viewModel.uploadImages(prodId, imageUris)
            .observe(getViewLifecycleOwner(), urls -> {
                // when urls are ready post the product
                postProduct(prodId, name, priceStr, desc, stockStr, urls);
                
                // reset button
                enableButtonAndShowLoading();
            });
    }

    private void enableButtonAndShowLoading() {
        binding.btnSave.setEnabled(true);
        binding.progressSave.setVisibility(View.INVISIBLE);
    }

    private void disableButtonAndShowLoading() {
        binding.btnSave.setEnabled(false);
        binding.progressSave.setVisibility(View.VISIBLE);
    }

    private boolean getDataAndValidate(String name, String price, String stock) {
        // clear previous errors
        binding.etName.setError(null);
        binding.etPrice.setError(null);
        binding.etStock.setError(null);

        // Set validation flag
        boolean valid = true;

        // Show error if the inputs are invalid
        if (name.isEmpty()) { binding.etName.setError("Vui lòng nhập tên cho cây"); valid = false; }
        if (price.isEmpty()) { binding.etPrice.setError("Vui lòng nhập giá cây"); valid = false; }
        if (stock.isEmpty()) { binding.etStock.setError("Vui lòng nhập số lượng tồn kho"); valid = false; }

        return valid;
    }

    private void postProduct(String prodId, String name, String priceStr,
                             String desc, String stockStr, List<String> urls) {
        long price = Long.parseLong(priceStr);
        int stock = Integer.parseInt(stockStr);
        String categoryId = categoryList.get(binding.spinnerCategory.getSelectedItemPosition()).getId();

        // Populate product fields
        Product prod = new Product();
        prod.setId(prodId);
        prod.setName(name);
        prod.setPrice(price);
        prod.setDescription(desc);
        prod.setInStock(stock);
        prod.setCategoryId(categoryId);
        prod.setImageUrls(new ArrayList<>(urls));
        prod.setTags(new ArrayList<>(tags));
        prod.setUpdatedAt(new Date());
        if (editingProductId == null) prod.setCreatedAt(new Date());

        // Check if the product id is provided to decide add or edit operation
        LiveData<Boolean> action = editingProductId == null
                ? viewModel.addProduct(prod)
                : viewModel.updateProduct(prodId, prod);
        action.observe(getViewLifecycleOwner(), success -> {
            Toast.makeText(requireContext(),
                    success ? (editingProductId == null ? "Product added" : "Product updated")
                            : "Operation failed",
                    Toast.LENGTH_SHORT).show();

            // Enable save button after operation
            binding.btnSave.setEnabled(true);

            if (Boolean.TRUE.equals(success)) {
                NavHostFragment.findNavController(this).navigateUp();
            }
        });
    }
}
