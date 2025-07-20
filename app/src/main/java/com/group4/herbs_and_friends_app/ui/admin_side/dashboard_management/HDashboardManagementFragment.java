package com.group4.herbs_and_friends_app.ui.admin_side.dashboard_management;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.databinding.FragmentHDashboardManagementBinding;
import com.group4.herbs_and_friends_app.ui.auth.login.HAuthVM;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * THE CODE BELOW IS CUMBERSOME, I COPY FROM admin fragment cuz i'm lazy
 * - please fix the logic and make the code cleaner and more readable
 */
@AndroidEntryPoint
public class HDashboardManagementFragment extends Fragment {

    // ==========================
    // == Fields
    // ==========================

    private HDashboardManagementVM hDashboardManagementVM;
    private FragmentHDashboardManagementBinding binding;

    private FirebaseAuth firebaseAuth;

    // ==========================
    // == Lifecycle
    // ==========================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHDashboardManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hDashboardManagementVM = new ViewModelProvider(this).get(HDashboardManagementVM.class);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            hDashboardManagementVM.fetchUser(uid, user -> {
                binding.tvName.setText(user.getName());
            }, () -> {
                Log.e("ADMIN", "Không tìm thấy user trong Firestore");
            });
        }
    }
}