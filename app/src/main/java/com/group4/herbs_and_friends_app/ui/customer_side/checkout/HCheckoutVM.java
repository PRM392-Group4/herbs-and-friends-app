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
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.OrderItem;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.data.model.enums.PaymentMethod;
import com.group4.herbs_and_friends_app.data.model.enums.ShippingMethod;
import com.group4.herbs_and_friends_app.data.repository.CartRepository;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;

@HiltViewModel
public class HCheckoutVM extends ViewModel {

    private final OrderRepository repository;
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final CouponRepository couponRepository;

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
    private final MutableLiveData<String> recipientName = new MutableLiveData<>("");
    private final MutableLiveData<String> recipientPhone = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> orderCreated = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFastCheckout = new MutableLiveData<>(false);

    // ========== Communications ==========
    private final NotificationPublisher notificationPublisher;

    @Inject
    public HCheckoutVM(OrderRepository repository, CartRepository cartRepo,
                       ProductRepository productRepo, CouponRepository couponRepository, NotificationPublisher notificationPublisher) {
        this.repository = repository;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.couponRepository = couponRepository;
        this.orderItems = cartRepo.getLiveCartItems();
        this.subTotal = cartRepo.getLiveTotalPrice();
        this.shippingFee = new MutableLiveData<>(ShippingMethod.STANDARD.getPrice());
        this.discountPrice = new MutableLiveData<>(0L);
        this.couponsList = repository.loadCoupons();
        totalPrice.addSource(subTotal, value -> calculateTotal());
        totalPrice.addSource(shippingFee, value -> calculateTotal());
        totalPrice.addSource(discountPrice, value -> calculateTotal());
        this.notificationPublisher = notificationPublisher;
        calculateTotal();
    }

    // ========== Getters for UI ==========

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

    public MutableLiveData<String> getOrderId() {
        return orderId;
    }

    public LiveData<List<CartItem>> getFastCheckoutItem() {
        return fastCheckoutItem;
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

    public void setShippingMethod(ShippingMethod value) {
        shippingMethod.setValue(value);
        shippingFee.setValue(value.getPrice());
        calculateTotal();
    }

    public LiveData<Long> getShippingFee() {
        return shippingFee;
    }

    public LiveData<PaymentMethod> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod value) {
        paymentMethod.setValue(value);
    }

    public LiveData<Coupon> getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon value) {
        coupon.setValue(value);
        double discountPercent = value != null ? value.getDiscount() : 0f;
        long sub = subTotal.getValue() != null ? subTotal.getValue() : 0;
        long fee = shippingFee.getValue() != null ? shippingFee.getValue() : 0;
        long discountAmount = (long) ((sub) * discountPercent);
        discountPrice.setValue(discountAmount);
        calculateTotal();
    }

    public MutableLiveData<Long> getDiscountPrice() {
        return discountPrice;
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public void setAddress(String value) {
        address.setValue(value);
    }

    public LiveData<String> getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String value) {
        recipientName.setValue(value);
    }

    public LiveData<String> getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String value) {
        recipientPhone.setValue(value);
    }

    public LiveData<Boolean> getOrderCreated() {
        return orderCreated;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
            if (success != null) {
                orderCreated.setValue(true);

                productRepo.modifyProductStock(getOrderProducts(), false);

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

    public void processPayment(Activity activity, RedirectCallback redirect) {
        OrderSchema orderApi = new OrderSchema();
        try {
            JSONObject data = orderApi.createOrder(String.format("%.0f", (double)
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

                                repository.updateOrderStatus(orderId.getValue(),
                                        OrderStatus.PENDING.getValue());

                                if (!isFastCheckout.getValue()) {
                                    cartRepo.clearCart().observeForever(success -> {
                                        if (success) {
                                            Log.d("HCheckoutVM", "Cart cleared successfully");
                                        } else {
                                            Log.e("HCheckoutVM", "Failed to clear cart");
                                            errorMessage.setValue("Failed to clear cart");
                                        }
                                    });
                                }

                                redirect.onRedirect(bundle);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Log.d("ZaloPay", "Payment failed: " + s + ", " + s + ", " + s1);
                                Bundle bundle = new Bundle();
                                bundle.putString("result", "Thanh toán đã được hủy");
                                bundle.putString("order_id", getOrderId().getValue());

                                repository.updateOrderStatus(orderId.getValue(),
                                        OrderStatus.CANCELLED.getValue());
                                productRepo.modifyProductStock(getOrderProducts(), true);

                                redirect.onRedirect(bundle);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Log.d("ZaloPay", "Payment error: " + s + ", " + s1);

                                Bundle bundle = new Bundle();
                                bundle.putString("result", "Lỗi thanh toán: " + zaloPayError.toString());
                                bundle.putString("order_id", getOrderId().getValue());

                                repository.updateOrderStatus(orderId.getValue(),
                                        OrderStatus.CANCELLED.getValue());
                                productRepo.modifyProductStock(getOrderProducts(), true);

                                redirect.onRedirect(bundle);
                            }
                        });

            }
        } catch (Exception e) {
            Log.d("Payment Error", e.getMessage());
            Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }

    }

    public interface RedirectCallback {
        void onRedirect(Bundle bundle);
    }

    public LiveData<Boolean> updateOrderStatus(String orderId, String newStatus) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        repository.updateOrderStatus(orderId, newStatus).observeForever(success -> {
            if (success != null && success) {
                // Fetch the order and send notification
                repository.getOrderWithItems(orderId).observeForever(order -> {
                    if (order != null && order.getUserId() != null) {
                        String userId = order.getUserId();
                        String title = "Đơn hàng của bạn đã có trạng thái mới: " + newStatus;
                        NotificationDto notificationDto = new NotificationDto(
                            title,
                            NotificationTypes.ORDER_STATUS_UPDATED,
                            new Date()
                        );
                        notificationPublisher.tryPublishToOneUser(userId, notificationDto);
                    }
                });
            }
            result.setValue(success);
        });
        return result;
    }

}