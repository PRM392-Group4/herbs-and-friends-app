package com.group4.herbs_and_friends_app.ui.customer_side.home.adapter;

import static com.group4.herbs_and_friends_app.utils.AppCts.VIEW_TYPE_LISTING;
import static com.group4.herbs_and_friends_app.utils.AppCts.VIEW_TYPE_MANAGE;

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
import com.group4.herbs_and_friends_app.databinding.ItemProductManageBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Product> productList;
    private Context context;
    private ProductActionListener listener;
    private int currentViewType;

    public ProductAdapter(Context context, ProductActionListener listener, int viewType) {
        this.context = context;
        this.listener = listener;
        this.currentViewType = viewType;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return currentViewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LISTING) {
            ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(context), parent, false);
            return new ProductListingViewHolder(binding);
        } else if (viewType == VIEW_TYPE_MANAGE) {
            ItemProductManageBinding binding = ItemProductManageBinding.inflate(LayoutInflater.from(context), parent, false);
            return new ProductManageViewHolder(binding);
        } else {
            // This handles any other unexpected viewType.
            throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);

        // --- Casting View Holder to ProductListingViewHolder ---
        if (holder.getItemViewType() == VIEW_TYPE_LISTING) {
            initializeProductListingViewHolder((ProductListingViewHolder) holder, product);
        }

        // --- Casting View Holder to ProductManageViewHolder ---
        else if (holder.getItemViewType() == VIEW_TYPE_MANAGE) {
            initializeProductManageViewHolder((ProductManageViewHolder) holder, product);
        }
    }

    private void initializeProductManageViewHolder(ProductManageViewHolder holder, Product product) {
        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(R.drawable.ic_default_background)
                .error(R.drawable.ic_default_background)
                .into(holder.image);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPriceDisplay());
        holder.shortDescription.setText(product.getDescription());
        holder.inStock.setText(context.getString(R.string.in_stock_placeholder_txt, product.getInStock()));

        // Format date
        Date updated = product.getUpdatedAt();
        if (updated != null) {
            String formatted = new SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault())
                    .format(updated);
            holder.updatedAt.setText(String.format(context.getString(R.string.updated_text), formatted));
            holder.updatedAt.setVisibility(View.VISIBLE);
        } else {
            holder.updatedAt.setVisibility(View.GONE);
        }

        // Set listeners for manage actions
        holder.btnEdit.setOnClickListener(v -> listener.onProductEditClick(product.getId()));
        holder.btnDelete.setOnClickListener(v -> listener.onProductDeleteClick(product.getId(), product.getName()));
    }

    private void initializeProductListingViewHolder(ProductListingViewHolder holder, Product product) {
        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(R.drawable.ic_default_background)
                .error(R.drawable.ic_default_background)
                .into(holder.image);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPriceDisplay());

        List<String> tags = product.getTags();
        if (tags != null && !tags.isEmpty()) {
            holder.tag.setText(tags.get(0));
            holder.tag.setVisibility(View.VISIBLE);
        } else {
            holder.tag.setVisibility(View.INVISIBLE);
        }

        // Set listeners for details actions
        holder.btnDetail.setOnClickListener(v -> listener.onProductDetailCLick(product.getId()));
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // --- ViewHolder for Customer Product Listing ---
    public static  class ProductListingViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name, price;
        MaterialButton btnDetail;
        Chip tag;
        public ProductListingViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            image = binding.productImage;
            name = binding.productName;
            price = binding.productPrice;
            btnDetail = binding.btnProductDetail;
            tag = binding.productTag;
        }
    }

    // --- ViewHolder for Admin Product Management ---
    public static class ProductManageViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name, price, shortDescription, inStock, updatedAt;
        MaterialButton btnEdit, btnDelete;

        public ProductManageViewHolder(@NonNull ItemProductManageBinding binding) {
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

    public interface ProductActionListener {
        void onProductDetailCLick(String productId);
        void onProductEditClick(String productId);
        void onProductDeleteClick(String productId, String productName);
    }
}
