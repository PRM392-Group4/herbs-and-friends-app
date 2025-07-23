package com.group4.herbs_and_friends_app.ui.customer_side.order;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HOrderDetailVM extends ViewModel {

    private OrderRepository orderRepository;
    private CouponRepository couponRepository;
    private MutableLiveData<String> orderIdLive = new MutableLiveData<>();
    private LiveData<Order> orderLive;
    private final MutableLiveData<String> couponCode = new MutableLiveData<>();
    private NotificationPublisher notificationPublisher;

    @Inject
    public HOrderDetailVM(OrderRepository orderRepository, CouponRepository couponRepository, NotificationPublisher notificationPublisher) {
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.notificationPublisher = notificationPublisher;
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
    public void loadCouponCode(String couponId){
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
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        LiveData<Boolean> updateStatusLiveData = orderRepository.updateOrderStatus(orderId, newStatus);

        Observer<Boolean> updateStatusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                updateStatusLiveData.removeObserver(this);
                Log.d("OrderDetailVM", "Order status updated: " + newStatus + " " + success);
                if (success != null && success) {
                    LiveData<Order> orderLiveData = orderRepository.getOrderWithItems(orderId);
                    Observer<Order> orderObserver = new Observer<Order>() {
                        @Override
                        public void onChanged(Order order) {
                            orderLiveData.removeObserver(this);

                            Log.d("OrderDetailVM", "Order user id: " + order.getUserId());
                            if (order != null && order.getUserId() != null) {
                                String userId = order.getUserId();
                                String title = "Đơn hàng " + orderId + " của bạn đã có trạng thái mới: " + newStatus;
                                NotificationDto notificationDto = new NotificationDto(
                                        title,
                                        NotificationTypes.ORDER_STATUS_UPDATED,
                                        new Date()
                                );
                                notificationPublisher.tryPublishToOneUser(userId, notificationDto);
                            }
                        }
                    };
                    orderLiveData.observeForever(orderObserver);
                }
                result.setValue(success);
            }
        };

        updateStatusLiveData.observeForever(updateStatusObserver);
        return result;
    }

    public LiveData<Boolean> cancelOrder(String orderId) {
        return updateOrderStatus(orderId, "Cancelled");
    }

    public LiveData<String> getCouponCode() {
        return couponCode;
    }
} 