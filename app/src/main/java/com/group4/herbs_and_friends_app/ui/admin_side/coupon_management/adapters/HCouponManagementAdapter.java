package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Coupon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HCouponManagementAdapter extends RecyclerView.Adapter<HCouponManagementAdapter.CouponViewHolder> {

    private List<Coupon> couponList;
    private final ICouponActionListener listener;

    public HCouponManagementAdapter(List<Coupon> couponList, ICouponActionListener listener) {
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
        return couponList.size();
    }

    public interface ICouponActionListener {
        void onCouponEditClick(String couponId);
        void onCouponDeleteClick(String couponId, String couponName);
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvDiscount, tvValidity;
        MaterialButton btnEdit, btnDelete;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_coupon_name);
            tvCode = itemView.findViewById(R.id.tv_coupon_code);
            tvDiscount = itemView.findViewById(R.id.tv_coupon_discount);
            tvValidity = itemView.findViewById(R.id.tv_coupon_validity);
            btnEdit = itemView.findViewById(R.id.btn_edit_coupon);
            btnDelete = itemView.findViewById(R.id.btn_delete_coupon);
        }

        public void bind(Coupon coupon, ICouponActionListener listener) {
            tvName.setText(coupon.getName());
            tvCode.setText("Mã: " + coupon.getCode());
            tvDiscount.setText(String.format(Locale.getDefault(), "-%.0f%%", coupon.getDiscount() * 100));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String validity = "Hiệu lực: " + sdf.format(coupon.getEffectiveDate()) + " - " + sdf.format(coupon.getExpiryDate());
            tvValidity.setText(validity);

            btnEdit.setOnClickListener(v -> listener.onCouponEditClick(coupon.getId()));
            btnDelete.setOnClickListener(v -> listener.onCouponDeleteClick(coupon.getId(), coupon.getName()));
        }
    }
} 