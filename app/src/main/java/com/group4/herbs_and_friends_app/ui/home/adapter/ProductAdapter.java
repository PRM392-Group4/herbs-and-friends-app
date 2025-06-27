package com.group4.herbs_and_friends_app.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.databinding.ItemProductBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private ProductClickListener listener;

    public ProductAdapter(Context context, ProductClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(context),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.image);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPriceDisplay());

        // Clear old chips
        holder.tags.removeAllViews();
        // Set new tag chips
        if (product.getTags() != null || !product.getTags().isEmpty()) {
            for (String tag : product.getTags()) {
                Chip chip = new Chip(context);
                chip.setText(tag);
                chip.setTextColor(ContextCompat.getColor(context, R.color.h_info));
                chip.setChipStrokeColorResource(R.color.h_info);
                chip.setChipStartPadding(0);
                chip.setChipEndPadding(0);
                holder.tags.addView(chip);
            }
        }

        holder.btnDetail.setOnClickListener(v -> listener.onProductDetailCLick(product.getId()));
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name, price;
        MaterialButton btnDetail;
        ChipGroup tags;
        public ProductViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            image = binding.productImage;
            name = binding.productName;
            price = binding.productPrice;
            btnDetail = binding.btnProductDetail;
            tags = binding.productTags;
        }
    }

    public interface ProductClickListener {
        void onProductDetailCLick(String productId);
    }
}
