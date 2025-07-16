package com.group4.herbs_and_friends_app.ui.customer_side.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HProfileVM extends ViewModel {

    private final OrderRepository orderRepository;

    @Inject
    public HProfileVM(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public LiveData<List<Order>> getUserOrders() {
        return orderRepository.getUserOrders();
    }
}