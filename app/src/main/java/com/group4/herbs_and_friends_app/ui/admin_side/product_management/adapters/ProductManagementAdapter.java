package com.group4.herbs_and_friends_app.ui.admin_side.product_management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.ItemProductManageBinding;
import com.group4.herbs_and_friends_app.ui.base.product.BaseProductAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductManagementAdapter extends BaseProductAdapter {
    public ProductManagementAdapter(Context context, BaseProductAdapter.ProductActionListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder buildViewHolder(ViewGroup parent, int viewType) {
        ItemProductManageBinding binding = ItemProductManageBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ProductManagementViewHolder(binding);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, Product product) {
        // --- Casting View Holder to ProductManagementViewHolder ---
        ProductManagementViewHolder h = (ProductManagementViewHolder) holder;

        // Set data
        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(R.drawable.ic_default_background)
                .error(R.drawable.ic_default_background)
                .into(h.image);
        h.name.setText(product.getName());
        h.price.setText(product.getPriceDisplay());
        h.shortDescription.setText(product.getDescription());
        h.inStock.setText(context.getString(R.string.in_stock_placeholder_txt, product.getInStock()));

        // Format date
        Date updated = product.getUpdatedAt();
        if (updated != null) {
            String formatted = new SimpleDateFormat(
                    context.getString(R.string.date_format), Locale.getDefault())
                    .format(updated);
            h.updatedAt.setText(String.format(
                    context.getString(R.string.updated_text), formatted));
            h.updatedAt.setVisibility(View.VISIBLE);
        } else {
            h.updatedAt.setVisibility(View.GONE);
        }

        // Set button events
        h.btnEdit.setOnClickListener(v -> listener.onProductEditClick(product.getId()));
        h.btnDelete.setOnClickListener(v -> listener.onProductDeleteClick(
                product.getId(), product.getName()));
    }

    static class ProductManagementViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name, price, shortDescription, inStock, updatedAt;
        MaterialButton btnEdit, btnDelete;

        ProductManagementViewHolder(@NonNull ItemProductManageBinding binding) {
            super(binding.getRoot());
            image = binding.productImage;
            name = binding.productName;
            price = binding.productPrice;
            shortDescription = binding.productShortDescription;
            inStock = binding.productInStock;
            updatedAt = binding.productUpdatedAt;
            btnEdit = binding.btnEditProduct;
            btnDelete = binding.btnDeleteProduct;
        }
    }
}
