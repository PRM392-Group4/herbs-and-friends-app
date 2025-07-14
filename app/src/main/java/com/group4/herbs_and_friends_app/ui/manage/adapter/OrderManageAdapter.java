package com.group4.herbs_and_friends_app.ui.manage.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.OrderManageViewHolder> {

    private Context context;
    private List<Order> orders = new ArrayList<>();
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderManageAdapter(Context context, OnOrderClickListener listener) {
        this.context = context;
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderManageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderManageViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void clearOrders() {
        this.orders.clear();
        notifyDataSetChanged();
    }

    class OrderManageViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrId, tvOrStatus, tvOrTime, tvOrQuantity, tvOrTotal;

        public OrderManageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrId = itemView.findViewById(R.id.tvOrId);
            tvOrStatus = itemView.findViewById(R.id.tvOrStatus);
            tvOrTime = itemView.findViewById(R.id.tvOrTime);
            tvOrQuantity = itemView.findViewById(R.id.tvOrQuantity);
            tvOrTotal = itemView.findViewById(R.id.tvOrTotal);

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (onOrderClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onOrderClickListener.onOrderClick(orders.get(position));
                    }
                }
            });
        }

        public void bind(Order order) {
            // Set order ID
            tvOrId.setText(order.getOrderNumber());
            
            // Set status with arrow
            tvOrStatus.setText(order.getStatusDisplay() + " >");
            
            // Set time
            tvOrTime.setText(order.getPlacedAtDisplay());
            
            // Set quantity
            tvOrQuantity.setText(String.valueOf(order.getTotalItemCount()));
            Log.d("OrderManageAdapter", "Order ID: " + order.getId() + " has " + order.getTotalItemCount() + " items");
            
            // Set total (remove the "đ" from display as it's added in the layout)
            tvOrTotal.setText(order.getTotalDisplay().replace(" đ", ""));
        }
    }
} 