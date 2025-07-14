package com.group4.herbs_and_friends_app.ui.profile.adapter;

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

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderHistoryAdapter(Context context, OnOrderClickListener listener) {
        this.context = context;
        this.orders = new ArrayList<>();
        this.onOrderClickListener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrId, tvOrStatus, tvOrTime, tvOrQuantity, tvOrTotal;

        public OrderHistoryViewHolder(@NonNull View itemView) {
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
            tvOrId.setText(order.getOrderNumber());
            tvOrStatus.setText(order.getStatusDisplay() + " >");
            tvOrTime.setText(order.getPlacedAtDisplay());
            tvOrQuantity.setText(String.valueOf(order.getTotalItemCount()));
            Log.d("quantity of order id: ", order.getId() + " is " + order.getTotalItemCount());
            tvOrTotal.setText(order.getTotalDisplay().replace(" đ", ""));
        }
    }
} 