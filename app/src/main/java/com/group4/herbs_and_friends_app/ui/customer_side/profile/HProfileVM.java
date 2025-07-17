package com.group4.herbs_and_friends_app.ui.customer_side.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HProfileVM extends ViewModel {

    private final OrderRepository orderRepository;
    private final AuthRepository authRepository;

    @Inject
    public HProfileVM(OrderRepository orderRepository, AuthRepository authRepository) {
        this.orderRepository = orderRepository;
        this.authRepository = authRepository;
    }

    public LiveData<List<Order>> getUserOrders() {
        return orderRepository.getUserOrders();
    }

    public void fetchUser(String uid, Consumer<User> onUserLoaded, Runnable onFailure) {
        authRepository.getUserByUid(uid, onUserLoaded, onFailure);
    }
}
