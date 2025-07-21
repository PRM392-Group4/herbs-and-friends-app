package com.group4.herbs_and_friends_app.ui.customer_side.manage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.databinding.FragmentHOrderManageBinding;
import com.group4.herbs_and_friends_app.databinding.ViewHActionbarWithoutSearchBinding;
import com.group4.herbs_and_friends_app.ui.customer_side.manage.adapter.OrderManageAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HOrderManageFragment extends Fragment implements OrderManageAdapter.OnOrderClickListener {

    private FragmentHOrderManageBinding binding;
    private HOrderManageVM orderManageVM;
    private OrderManageAdapter orderAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHOrderManageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderManageVM = new ViewModelProvider(this).get(HOrderManageVM.class);

        setupActionBar();
        setupRecyclerView();
        setupSearchAndFilter();
        observeData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupActionBar() {
        ViewHActionbarWithoutSearchBinding actionbarBinding = binding.includeActionbarOrderManage;
        actionbarBinding.btnBack.setVisibility(View.GONE);
        actionbarBinding.actionBarTitle.setText("Quản lý đơn hàng");
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderManageAdapter();
        orderAdapter.setOnOrderClickListener(this);
        
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrders.setAdapter(orderAdapter);
    }

    private void setupSearchAndFilter() {
        // Search functionality
        binding.etSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                orderManageVM.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter functionality
        binding.btnFilterStatus.setOnClickListener(v -> showStatusFilterMenu());
    }

    private void showStatusFilterMenu() {
        PopupMenu popup = new PopupMenu(requireContext(), binding.btnFilterStatus);
        popup.getMenuInflater().inflate(R.menu.view_h_order_status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            OrderStatus selectedStatus = null;

            if (itemId == R.id.status_all) {
                selectedStatus = null;
            } else if (itemId == R.id.status_pending) {
                selectedStatus = OrderStatus.PENDING;
            } else if (itemId == R.id.status_confirmed) {
                selectedStatus = OrderStatus.CONFIRMED;
            } else if (itemId == R.id.status_shipping) {
                selectedStatus = OrderStatus.SHIPPING;
            } else if (itemId == R.id.status_completed) {
                selectedStatus = OrderStatus.COMPLETED;
            } else if (itemId == R.id.status_cancelled) {
                selectedStatus = OrderStatus.CANCELLED;
            } else if (itemId == R.id.status_unpaid) {
                selectedStatus = OrderStatus.UNPAID;
            }

            orderManageVM.setStatusFilter(selectedStatus);
            return true;
        });

        popup.show();
    }

    private void observeData() {
        orderManageVM.getFilteredOrdersLive().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<Order> orders) {
        Log.d("Num of orders: ", String.valueOf(orders.size()));
        if (orders == null || orders.isEmpty()) {
            binding.rvOrders.setVisibility(View.GONE);
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            binding.rvOrders.setVisibility(View.VISIBLE);
            binding.emptyStateLayout.setVisibility(View.GONE);
            orderAdapter.setOrders(orders);
        }
    }

    @Override
    public void onOrderClick(Order order) {
        NavController navController = NavHostFragment.findNavController(this);
        HOrderManageFragmentDirections.ActionOrderManageToOrderDetail action =
                HOrderManageFragmentDirections.actionOrderManageToOrderDetail(order.getId());
        navController.navigate(action);
    }
} 