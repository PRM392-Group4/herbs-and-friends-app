package com.group4.herbs_and_friends_app.ui.customer_side.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.ItemProductBinding;
import com.group4.herbs_and_friends_app.ui.base.product.BaseProductAdapter;
import java.util.List;

public class ProductListingAdapter extends BaseProductAdapter {
    public ProductListingAdapter(Context context, BaseProductAdapter.ProductActionListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder buildViewHolder(ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ProductListingViewHolder(binding);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, Product product) {
        // --- Casting View Holder to ProductListingViewHolder ---
        ProductListingViewHolder h = (ProductListingViewHolder) holder;

        // Set data
        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(R.drawable.ic_default_background)
                .error(R.drawable.ic_default_background)
                .into(h.image);
        h.name.setText(product.getName());
        h.price.setText(product.getPriceDisplay());

        // Set tags
        List<String> tags = product.getTags();
        if (tags != null && !tags.isEmpty()) {
            h.tag.setText(tags.get(0));
            h.tag.setVisibility(View.VISIBLE);
        } else {
            h.tag.setVisibility(View.INVISIBLE);
        }

        // Add button event
        h.btnDetail.setOnClickListener(v -> listener.onProductDetailCLick(product.getId()));
    }

    static class ProductListingViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name, price;
        MaterialButton btnDetail;
        Chip tag;

        ProductListingViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            image = binding.productImage;
            name = binding.productName;
            price = binding.productPrice;
            btnDetail = binding.btnProductDetail;
            tag = binding.productTag;
        }
    }
}

