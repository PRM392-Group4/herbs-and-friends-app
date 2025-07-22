package com.group4.herbs_and_friends_app.ui.customer_side.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HProfileVM extends ViewModel {

    private final OrderRepository orderRepository;
    private final AuthRepository authRepository;
    
    // Original data from repository
    private LiveData<List<Order>> allOrdersLive;
    
    // Filter and sort parameters
    private MutableLiveData<OrderStatus> selectedStatusLive = new MutableLiveData<>();
    private MutableLiveData<SortOrder> sortOrderLive = new MutableLiveData<>();
    
    // Filtered and sorted results
    private MediatorLiveData<List<Order>> filteredOrdersLive = new MediatorLiveData<>();

    public enum SortOrder {
        NEWEST_FIRST("Mới nhất trước"),
        OLDEST_FIRST("Cũ nhất trước");
        
        private final String displayName;
        
        SortOrder(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    @Inject
    public HProfileVM(OrderRepository orderRepository, AuthRepository authRepository) {
        this.orderRepository = orderRepository;
        this.authRepository = authRepository;
        
        this.allOrdersLive = orderRepository.getUserOrders();
        
        // Setup filtering and sorting logic
        setupFilteringAndSorting();
        
        // Set default sort order to newest first
        sortOrderLive.setValue(SortOrder.NEWEST_FIRST);
    }

    private void setupFilteringAndSorting() {
        // Add sources to the mediator
        filteredOrdersLive.addSource(allOrdersLive, orders -> applyFiltersAndSort());
        filteredOrdersLive.addSource(selectedStatusLive, status -> applyFiltersAndSort());
        filteredOrdersLive.addSource(sortOrderLive, sortOrder -> applyFiltersAndSort());
    }

    private void applyFiltersAndSort() {
        List<Order> allOrders = allOrdersLive.getValue();
        if (allOrders == null) {
            filteredOrdersLive.setValue(new ArrayList<>());
            return;
        }

        List<Order> filtered = new ArrayList<>(allOrders);
        
        // Apply status filter
        OrderStatus selectedStatus = selectedStatusLive.getValue();
        if (selectedStatus != null) {
            filtered = filtered.stream()
                    .filter(order -> order.getStatusEnum() == selectedStatus)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        // Apply sorting
        SortOrder sortOrder = sortOrderLive.getValue();
        if (sortOrder != null) {
            switch (sortOrder) {
                case NEWEST_FIRST:
                    Collections.sort(filtered, (o1, o2) -> {
                        if (o1.getPlacedAt() == null && o2.getPlacedAt() == null) return 0;
                        if (o1.getPlacedAt() == null) return 1;
                        if (o2.getPlacedAt() == null) return -1;
                        return o2.getPlacedAt().compareTo(o1.getPlacedAt());
                    });
                    break;
                case OLDEST_FIRST:
                    Collections.sort(filtered, (o1, o2) -> {
                        if (o1.getPlacedAt() == null && o2.getPlacedAt() == null) return 0;
                        if (o1.getPlacedAt() == null) return 1;
                        if (o2.getPlacedAt() == null) return -1;
                        return o1.getPlacedAt().compareTo(o2.getPlacedAt());
                    });
                    break;
            }
        }
        
        Log.d("HProfileVM", "Filtered and sorted " + allOrders.size() + " orders to " + filtered.size());
        filteredOrdersLive.setValue(filtered);
    }

    public LiveData<List<Order>> getUserOrders() {
        return filteredOrdersLive;
    }

    public void setStatusFilter(OrderStatus status) {
        selectedStatusLive.setValue(status);
    }

    public void setSortOrder(SortOrder sortOrder) {
        sortOrderLive.setValue(sortOrder);
    }

    public void clearFilters() {
        selectedStatusLive.setValue(null);
        sortOrderLive.setValue(null);
    }

    public OrderStatus getSelectedStatus() {
        return selectedStatusLive.getValue();
    }

    public SortOrder getSortOrder() {
        return sortOrderLive.getValue();
    }

    public void fetchUser(String uid, Consumer<User> onUserLoaded, Runnable onFailure) {
        authRepository.getUserByUid(uid, onUserLoaded, onFailure);
    }

    public void refreshOrderHistory() {
        // Force refresh by getting new data from repository
        Log.d("HProfileVM", "Manually refreshing order history");
        this.allOrdersLive = orderRepository.getUserOrders();
        
        // Re-setup the mediator with the fresh data source
        filteredOrdersLive.removeSource(allOrdersLive);
        filteredOrdersLive.addSource(allOrdersLive, orders -> applyFiltersAndSort());
        
        // Trigger immediate update
        applyFiltersAndSort();
    }
}
