package com.group4.herbs_and_friends_app.ui.customer_side.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.group4.herbs_and_friends_app.ui.customer_side.order.OrderHistoryClickHandler;
import com.group4.herbs_and_friends_app.ui.customer_side.profile.adapter.OrderHistoryAdapter;

import java.util.Date;
import java.util.HashMap;
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
    }

    private void loadOrderHistory() {
        if (currentUser != null) {
            hProfileVM.getUserOrders().observe(getViewLifecycleOwner(), orders -> {
                if (orders != null && orderHistoryAdapter != null) {
                    orderHistoryAdapter.setOrders(orders);
                }
            });
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
