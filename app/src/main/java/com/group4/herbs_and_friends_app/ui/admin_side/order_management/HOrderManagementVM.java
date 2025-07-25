package com.group4.herbs_and_friends_app.ui.admin_side.order_management;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HOrderManagementVM extends ViewModel {

    private OrderRepository orderRepository;
    private NotificationPublisher notificationPublisher;

    // Original data from repository
    private LiveData<List<Order>> allOrdersLive;

    // Filter and search parameters
    private MutableLiveData<String> searchQueryLive = new MutableLiveData<>();
    private MutableLiveData<OrderStatus> selectedStatusLive = new MutableLiveData<>();

    // Filtered results
    private MediatorLiveData<List<Order>> filteredOrdersLive = new MediatorLiveData<>();

    @Inject
    public HOrderManagementVM(OrderRepository orderRepository, NotificationPublisher notificationPublisher) {
        this.orderRepository = orderRepository;
        this.notificationPublisher = notificationPublisher;

        this.allOrdersLive = orderRepository.getAllOrders();

        // Setup filtering logic
        setupFiltering();
    }

    private void setupFiltering() {
        // Add sources to the mediator
        filteredOrdersLive.addSource(allOrdersLive, orders -> applyFilters());
        filteredOrdersLive.addSource(searchQueryLive, query -> applyFilters());
        filteredOrdersLive.addSource(selectedStatusLive, status -> applyFilters());
    }

    private void applyFilters() {
        List<Order> allOrders = allOrdersLive.getValue();
        if (allOrders == null) {
            filteredOrdersLive.setValue(new ArrayList<>());
            return;
        }

        List<Order> filtered = new ArrayList<>(allOrders);

        // Apply search filter
        String searchQuery = searchQueryLive.getValue();
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.trim().toLowerCase();
            filtered = filtered.stream()
                    .filter(order ->
                            order.getOrderNumber().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // Apply status filter
        OrderStatus selectedStatus = selectedStatusLive.getValue();
        if (selectedStatus != null) {
            filtered = filtered.stream()
                    .filter(order -> order.getStatusEnum() == selectedStatus)
                    .collect(Collectors.toList());
        }

        Log.d("HOrderManagementVM", "Filtered " + allOrders.size() + " orders down to " + filtered.size());
        filteredOrdersLive.setValue(filtered);
    }

    // Public methods for the fragment
    public LiveData<List<Order>> getFilteredOrdersLive() {
        return filteredOrdersLive;
    }

    public void setSearchQuery(String query) {
        searchQueryLive.setValue(query);
    }

    public void setStatusFilter(OrderStatus status) {
        selectedStatusLive.setValue(status);
    }

    public void clearFilters() {
        searchQueryLive.setValue("");
        selectedStatusLive.setValue(null);
    }

    public OrderStatus getSelectedStatus() {
        return selectedStatusLive.getValue();
    }

    public String getCurrentSearchQuery() {
        return searchQueryLive.getValue();
    }

    public LiveData<Boolean> updateOrderStatus(String orderId, OrderStatus newStatus) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        orderRepository.updateOrderStatus(orderId, newStatus.getValue()).observeForever(success -> {
            if (success != null && success) {
                orderRepository.getOrderWithItems(orderId).observeForever(order -> {
                    if (order != null && order.getUserId() != null) {
                        String userId = order.getUserId();
                        String title = "Đơn hàng của bạn đã có trạng thái mới: " + newStatus.getValue();
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