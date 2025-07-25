package com.group4.herbs_and_friends_app.ui.customer_side.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private Context context;
    private List<OrderItem> orderItems;

    public OrderItemAdapter(Context context) {
        this.context = context;
        this.orderItems = new ArrayList<>();
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductQuantity;

        private ImageView imgUrl;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            imgUrl = itemView.findViewById(R.id.ivProductImage);
        }

        public void bind(OrderItem item) {
            tvProductName.setText(item.getName());
            tvProductPrice.setText(item.getUnitPriceDisplay());
            tvProductQuantity.setText("x" + item.getQuantity());

            if (item.getImgUrl() != null && !item.getImgUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImgUrl())
                        .placeholder(R.drawable.ic_category_all)
                        .error(R.drawable.ic_category_all)
                        .into(imgUrl);
            } else {
                imgUrl.setImageResource(R.drawable.ic_category_all);
            }
        }
    }
} 