package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Coupon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HCouponSelectionAdapter extends RecyclerView.Adapter<HCouponSelectionAdapter.CouponViewHolder> {

    private final IOnCouponSelectedListener listener;
    private List<Coupon> couponList;

    public HCouponSelectionAdapter(List<Coupon> couponList, IOnCouponSelectedListener listener) {
        this.couponList = couponList != null ? couponList : new ArrayList<>();
        this.listener = listener;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList != null ? couponList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_selection, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.bind(coupon, listener);
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public interface IOnCouponSelectedListener {
        void onCouponSelected(Coupon coupon);
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

        public void bind(Coupon coupon, IOnCouponSelectedListener listener) {
            tvName.setText(coupon.getName());
            tvCode.setText("Mã: " + coupon.getCode());
            tvDiscount.setText(String.format(Locale.getDefault(), "-%.0f%%", coupon.getDiscount() * 100));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String validity = "Hiệu lực: " + sdf.format(coupon.getEffectiveDate()) + " - " + sdf.format(coupon.getExpiryDate());
            tvValidity.setText(validity);

            // Only selection functionality - no edit/delete buttons
            itemView.setOnClickListener(v -> listener.onCouponSelected(coupon));
        }
    }
} 