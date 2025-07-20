package com.group4.herbs_and_friends_app.ui.customer_side.checkout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.api.OrderSchema;
import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.OrderItem;
import com.group4.herbs_and_friends_app.data.model.enums.PaymentMethod;
import com.group4.herbs_and_friends_app.data.model.enums.ShippingMethod;
import com.group4.herbs_and_friends_app.data.repository.CartRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

@HiltViewModel
public class HCheckoutVM extends ViewModel {

    private final OrderRepository repository;
    private final CartRepository cartRepo;
    // ========== LiveData for Order Data ==========
    private final LiveData<List<CartItem>> orderItems;
    private final MutableLiveData<List<CartItem>> fastCheckoutItem =
            new MutableLiveData<>(new ArrayList<CartItem>());
    private final LiveData<List<Coupon>> couponsList;
    private final MediatorLiveData<Long> totalPrice = new MediatorLiveData<>(0L);
    private final LiveData<Long> subTotal;
    private final MutableLiveData<Long> discountPrice;
    private final MutableLiveData<String> note = new MutableLiveData<>("");
    private final MutableLiveData<ShippingMethod> shippingMethod =
            new MutableLiveData<>(ShippingMethod.STANDARD);
    private final MutableLiveData<Long> shippingFee;
    private final MutableLiveData<PaymentMethod> paymentMethod =
            new MutableLiveData<>(PaymentMethod.ZALOPAY);
    private final MutableLiveData<String> orderId = new MutableLiveData<>(null);
    private final MutableLiveData<Coupon> coupon = new MutableLiveData<>(null);
    private final MutableLiveData<String> address = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> orderCreated = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFastCheckout = new MutableLiveData<>(false);

    @Inject
    public HCheckoutVM(OrderRepository repository, CartRepository cartRepo) {
        this.repository = repository;
        this.cartRepo = cartRepo;
        this.orderItems = cartRepo.getLiveCartItems();
        this.subTotal = cartRepo.getLiveTotalPrice();
        this.shippingFee = new MutableLiveData<>(ShippingMethod.STANDARD.getPrice());
        this.discountPrice = new MutableLiveData<>(0L);
        this.couponsList = repository.loadCoupons();
        totalPrice.addSource(subTotal, value -> calculateTotal());
        totalPrice.addSource(shippingFee, value -> calculateTotal());
        totalPrice.addSource(discountPrice, value -> calculateTotal());
        calculateTotal();
    }

    // ========== Getters for UI ==========

    public MutableLiveData<String> getOrderId() {
        return orderId;
    }
    public LiveData<List<CartItem>> getFastCheckoutItem() {
        return fastCheckoutItem;
    }

    public LiveData<List<CartItem>> getOrderItems() {
        return orderItems;
    }

    public LiveData<Long> getSubTotal() {
        return !fastCheckoutItem.getValue().isEmpty() ? calculateFastCheckoutSubTotal() :
                this.subTotal;
    }

    public LiveData<Boolean> getIsFastCheckout() {
        return isFastCheckout;
    }

    public LiveData<Long> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<List<Coupon>> getCouponsList() {
        return couponsList;
    }

    public LiveData<ShippingMethod> getShippingMethod() {
        return shippingMethod;
    }

    public LiveData<Long> getShippingFee() {
        return shippingFee;
    }

    public LiveData<PaymentMethod> getPaymentMethod() {
        return paymentMethod;
    }

    public LiveData<Coupon> getCoupon() {
        return coupon;
    }

    public MutableLiveData<Long> getDiscountPrice() {
        return discountPrice;
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public LiveData<Boolean> getOrderCreated() {
        return orderCreated;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // ========== Setters (used by Fragment binding) ==========
    public void setFastCheckoutItem(CartItem item) {
        List<CartItem> currentItems = fastCheckoutItem.getValue();
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }
        currentItems.clear(); // Clear previous items for fast checkout
        currentItems.add(item);
        fastCheckoutItem.setValue(currentItems);
        isFastCheckout.setValue(true);
        calculateTotal();
    }

    public void setShippingMethod(ShippingMethod value) {
        shippingMethod.setValue(value);
        shippingFee.setValue(value.getPrice());
        calculateTotal();
    }

    public void setPaymentMethod(PaymentMethod value) {
        paymentMethod.setValue(value);
    }

    public void setCoupon(Coupon value) {
        coupon.setValue(value);
        double discountPercent = value != null ? value.getDiscount() : 0f;
        long sub = subTotal.getValue() != null ? subTotal.getValue() : 0;
        long fee = shippingFee.getValue() != null ? shippingFee.getValue() : 0;
        long discountAmount = (long) ((sub + fee) * discountPercent);
        discountPrice.setValue(discountAmount);
        calculateTotal();
    }

    public void setAddress(String value) {
        address.setValue(value);
    }

    // ========== Total Calculation ==========
    private void calculateTotal() {
        long sub = getSubTotal().getValue() != null ? getSubTotal().getValue() : 0L;
        long fee = getShippingFee().getValue() != null ? getShippingFee().getValue() : 0L;
        long discount = getDiscountPrice().getValue() != null ? getDiscountPrice().getValue() : 0L;
        totalPrice.setValue(sub + fee - discount);
    }

    private LiveData<Long> calculateFastCheckoutSubTotal() {
        MutableLiveData<Long> fastSubTotal = new MutableLiveData<>(0L);
        CartItem item = fastCheckoutItem.getValue().get(0);
        if (item != null) {
            long total = 0;
            total += item.getPrice() * item.getQuantity();
            fastSubTotal.setValue(total);
        }
        return fastSubTotal;
    }

    // ========== Trigger Order Creation ==========
    public LiveData<String> createOrder(Order order) {
        MutableLiveData<String> result = new MutableLiveData<>();
        // Save order to Firestore
        repository.createOrder(order).observeForever(success -> {
            if (success!=null) {
                if (!isFastCheckout.getValue()) {
                    cartRepo.clearCart().observeForever(clearSuccess -> {
                        if (clearSuccess) {
                            Log.d("HCheckoutVM", "Cart cleared successfully");
                        } else {
                            Log.e("HCheckoutVM", "Failed to clear cart");
                            errorMessage.setValue("Failed to clear cart");
                        }
                    });
                }
                orderCreated.setValue(true);
                orderId.setValue(success);
                result.setValue(success);
            } else {
                orderCreated.setValue(false);
            }
        });
        orderId.postValue(result.getValue());
        return result;
    }

    public List<OrderItem> getOrderProducts() {
        return fromCartToOrderItem(isFastCheckout.getValue() ? fastCheckoutItem.getValue() :
                orderItems.getValue());
    }

    public int getTotalItems() {
        return isFastCheckout.getValue() ? fastCheckoutItem.getValue().get(0).getQuantity() :
                orderItems.getValue().size();
    }

    private static List<OrderItem> fromCartToOrderItem(List<CartItem> items) {
        List<OrderItem> list = new ArrayList<>();
        if (items == null) {
            return list;
        }
        for (CartItem item : items) {
            OrderItem obj = new OrderItem();
            obj.setProductId(item.getProductId());
            obj.setName(item.getName());
            obj.setQuantity(item.getQuantity());
            obj.setUnitPrice(item.getPrice());
            list.add(obj);
        }
        return list;
    }

    public void processPayment(Activity activity, RedirectCallback redirect){
        OrderSchema orderApi = new OrderSchema();
        try {
            JSONObject data = orderApi.createOrder(String.format("%.0f",(double)
                    getTotalPrice().getValue()));
            String code = data.getString("return_code");

            if (code != null && code.equals("1")) {
                Bundle bundle = new Bundle();
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(activity, token, "demozpdk://app",
                        new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Log.d("ZaloPay", "Payment succeeded: " + s + ", " + s1 + ", " + s2);

                                bundle.putString("result", "Thanh toán thành công");
                                bundle.putString("total",
                                        "Bạn đã thanh toán " + DisplayFormat.toMoneyDisplayString(getTotalPrice().getValue()));
                                bundle.putString("order_id", getOrderId().getValue());
                                redirect.onRedirect(bundle);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Log.d("ZaloPay", "Payment failed: " + s + ", " + s + ", " + s1);
                                Bundle bundle = new Bundle();
                                bundle.putString("result", "Thanh toán đã được hủy");
                                bundle.putString("order_id", getOrderId().getValue());

                                redirect.onRedirect(bundle);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Log.d("ZaloPay", "Payment error: " + s + ", " + s1);

                                Bundle bundle = new Bundle();
                                bundle.putString("result", "Lỗi thanh toán: " + zaloPayError.toString());
                                bundle.putString("order_id", getOrderId().getValue());
                                redirect.onRedirect(bundle);
                            }
                        });

            }
        } catch (Exception e) {
            Log.d("Payment Error", e.getMessage());
            Toast.makeText(activity.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
        }

    }
    public interface RedirectCallback{
        void onRedirect(Bundle bundle);
    }

}