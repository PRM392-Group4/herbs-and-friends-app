package com.group4.herbs_and_friends_app.ui.cart.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.databinding.ItemHCartViewHolderBinding;
import com.group4.herbs_and_friends_app.ui.cart.viewholder.HCartViewHolder;
import com.group4.herbs_and_friends_app.ui.cart.viewholder.IViewHolderListeners;

public class HCartRecycleViewAdapter extends ListAdapter<CartItem, HCartViewHolder> {


    // ===========================
    // === Fields
    // ===========================

    private final IViewHolderListeners listeners;

    // ===========================
    // === Constructors
    // ===========================

    public HCartRecycleViewAdapter(IViewHolderListeners listeners) {
        super(DIFF_CALLBACK);
        this.listeners = listeners;
    }

    // ===========================
    // === Methods
    // ===========================

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartItem>() {

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
    public HCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHCartViewHolderBinding binding = ItemHCartViewHolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HCartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HCartViewHolder holder, int position) {
        CartItem cartItem = getItem(position);
        holder.bindData(cartItem, listeners);
    }
}
