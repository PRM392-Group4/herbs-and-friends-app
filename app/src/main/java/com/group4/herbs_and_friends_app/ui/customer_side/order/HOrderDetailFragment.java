package com.group4.herbs_and_friends_app.ui.customer_side.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.data.model.enums.Role;
import com.group4.herbs_and_friends_app.data.model.enums.ShippingMethod;
import com.group4.herbs_and_friends_app.databinding.FragmentHOrderDetailBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarWithoutSearchBinding;
import com.group4.herbs_and_friends_app.ui.auth.login.HAuthVM;
import com.group4.herbs_and_friends_app.ui.customer_side.order.adapter.OrderItemAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HOrderDetailFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHOrderDetailBinding binding;
    private HOrderDetailVM orderDetailVM;
    private HAuthVM authVM;
    private OrderItemAdapter orderItemAdapter;
    private String orderId;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderDetailVM = new ViewModelProvider(this).get(HOrderDetailVM.class);

        // Initialize ViewModel for authentication
        authVM = new ViewModelProvider(this).get(HAuthVM.class);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get order ID from arguments
        if (getArguments() != null) {
            orderId = HOrderDetailFragmentArgs.fromBundle(getArguments()).getOrderId();
        }


        setupActionBar();
        setupRecyclerView();
        observeData();

        // Load order data
        if (orderId != null) {
            orderDetailVM.setOrderId(orderId);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupActionBar() {
        ViewHActionbarWithoutSearchBinding actionbarBinding = binding.includeActionbarOrderDetail;
        actionbarBinding.actionBarTitle.setText("Chi tiết đơn hàng");

        // Navigate back
        actionbarBinding.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    private void setupRecyclerView() {
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderItemAdapter = new OrderItemAdapter(requireContext());
        binding.rvOrderItems.setAdapter(orderItemAdapter);
    }

    private void observeData() {
        orderDetailVM.getOrderLive().observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                populateOrderDetails(order);
                setupOrderProcessing(order);
            } else {
                showOrderNotFound();
            }
        });
    }

    private void populateOrderDetails(Order order) {
        binding.tvOrderNumber.setText(order.getOrderNumber());
        binding.tvOrderStatus.setText(order.getStatusDisplay());
        binding.tvOrderTime.setText(order.getPlacedAtDisplay());
        binding.tvShippingMethod.setText(order.getShippingMethodDisplay());
        binding.tvPaymentMethod.setText(order.getPaymentMethodDisplay());

        binding.tvTransactionCode.setText("#" + order.getId());

        // Discount Code
        if (order.getCouponId() != null && !order.getCouponId().isEmpty()) {
            binding.tvDiscountCode.setText("#" + order.getCouponId());
        } else {
            binding.tvDiscountCode.setText("Không có");
        }

        // Note
        if (order.getNote() != null && !order.getNote().isEmpty()) {
            binding.tvNote.setText(order.getNote());
        } else {
            binding.tvNote.setText("Không có");
        }

        // Order Items
        if (order.getItems() != null) {
            orderItemAdapter.setOrderItems(order.getItems());
        }

        binding.tvRecipientName.setText(order.getRecipientName() != null ?
                order.getRecipientName() : "Chưa cập nhật");
        binding.tvRecipientPhone.setText(order.getRecipientPhone() != null ?
                order.getRecipientPhone() : "Chưa cập nhật");
        binding.tvRecipientAddress.setText(order.getAddress() != null ?
                order.getAddress() : "Chưa cập nhật");

        // Payment Details
        binding.tvSubtotal.setText(order.getSubtotalDisplay());
        binding.tvShippingFee.setText("+ " + order.getShippingFeeDisplay());
        binding.tvDiscount.setText("- " + order.getDiscountDisplay());
        Log.d("OrderDetail", "Discount: " + order.getDiscountDisplay() + "for order ID: " + order.getId());
        binding.tvTotal.setText(order.getTotalDisplay());
    }

    private void showOrderNotFound() {
        binding.tvOrderNumber.setText("Không tìm thấy đơn hàng");
        binding.tvOrderStatus.setText("Lỗi");
    }

    private void setupOrderProcessing(Order currentOrder) {
        String curStatus = currentOrder.getStatus();
        if (OrderStatus.CANCELLED.getValue().equals(curStatus) || OrderStatus.COMPLETED.getValue().equals(curStatus)) {
            binding.orderProcessFooter.setVisibility(View.GONE);
            return;
        }
        if (currentUser.getUid() != null) {
            authVM.fetchUser(currentUser.getUid(), user -> {
                        if (user != null) {
                            // Check if the user is a customer
                            if (!Role.ADMIN.getValue().trim().equalsIgnoreCase(user.getRole())) {
                                return;
                            }
                            binding.orderProcessFooter.setVisibility(View.VISIBLE);
                            binding.btnCancel.setOnClickListener(v -> {
                                orderDetailVM.cancelOrder(orderId).observe(getViewLifecycleOwner(), success -> {
                                    if (success) {
                                        Log.d("HOrderDetailFragment", "Order cancelled successfully");
                                        Toast.makeText(getContext(), "Đơn hàng đã được hủy",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e("HOrderDetailFragment", "Failed to cancel order");
                                    }
                                });
                            });
                            if (OrderStatus.UNPAID.getValue().equals(curStatus)) {
                                binding.btnProcess.setVisibility(View.GONE);
                            } else if (OrderStatus.PENDING.getValue().equals(curStatus)) {
                                setNextOrderStatus(OrderStatus.CONFIRMED);
                            } else if (OrderStatus.CONFIRMED.getValue().equals(curStatus)) {
                                if (ShippingMethod.PICKUP.getValue().equals(currentOrder.getShippingMethod())) {
                                    setNextOrderStatus(OrderStatus.COMPLETED);
                                } else {
                                    setNextOrderStatus(OrderStatus.SHIPPING);
                                }
                            } else if (OrderStatus.SHIPPING.getValue().equals(curStatus)) {
                                setNextOrderStatus(OrderStatus.COMPLETED);
                            }
                        }
                    }
                    , () -> Log.e("HOrderDetailFragment", "Failed to fetch user"));

        }
        ;

    }

    private void setNextOrderStatus(OrderStatus nextStatus) {
        binding.btnProcess.setText(nextStatus.getDisplayName());
        binding.btnProcess.setOnClickListener(v -> {
            orderDetailVM.updateOrderStatus(orderId, nextStatus.getValue())
                    .observe(getViewLifecycleOwner(), success -> {
                        if (success) {
                            Log.d("HOrderDetailFragment", "Order completed successfully");
                            Toast.makeText(requireContext(),
                                    "Đơn hàng đã được cập nhật thành: " + nextStatus.getDisplayName(), Toast.LENGTH_LONG).show();

                        } else {
                            Log.e("HOrderDetailFragment", "Failed to complete order");
                        }
                    });
        });
    }
}