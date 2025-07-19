package com.group4.herbs_and_friends_app.ui.customer_side.checkout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Coupon;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HCouponSelectAdapter extends RecyclerView.Adapter<HCouponSelectAdapter.CouponViewHolder> {

    public interface OnCouponClickListener {
        void onCouponClick(Coupon coupon);
    }

    private List<Coupon> couponList;
    private final OnCouponClickListener listener;

    public HCouponSelectAdapter(List<Coupon> couponList, OnCouponClickListener listener) {
        this.couponList = couponList;
        this.listener = listener;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.bind(coupon, listener);
    }

    @Override
    public int getItemCount() {
        return couponList != null ? couponList.size() : 0;
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvDiscount, tvValidity;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_coupon_name);
            tvCode = itemView.findViewById(R.id.tv_coupon_code);
            tvDiscount = itemView.findViewById(R.id.tv_coupon_discount);
            tvValidity = itemView.findViewById(R.id.tv_coupon_validity);
        }

        public void bind(Coupon coupon, OnCouponClickListener listener) {
            tvName.setText(coupon.getName());
            tvCode.setText("Mã: " + coupon.getCode());
            tvDiscount.setText(String.format(Locale.getDefault(), "-%.0f%%", coupon.getDiscount()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String validity = "Hiệu lực: " + sdf.format(coupon.getEffectiveDate()) + " - " + sdf.format(coupon.getExpiryDate());
            tvValidity.setText(validity);

            itemView.setOnClickListener(v -> listener.onCouponClick(coupon));
        }
    }
}
