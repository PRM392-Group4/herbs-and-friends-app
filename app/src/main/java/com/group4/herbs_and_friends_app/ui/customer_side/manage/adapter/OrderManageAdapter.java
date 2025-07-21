package com.group4.herbs_and_friends_app.ui.customer_side.manage.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.databinding.ItemOrderHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.OrderManageViewHolder> {

    private List<Order> orders = new ArrayList<>();
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderManageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderManageViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderManageViewHolder extends RecyclerView.ViewHolder {
        private ItemOrderHistoryBinding binding;

        public OrderManageViewHolder(@NonNull ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            Log.d("OrderManageAdapter", "Order ID: " + order.getId() + " has " + order.getTotalItemCount() + " items");
            
            binding.tvOrderNumber.setText(order.getOrderNumber());
            binding.tvOrderTime.setText(order.getPlacedAtDisplay());
            binding.tvOrderQuantity.setText(String.valueOf(order.getTotalItemCount()));
            binding.tvOrderTotal.setText(order.getTotalDisplay());
            binding.tvOrderStatus.setText(order.getStatusDisplay());

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }
    }
} 