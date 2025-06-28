package com.group4.herbs_and_friends_app.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
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

        List<String> tags = product.getTags();
        if (tags != null && !tags.isEmpty()) {
            holder.tag.setText(tags.get(0));
            holder.tag.setVisibility(View.VISIBLE);
        } else holder.tag.setVisibility(View.INVISIBLE);

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
        Chip tag;
        public ProductViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            image = binding.productImage;
            name = binding.productName;
            price = binding.productPrice;
            btnDetail = binding.btnProductDetail;
            tag = binding.productTag;
        }
    }

    public interface ProductClickListener {
        void onProductDetailCLick(String productId);
    }
}
