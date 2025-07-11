package com.group4.herbs_and_friends_app.ui.cart.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.databinding.ItemHCartViewHolderBinding;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

public class HCartViewHolder extends RecyclerView.ViewHolder {

    // ==============================
    // === Fields
    // ==============================

    private ItemHCartViewHolderBinding binding;

    // ==============================
    // === Constructors
    // ==============================

    public HCartViewHolder(@NonNull ItemHCartViewHolderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // ==============================
    // === Methods
    // ==============================

    public void bindData(CartItem cartItem, IViewHolderListeners listeners) {

        // 1. Image
        Glide.with(binding.ivThumbnail.getContext())
                .load(cartItem.getImageUrl())
                .into(binding.ivThumbnail);

        // 2. Name, Price, Quantity
        binding.tvName.setText(cartItem.getName());
        binding.tvPrice.setText(DisplayFormat.toMoneyDisplayString(cartItem.getPrice()));
        binding.tvQty.setText(String.valueOf(cartItem.getQuantity()));

        // 3. Events
        binding.btnMinus.setOnClickListener(v -> listeners.onItemModifyQuantity(cartItem.getProductId(), cartItem.getQuantity(), -1));
        binding.btnPlus.setOnClickListener(v -> listeners.onItemModifyQuantity(cartItem.getProductId(), cartItem.getQuantity(), 1));
    }
}
