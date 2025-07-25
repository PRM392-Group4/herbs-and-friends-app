package com.group4.herbs_and_friends_app.ui.customer_side.checkout;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.FragmentHOrderResultBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HOrderResultFragment extends Fragment {

    private FragmentHOrderResultBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHOrderResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get arguments from Bundle
        Bundle args = getArguments();
        String result = args != null ? args.getString("result", "Processing...") : "Processing...";
        String orderId = args != null ? args.getString("order_id", "N/A") : "N/A";
        String total = args != null ? args.getString("total", "") : "";

        // Set result text and color based on status
        binding.textResult.setText(result);
        if (result.contains("thành công")) {
            binding.imgPaymentStatus.setImageResource(R.drawable.ic_check_circle);
            binding.textResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (result.contains("hủy") || result.contains("Lỗi")) {
            binding.imgPaymentStatus.setImageResource(R.drawable.ic_cancel);
            binding.textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        // Set order ID
        binding.textOrderId.setText("Mã đơn hàng: " + orderId);

        // Set payment time
        String paymentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());
        if (result.contains("Thanh toán")) {
            binding.textPaymentTime.setText("Thanh toán lúc: " + paymentTime);
        } else {
            binding.textPaymentTime.setText("Đặt hàng lúc: " + paymentTime);
        }

        if (!total.isEmpty()) {
            binding.textTotal.setText(total);
            binding.textTotal.setVisibility(VISIBLE);
        } else {
            binding.textTotal.setVisibility(GONE);
        }

        // Log for debugging
        Log.d("HOrderResultFragment", "Displaying result: result=" + result + ", orderId=" + orderId + ", total=" + total);

        // Set button listeners
        binding.buttonBackHome.setOnClickListener(v -> {
            Log.d("HOrderResultFragment", "Navigating to HHomeFragment");
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_HOrderResultFragment_to_HHomeFragment);
        });

        binding.buttonOrderDetails.setOnClickListener(v -> {
            Log.d("HOrderResultFragment", "Navigating to HOrderDetailsFragment with orderId=" + orderId);
            navigateToOrderDetail(orderId);
        });
    }

    public void navigateToOrderDetail(String orderId) {
        NavController navController = NavHostFragment.findNavController(this);
        HOrderResultFragmentDirections.ActionHOrderResultFragmentToHOrderDetailsFragment action =
                HOrderResultFragmentDirections.actionHOrderResultFragmentToHOrderDetailsFragment(orderId);
        navController.navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}