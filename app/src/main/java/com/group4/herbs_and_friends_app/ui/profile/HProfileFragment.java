package com.group4.herbs_and_friends_app.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.mail.PasswordUtils;
import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.databinding.FragmentHProfileBinding;

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

    private FragmentHProfileBinding binding;

    @Inject
    public FirebaseFirestore firebaseFirestore;
    private HProfileVM hProfileVM;

    @Inject
    FirebaseFirestore firestore;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

        TabLayout tabLayout = binding.tabLayout;
        View layoutProfile = view.findViewById(R.id.layoutProfile);
        View layoutHistory = view.findViewById(R.id.layoutHistory);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvAdName = view.findViewById(R.id.tvAdName);
        TextView tvAdPhone = view.findViewById(R.id.tvAdPhone);
        TextView tvAddress = view.findViewById(R.id.tvAddress);

        tabLayout.addTab(tabLayout.newTab().setText("Thông tin cá nhân"));
        tabLayout.addTab(tabLayout.newTab().setText("Lịch sử mua hàng"));

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
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnLogin.setOnClickListener(v ->{
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_profileFragment_to_HLoginFragment);
            Log.d("btnLogin", "Go to Login");
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_profileFragment_to_HLoginFragment);
        });

        if (currentUser != null) {
            String uid = currentUser.getUid();

            firebaseFirestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                tvName.setText(user.getName());
                                tvEmail.setText(user.getEmail());
                                tvAdName.setText(user.getName());
                                tvAdPhone.setText(user.getPhone());
                                tvAddress.setText(user.getAddress());

                                btnLogin.setVisibility(View.GONE);
                                btnLogout.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("PROFILE", "Không thể lấy thông tin user", e));
        }

        ImageView imgEdit = view.findViewById(R.id.imgEdit);

        imgEdit.setOnClickListener(v -> showEditDialog());

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showEditDialog() {
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) return;

                    User user = documentSnapshot.toObject(User.class);
                    if (user == null) return;

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
                                                    Log.d("EDIT", "Password updated in FirebaseAuth");

                                                    // vẫn lưu hash mật khẩu trong Firestore
                                                    updates.put("password", PasswordUtils.hashPassword(newPassword));

                                                    applyProfileUpdates(uid, updates);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(requireContext(), "Lỗi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("EDIT", "Failed to update password", e);
                                                });
                                    } else {
                                        Toast.makeText(requireContext(), "Không thể cập nhật mật khẩu: chưa đăng nhập", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    applyProfileUpdates(uid, updates);
                                }

                            })
                            .setNegativeButton("Hủy", null)
                            .show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    Log.e("EDIT", "Failed to get user data", e);
                });
    }


    private void reloadUserInfo() {
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && getView() != null) {
                            TextView tvName = getView().findViewById(R.id.tvName);
                            TextView tvEmail = getView().findViewById(R.id.tvEmail);
                            TextView tvAdName = getView().findViewById(R.id.tvAdName);
                            TextView tvAdPhone = getView().findViewById(R.id.tvAdPhone);
                            TextView tvAddress = getView().findViewById(R.id.tvAddress);

                            tvName.setText(user.getName());
                            tvEmail.setText(user.getEmail());
                            tvAdName.setText(user.getName());
                            tvAdPhone.setText(user.getPhone());
                            tvAddress.setText(user.getAddress());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("RELOAD", "Không thể reload thông tin", e));
    }

    private void applyProfileUpdates(String uid, Map<String, Object> updates) {
        firestore
                .collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    reloadUserInfo();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    Log.e("EDIT", "Update Firestore failed", e);
                });
    }


}
