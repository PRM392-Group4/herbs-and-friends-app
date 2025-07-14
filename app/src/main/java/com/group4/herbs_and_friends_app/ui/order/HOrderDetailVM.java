package com.group4.herbs_and_friends_app.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HOrderDetailVM extends ViewModel {

    private OrderRepository orderRepository;
    private MutableLiveData<String> orderIdLive = new MutableLiveData<>();
    private LiveData<Order> orderLive;

    @Inject
    public HOrderDetailVM(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        
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

    // =================================
    // === Public Methods
    // =================================

    /**
     * Set the order ID to load order details
     */
    public void setOrderId(String orderId) {
        orderIdLive.setValue(orderId);
    }

    /**
     * Get live order data with items
     */
    public LiveData<Order> getOrderLive() {
        return orderLive;
    }

    /**
     * Update order status
     */
    public LiveData<Boolean> updateOrderStatus(String orderId, String newStatus) {
        return orderRepository.updateOrderStatus(orderId, newStatus);
    }

    /**
     * Cancel order (set status to cancelled)
     */
    public LiveData<Boolean> cancelOrder(String orderId) {
        return orderRepository.updateOrderStatus(orderId, "Cancelled");
    }
} 