package com.group4.herbs_and_friends_app.ui.customer_side.manage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        setupManageSpinner();
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

    private void setupManageSpinner() {
        Spinner spinner = binding.spinnerManageType;
        
        // Create adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.manage_type_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        // Set default selection to "Quản lý đơn hàng" (index 1)
        spinner.setSelection(1);
        
        // Handle spinner selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Navigate to product management
                        navigateToProductManagement();
                        break;
                    case 1:
                        // Already on order management, do nothing
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void navigateToProductManagement() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_orderManage_to_productManage);
    }

    private void setupRecyclerView() {
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderManageAdapter(requireContext(), this);
        binding.rvOrders.setAdapter(orderAdapter);
    }

    private void setupSearchAndFilter() {
        // Setup search functionality
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

        // Setup filter button
        binding.btnFilterStatus.setOnClickListener(v -> showStatusFilterMenu());
    }

    private void showStatusFilterMenu() {
        PopupMenu popup = new PopupMenu(requireContext(), binding.btnFilterStatus);
        
        // Add all status options
        popup.getMenu().add(0, 0, 0, "Tất cả");
        popup.getMenu().add(0, 1, 1, "Chờ xác nhận");
        popup.getMenu().add(0, 2, 2, "Đã xác nhận");
        popup.getMenu().add(0, 3, 3, "Đang giao");
        popup.getMenu().add(0, 4, 4, "Hoàn thành");
        popup.getMenu().add(0, 5, 5, "Đã hủy");

        popup.setOnMenuItemClickListener(item -> {
            OrderStatus selectedStatus = null;
            String statusText = "Trạng thái";

            switch (item.getItemId()) {
                case 0: // All
                    selectedStatus = null;
                    statusText = "Tất cả";
                    break;
                case 1: // Pending
                    selectedStatus = OrderStatus.PENDING;
                    statusText = "Chờ xác nhận";
                    break;
                case 2: // Confirmed
                    selectedStatus = OrderStatus.CONFIRMED;
                    statusText = "Đã xác nhận";
                    break;
                case 3: // Shipping
                    selectedStatus = OrderStatus.SHIPPING;
                    statusText = "Đang giao";
                    break;
                case 4: // Completed
                    selectedStatus = OrderStatus.COMPLETED;
                    statusText = "Hoàn thành";
                    break;
                case 5: // Cancelled
                    selectedStatus = OrderStatus.CANCELLED;
                    statusText = "Đã hủy";
                    break;
            }

            binding.btnFilterStatus.setText(statusText);
            orderManageVM.setStatusFilter(selectedStatus);
            return true;
        });

        popup.show();
    }

    private void observeData() {
        orderManageVM.getFilteredOrdersLive().observe(getViewLifecycleOwner(), orders -> {
            updateUI(orders);
        });
    }

    private void updateUI(List<Order> orders) {
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
        // Navigate to existing OrderDetail fragment
        NavController navController = NavHostFragment.findNavController(this);
        HOrderManageFragmentDirections.ActionOrderManageToOrderDetail action = 
                HOrderManageFragmentDirections.actionOrderManageToOrderDetail(order.getId());
        navController.navigate(action);
    }
} 