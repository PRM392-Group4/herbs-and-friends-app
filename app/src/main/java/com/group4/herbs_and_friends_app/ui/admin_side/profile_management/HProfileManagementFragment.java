package com.group4.herbs_and_friends_app.ui.admin_side.profile_management;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.mail.PasswordUtils;
import com.group4.herbs_and_friends_app.databinding.FragmentHProfileManagementBinding;
import com.group4.herbs_and_friends_app.ui.auth.login.HAuthVM;
import com.group4.herbs_and_friends_app.utils.AppCts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HProfileManagementFragment extends Fragment {

    @Inject
    FirebaseFirestore firestore;
    // ================================
    // === Fields
    // ================================
    private FragmentHProfileManagementBinding binding;
    private HProfileManagementVM hProfileManagementVM;
    private FirebaseAuth firebaseAuth;
    private HAuthVM hAuthVM;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;

    // ================================
    // === Lifecycle
    // ================================
    public static HProfileManagementFragment newInstance() {
        return new HProfileManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHProfileManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hAuthVM = new ViewModelProvider(requireActivity()).get(HAuthVM.class);
        hProfileManagementVM = new ViewModelProvider(this).get(HProfileManagementVM.class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setup SharedPreferencs
        sharedPreferences = getActivity().getSharedPreferences(AppCts.SharePref.PREF_AUTH_NAME, MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            hProfileManagementVM.fetchUser(uid, user -> {
                binding.tvName.setText(user.getName());
                binding.tvEmail.setText(user.getEmail());
            }, () -> {
                Log.e("ADMIN", "Không tìm thấy user trong Firestore");
            });
        }

        binding.btnLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            sharedPreferences.edit().putBoolean(AppCts.SharePref.KEY_FIRST_LOGIN, false);
            hAuthVM.fetchUserAndEmitNextDestination(null);
        });

        binding.imgEdit.setOnClickListener(v -> showEditDialog());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showEditDialog() {
        if (currentUser == null) return;
        String uid = currentUser.getUid();

        hProfileManagementVM.fetchUser(uid, user -> {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_edit_profile_admin, null);

            EditText edtName = dialogView.findViewById(R.id.edtName);
            EditText edtPassword = dialogView.findViewById(R.id.edtPassword);

            edtName.setText(user.getName());

            new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newName = edtName.getText().toString().trim();
                        String newPassword = edtPassword.getText().toString().trim();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", newName);
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
            hProfileManagementVM.fetchUser(uid, user -> {
                if (binding == null) return;

                binding.tvName.setText(user.getName());
                binding.tvEmail.setText(user.getEmail());
            }, () -> {
                Log.e("PROFILE", "Không thể reload thông tin user");
            });
        }
    }
}
