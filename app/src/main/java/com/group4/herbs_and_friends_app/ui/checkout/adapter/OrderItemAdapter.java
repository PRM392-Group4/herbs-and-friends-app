package com.group4.herbs_and_friends_app.ui.checkout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.databinding.ItemCheckoutProductBinding;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import java.util.List;

public class OrderItemAdapter extends ListAdapter<CartItem, OrderItemAdapter.ItemViewHolder> {
    private final Context context;

    public OrderItemAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CartItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
                    return oldItem.getProductId().equals(newItem.getProductId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCheckoutProductBinding binding =
                ItemCheckoutProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindView(getItem(position));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemCheckoutProductBinding binding;

        public ItemViewHolder(@NonNull ItemCheckoutProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindView(CartItem item) {
            Glide.with(binding.imageIcon.getContext())
                    .load(item.getImageUrl())
                    .into(binding.imageIcon);

            binding.textProductName.setText(item.getName());
            binding.textProductPrice.setText(DisplayFormat.toMoneyDisplayString(item.getPrice()));
            binding.textProductQuantity.setText(String.valueOf(item.getQuantity()));
        }
    }
}