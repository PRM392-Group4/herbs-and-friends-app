package com.group4.herbs_and_friends_app.ui.customer_side.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HOrderDetailVM extends ViewModel {

    private OrderRepository orderRepository;
    private CouponRepository couponRepository;
    private MutableLiveData<String> orderIdLive = new MutableLiveData<>();
    private LiveData<Order> orderLive;
    private final MutableLiveData<String> couponCode = new MutableLiveData<>();

    @Inject
    public HOrderDetailVM(OrderRepository orderRepository, CouponRepository couponRepository) {
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        
        // Set up reactive data flow
        this.orderLive = androidx.lifecycle.Transformations.switchMap(orderIdLive, orderId -> {
            if (orderId == null || orderId.isEmpty()) {
                MutableLiveData<Order> emptyResult = new MutableLiveData<>();
                emptyResult.setValue(null);
                return emptyResult;
            }
            return orderRepository.getOrderWithItems(orderId);
        });
    }
    public void getCouponCode(String couponId){
        couponRepository.getCouponById(couponId).observeForever(new Observer<Coupon>() {
            @Override
            public void onChanged(Coupon coupon) {
                if (coupon != null) {
                    couponCode.postValue(coupon.getCode());
                } else {
                    couponCode.postValue("");
                }
                couponRepository.getCouponById(couponId).removeObserver(this);
            }
        });
    }

    public void setOrderId(String orderId) {
        orderIdLive.setValue(orderId);
    }
    public LiveData<Order> getOrderLive() {
        return orderLive;
    }

    public LiveData<Boolean> updateOrderStatus(String orderId, String newStatus) {
        return orderRepository.updateOrderStatus(orderId, newStatus);
    }

    public LiveData<Boolean> cancelOrder(String orderId) {
        return orderRepository.updateOrderStatus(orderId, "Cancelled");
    }

    public LiveData<String> getCouponCode() {
        return couponCode;
    }
} 