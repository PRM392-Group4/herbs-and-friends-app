package com.group4.herbs_and_friends_app.ui.customer_side.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.PopupMenu;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.mail.PasswordUtils;
import com.group4.herbs_and_friends_app.databinding.FragmentHProfileBinding;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.ui.customer_side.order.OrderHistoryClickHandler;
import com.group4.herbs_and_friends_app.ui.customer_side.profile.adapter.OrderHistoryAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HProfileFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    @Inject
    FirebaseFirestore firestore;
    private FragmentHProfileBinding binding;
    private FirebaseUser currentUser;
    private HProfileVM hProfileVM;

    private OrderHistoryAdapter orderHistoryAdapter;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hProfileVM = new ViewModelProvider(this).get(HProfileVM.class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        TabLayout tabLayout = binding.tabLayout;
        View layoutProfile = view.findViewById(R.id.layoutProfile);
        View layoutHistory = view.findViewById(R.id.layoutHistory);

        tabLayout.addTab(tabLayout.newTab().setText("Thông tin cá nhân"));
        tabLayout.addTab(tabLayout.newTab().setText("Lịch sử mua hàng"));

        // Set up order history RecyclerView
        setupOrderHistoryRecyclerView(layoutHistory);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    layoutProfile.setVisibility(View.VISIBLE);
                    layoutHistory.setVisibility(View.GONE);
                } else {
                    layoutProfile.setVisibility(View.GONE);
                    layoutHistory.setVisibility(View.VISIBLE);
                    // Load order history when tab is selected
                    loadOrderHistory();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.layoutProfile.btnLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_profileFragment_to_loginFragment);
            Log.d("btnLogin", "Go to Login");
        });

        binding.layoutProfile.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_profileFragment_to_loginFragment);
        });

        binding.layoutProfile.imgEdit.setOnClickListener(v -> showEditDialog());

        // Load user info
        if (currentUser != null) {
            String uid = currentUser.getUid();
            hProfileVM.fetchUser(uid, user -> {
                binding.layoutProfile.tvName.setText(user.getName());
                binding.layoutProfile.tvEmail.setText(user.getEmail());
                binding.layoutProfile.tvAdName.setText(user.getName());
                binding.layoutProfile.tvAdPhone.setText(user.getPhone());
                binding.layoutProfile.tvAddress.setText(user.getAddress());

                binding.layoutProfile.btnLogin.setVisibility(View.GONE);
                binding.layoutProfile.btnLogout.setVisibility(View.VISIBLE);
            }, () -> {
                Log.e("PROFILE", "Không thể lấy thông tin user");
            });
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupOrderHistoryRecyclerView(View layoutHistory) {
        RecyclerView recyclerView = layoutHistory.findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderHistoryAdapter = new OrderHistoryAdapter(requireContext(), order -> {
            // Navigate to order detail when clicked
            OrderHistoryClickHandler.navigateToOrderDetailsFromProfile(this, order.getId());
        });

        recyclerView.setAdapter(orderHistoryAdapter);
        
        // Setup filter and sort buttons
        setupFilterAndSortButtons(layoutHistory);
    }
    
    private void setupFilterAndSortButtons(View layoutHistory) {
        // Refresh button
        View btnRefreshOrders = layoutHistory.findViewById(R.id.btnRefreshOrders);
        btnRefreshOrders.setOnClickListener(v -> refreshOrderHistory());
        
        // Status filter button
        View btnFilterStatus = layoutHistory.findViewById(R.id.btnFilterStatus);
        btnFilterStatus.setOnClickListener(v -> showStatusFilterMenu(btnFilterStatus));
        
        // Sort button
        View btnSortDate = layoutHistory.findViewById(R.id.btnSortDate);
        btnSortDate.setOnClickListener(v -> showSortMenu(btnSortDate));
        
        // Setup active filter chips
        setupActiveFilterChips(layoutHistory);
    }
    
    private void showStatusFilterMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
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

            hProfileVM.setStatusFilter(selectedStatus);
            updateActiveFilterChips();
            return true;
        });

        popup.show();
    }
    
    private void showSortMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.view_h_order_sort_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            HProfileVM.SortOrder sortOrder = null;

            if (itemId == R.id.sort_newest_first) {
                sortOrder = HProfileVM.SortOrder.NEWEST_FIRST;
            } else if (itemId == R.id.sort_oldest_first) {
                sortOrder = HProfileVM.SortOrder.OLDEST_FIRST;
            }

            hProfileVM.setSortOrder(sortOrder);
            updateActiveFilterChips();
            return true;
        });

        popup.show();
    }
    
    private void setupActiveFilterChips(View layoutHistory) {
        View layoutActiveFilters = layoutHistory.findViewById(R.id.layoutActiveFilters);
        View chipStatusFilter = layoutHistory.findViewById(R.id.chipStatusFilter);
        View chipSortOrder = layoutHistory.findViewById(R.id.chipSortOrder);
        
        // Set up chip close listeners
        chipStatusFilter.setOnClickListener(v -> {
            hProfileVM.setStatusFilter(null);
            updateActiveFilterChips();
        });
        
        chipSortOrder.setOnClickListener(v -> {
            hProfileVM.setSortOrder(null);
            updateActiveFilterChips();
        });
    }
    
    private void updateActiveFilterChips() {
        View layoutHistory = binding.layoutHistory.getRoot();
        View layoutActiveFilters = layoutHistory.findViewById(R.id.layoutActiveFilters);
        Chip chipStatusFilter = layoutHistory.findViewById(R.id.chipStatusFilter);
        Chip chipSortOrder = layoutHistory.findViewById(R.id.chipSortOrder);
        
        OrderStatus selectedStatus = hProfileVM.getSelectedStatus();
        HProfileVM.SortOrder sortOrder = hProfileVM.getSortOrder();
        
        boolean hasActiveFilters = selectedStatus != null || sortOrder != null;
        layoutActiveFilters.setVisibility(hasActiveFilters ? View.VISIBLE : View.GONE);
        
        if (selectedStatus != null) {
            chipStatusFilter.setVisibility(View.VISIBLE);
            chipStatusFilter.setText("Trạng thái: " + selectedStatus.getDisplayName());
        } else {
            chipStatusFilter.setVisibility(View.GONE);
        }
        
        if (sortOrder != null) {
            chipSortOrder.setVisibility(View.VISIBLE);
            chipSortOrder.setText("Sắp xếp: " + sortOrder.getDisplayName());
        } else {
            chipSortOrder.setVisibility(View.GONE);
        }
    }

    private void loadOrderHistory() {
        if (currentUser != null) {
            hProfileVM.getUserOrders().observe(getViewLifecycleOwner(), orders -> {
                if (orders != null && orderHistoryAdapter != null) {
                    orderHistoryAdapter.setOrders(orders);
                    updateOrderHistoryUI(orders);
                    updateActiveFilterChips();
                }
            });
        }
    }
    
    private void refreshOrderHistory() {
        if (currentUser != null) {
            Log.d("HProfileFragment", "Refreshing order history manually");
            
            // Disable refresh button temporarily and show loading state
            View layoutHistory = binding.layoutHistory.getRoot();
            View btnRefreshOrders = layoutHistory.findViewById(R.id.btnRefreshOrders);
            btnRefreshOrders.setEnabled(false);
            
            hProfileVM.refreshOrderHistory();
            
            // Show a brief toast to indicate refresh
            Toast.makeText(requireContext(), "Đang làm mới danh sách đơn hàng...", Toast.LENGTH_SHORT).show();
            
            // Re-enable button after a short delay
            btnRefreshOrders.postDelayed(() -> {
                btnRefreshOrders.setEnabled(true);
            }, 2000); // 2 seconds
        }
    }
    
    private void updateOrderHistoryUI(List<Order> orders) {
        View layoutHistory = binding.layoutHistory.getRoot();
        RecyclerView recyclerView = layoutHistory.findViewById(R.id.recyclerViewOrderHistory);
        View emptyStateLayout = layoutHistory.findViewById(R.id.emptyStateLayout);
        
        if (orders == null || orders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void showEditDialog() {
        if (currentUser == null) return;
        String uid = currentUser.getUid();

        hProfileVM.fetchUser(uid, user -> {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

            EditText edtName = dialogView.findViewById(R.id.edtName);
            EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
            EditText edtPassword = dialogView.findViewById(R.id.edtPassword);
            EditText edtAddress = dialogView.findViewById(R.id.edtAddress);

            edtName.setText(user.getName());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());

            new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newName = edtName.getText().toString().trim();
                        String newPhone = edtPhone.getText().toString().trim();
                        String newPassword = edtPassword.getText().toString().trim();
                        String newAddress = edtAddress.getText().toString().trim();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", newName);
                        updates.put("phone", newPhone);
                        updates.put("address", newAddress);
                        updates.put("updateAt", new Date());

                        if (!newPassword.isEmpty()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                firebaseUser.updatePassword(newPassword)
                                        .addOnSuccessListener(unused -> {
                                            updates.put("password", PasswordUtils.hashPassword(newPassword));
                                            applyProfileUpdates(uid, updates);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(requireContext(), "Lỗi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("EDIT", "Failed to update password", e);
                                        });
                            }
                        } else {
                            applyProfileUpdates(uid, updates);
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();

        }, () -> {
            Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
        });
    }

    private void applyProfileUpdates(String uid, Map<String, Object> updates) {
        firestore.collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UPDATE", "Cập nhật thành công: " + updates);
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    reloadUserInfo();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    Log.e("EDIT", "Update Firestore failed", e);
                });
    }

    private void reloadUserInfo() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            hProfileVM.fetchUser(uid, user -> {
                if (binding == null) return;

                binding.layoutProfile.tvName.setText(user.getName());
                binding.layoutProfile.tvEmail.setText(user.getEmail());
                binding.layoutProfile.tvAdName.setText(user.getName());
                binding.layoutProfile.tvAdPhone.setText(user.getPhone());
                binding.layoutProfile.tvAddress.setText(user.getAddress());
            }, () -> {
                Log.e("PROFILE", "Không thể reload thông tin user");
            });
        }
    }
}
