package com.group4.herbs_and_friends_app.ui.customer_side.checkout;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.data.model.enums.PaymentMethod;
import com.group4.herbs_and_friends_app.data.model.enums.ShippingMethod;
import com.group4.herbs_and_friends_app.databinding.FragmentHCheckoutBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarWithoutSearchBinding;
import com.group4.herbs_and_friends_app.ui.admin_side.coupon_management.adapters.HCouponSelectionAdapter;
import com.group4.herbs_and_friends_app.ui.auth.login.HAuthVM;
import com.group4.herbs_and_friends_app.ui.customer_side.checkout.adapter.OrderItemAdapter;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import java.util.ArrayList;
import java.util.Date;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCheckoutFragment extends Fragment {

    private FragmentHCheckoutBinding binding;
    private OrderItemAdapter adapter;
    private HCheckoutVM checkoutVM;
    private HAuthVM authVM;
    private FirebaseUser currentUser;
    private Bundle pendingNavigationBundle;

    public static HCheckoutFragment newInstance() {
        return new HCheckoutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        checkoutVM = new ViewModelProvider(this).get(HCheckoutVM.class);
        authVM = new ViewModelProvider(this).get(HAuthVM.class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Hãy đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle args = getArguments();
        if (args != null && args.containsKey("fastCheckoutItem_id")) {
            String id = String.valueOf(args.getString("fastCheckoutItem_id"));
            String name = String.valueOf(args.getString("fastCheckoutItem_name"));
            String img = String.valueOf(args.getString("fastCheckoutItem_imageUrl"));
            int quantity = args.getInt("fastCheckoutItem_quantity");
            Long unitPrice = args.getLong("fastCheckoutItem_unitPrice");
            CartItem item = new CartItem(id, name, unitPrice, img, quantity);

            if (item != null) {
                checkoutVM.setFastCheckoutItem(item);
            }
        }

        setActionBar();
        setOrderProductView();
        setupObserverOrderItems();
        setupButtonSelectCoupon();
        setPriceDisplay();
        setShippingMethodAction();
        setPaymentMethodAction();
        setupEditAddressAction();
        binding.btnCheckout.setOnClickListener(v -> {
            placeOrder();
        });
    }

    public void onResume() {
        super.onResume();
        if (pendingNavigationBundle != null) {
            Log.d("HCheckoutFragment", "Processing pending navigation: result=" + pendingNavigationBundle.getString("result"));
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_HCheckoutFragment_to_HOrderResultFragment, pendingNavigationBundle);
            pendingNavigationBundle = null;
        }
    }

    private void setActionBar() {
        ViewHActionbarWithoutSearchBinding actionBar = binding.includeActionbarCheckout;
        actionBar.actionBarTitle.setText(R.string.checkout_title);
        actionBar.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setOrderProductView() {
        adapter = new OrderItemAdapter(getContext());
        binding.recyclerOrderItems.setAdapter(adapter);
        binding.recyclerOrderItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void setupObserverOrderItems() {
        checkoutVM.getOrderItems().observe(getViewLifecycleOwner(), items -> {
            if (!checkoutVM.getIsFastCheckout().getValue() && items != null && !items.isEmpty()) {
                adapter.submitList(new ArrayList<>(items));
            } else if (checkoutVM.getIsFastCheckout().getValue()) {
                adapter.submitList(checkoutVM.getFastCheckoutItem().getValue());
            }
        });
    }

    private void setShippingMethodAction() {
        binding.radioGroupShipping.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioExpress) {
                checkoutVM.setShippingMethod(ShippingMethod.EXPRESS);
                binding.textReceiverAddress.setVisibility(VISIBLE);
            } else if (checkedId == R.id.radioStandard) {
                checkoutVM.setShippingMethod(ShippingMethod.STANDARD);
                binding.textReceiverAddress.setVisibility(VISIBLE);
            } else {
                checkoutVM.setShippingMethod(ShippingMethod.PICKUP);
                binding.textReceiverAddress.setVisibility(GONE);
            }
        });
    }

    private void setPaymentMethodAction() {
        binding.radioGroupPayment.setOnCheckedChangeListener((group, checked) -> {
            if (checked == R.id.radioZalo) {
                checkoutVM.setPaymentMethod(PaymentMethod.ZALOPAY);
            } else {
                checkoutVM.setPaymentMethod(PaymentMethod.CASH);
                binding.btnCheckout.setText("Đặt hàng");
            }
        });
    }

    private void setPriceDisplay() {
        checkoutVM.getTotalPrice().observe(getViewLifecycleOwner(), price -> {
            binding.textSubtotal.setText(
                    DisplayFormat.toMoneyDisplayString(checkoutVM.getSubTotal().getValue()));
            binding.textShippingFee.setText(
                    DisplayFormat.toMoneyDisplayString(checkoutVM.getShippingFee().getValue()));
            binding.textDiscount.setText(
                    DisplayFormat.toMoneyDisplayString(checkoutVM.getDiscountPrice().getValue()));
            binding.textTotal.setText(
                    DisplayFormat.toMoneyDisplayString(checkoutVM.getTotalPrice().getValue()));
            binding.textBottomTotal.setText(
                    DisplayFormat.toMoneyDisplayString(checkoutVM.getTotalPrice().getValue()));
        });
    }

    private void placeOrder() {
        // Collect form data
        String address = checkoutVM.getAddress().getValue();
        String recipientName = checkoutVM.getRecipientName().getValue();
        String recipientPhone = checkoutVM.getRecipientPhone().getValue();

        ShippingMethod shippingMethod = checkoutVM.getShippingMethod().getValue();
        if (recipientName == null
                || recipientPhone == null
                || address == null
                || recipientName.isEmpty()
                || recipientPhone.isEmpty()
                || (shippingMethod != ShippingMethod.PICKUP && address.isEmpty())) {
            binding.txtErrorAddress.setText("Vui lòng nhập đầy đủ thông tin.");
            binding.txtErrorAddress.setVisibility(VISIBLE);
            return;
        }
        PaymentMethod paymentMethod = checkoutVM.getPaymentMethod().getValue();
        Coupon coupon = checkoutVM.getCoupon().getValue();
        Long total = checkoutVM.getTotalPrice().getValue();

        // Create Order object
        Order order = new Order();
        order.setUserId(currentUser.getUid());
        order.setStatus(paymentMethod == PaymentMethod.ZALOPAY ?
                OrderStatus.UNPAID.getValue() : OrderStatus.PENDING.getValue());
        order.setTotal(total != null ? total : 0);
        order.setPaymentMethod(paymentMethod != null ? paymentMethod.getValue() : PaymentMethod.MOMO.getValue());
        order.setShippingMethod(shippingMethod != null ? shippingMethod.getValue() : ShippingMethod.STANDARD.getValue());
        order.setCouponId(binding.etCouponId.getText().toString());
//        order.setCoupon(coupon != null ? FirebaseFirestore.getInstance().collection("coupons").document(coupon.getId()) : null);
        order.setPlacedAt(new Date());
        order.setNote(binding.editNote.getText().toString()); // Optional, not in UI
        order.setAddress(address);
        order.setRecipientName(recipientName);
        order.setRecipientPhone(recipientPhone);
        order.setUserId(currentUser.getUid());
        order.setItems(checkoutVM.getOrderProducts());

        // Create order and observe result
        checkoutVM.createOrder(order).observe(getViewLifecycleOwner(), success -> {
            if (checkoutVM.getOrderCreated().getValue() && success != null) {
                Toast.makeText(getContext(), "Tạo đơn hàng thành công", Toast.LENGTH_SHORT).show();
                if (checkoutVM.getPaymentMethod().getValue() == PaymentMethod.ZALOPAY) {
                    checkoutVM.processPayment(requireActivity(), bundle -> {
                        Log.d("HCheckoutFragment", "Redirecting with bundle: result=" + bundle.getString("result") +
                                ", total=" + bundle.getString("total") + ", orderId=" + bundle.getString("order_id"));
                        if (isResumed()) {
                            NavController navController = NavHostFragment.findNavController(HCheckoutFragment.this);
                            navController.navigate(R.id.action_HCheckoutFragment_to_HOrderResultFragment, bundle);
                        } else {
                            pendingNavigationBundle = bundle;
                            Log.d("HCheckoutFragment", "Fragment not resumed, storing pending navigation");
                        }
                    });
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("result", "Đặt hàng thành công");
                    bundle.putString("total",
                            "Đơn hàng của bạn có tổng giá trị là " + DisplayFormat.toMoneyDisplayString(checkoutVM.getTotalPrice().getValue()));
                    bundle.putString("order_id", checkoutVM.getOrderId().getValue());
                    NavController navController = NavHostFragment.findNavController(HCheckoutFragment.this);
                    navController.navigate(R.id.action_HCheckoutFragment_to_HOrderResultFragment, bundle);
                }
            } else {
                Toast.makeText(getContext(), "Lỗi tạo đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupEditAddressAction() {
        if (currentUser.getUid() != null) {
            authVM.fetchUser(currentUser.getUid(), user -> {
                if (user == null) {
                    binding.textReceiverAddress.setText("Địa chỉ nhận hàng");
                    binding.textReceiverPhone.setText("Số điện thoại");
                    binding.textReceiverName.setText("Tên người nhận");
                    return;
                }
                // Set initial values for address fields
                binding.textReceiverName.setText(user.getName());
                binding.textReceiverPhone.setText(user.getPhone());
                binding.textReceiverAddress.setText(user.getAddress());
                checkoutVM.setRecipientName(user.getName());
                checkoutVM.setRecipientPhone(user.getPhone());
                checkoutVM.setAddress(user.getAddress());
            }, () -> Log.e("HCheckoutFragment", "Failed to fetch user"));
        }
        binding.btnEditAddress.setOnClickListener(v -> {
            // Get current values from UI
            String currentName = checkoutVM.getRecipientName().getValue();
            String currentPhone = checkoutVM.getRecipientPhone().getValue();
            String currentAddress = checkoutVM.getAddress().getValue();

            // Show dialog with current values
            HEditAddressDialog dialog = HEditAddressDialog.newInstance(currentName, currentPhone,
                    currentAddress, checkoutVM.getShippingMethod().getValue() == ShippingMethod.PICKUP);
            dialog.setOnAddressUpdatedListener((recipientName, recipientPhone, address) -> {
                // Update UI
                binding.textReceiverName.setText(recipientName);
                binding.textReceiverPhone.setText(recipientPhone);
                binding.textReceiverAddress.setText(address);
                // Update ViewModel
                checkoutVM.setAddress(address);
                checkoutVM.setRecipientName(recipientName);
                checkoutVM.setRecipientPhone(recipientPhone);
                Log.d("HCheckoutFragment", "Address updated: name=" + recipientName + ", phone=" + recipientPhone + ", address=" + address);
            });
            dialog.show(getParentFragmentManager(), "HEditAddressDialog");
        });
    }

    /**
     * Setup button select coupon
     */

    private void setupButtonSelectCoupon() {
        binding.btnApplyCoupon.setOnClickListener(v -> {

            HCouponSelectBottomSheet bottomSheet = new HCouponSelectBottomSheet();
            bottomSheet.setOnCouponSelectedListener(new HCouponSelectionAdapter.IOnCouponSelectedListener() {
                @Override
                public void onCouponSelected(Coupon coupon) {
                    binding.etCouponCode.setText(coupon.getCode());
                    binding.etCouponId.setText(coupon.getId());
                    checkoutVM.setCoupon(coupon);
                }
            });
            bottomSheet.show(getParentFragmentManager(), "HCouponSelectBottomSheet");
        });
    }

}