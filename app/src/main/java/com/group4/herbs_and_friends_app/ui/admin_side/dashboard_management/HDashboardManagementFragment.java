package com.group4.herbs_and_friends_app.ui.admin_side.dashboard_management;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * THE CODE BELOW IS WRONG, I COPY FROM admin fragment cuz i'm lazy
 * - please fix the logic
 * <p>
 * PLEASE REFACTOR THE CODE, DO NOT INJECT REPOSITORY INTO THE FRAGMENT
 * - FRAGMENT includes only VIEW MODEL for DATA BINDING (View layer in Clean Architecture)
 * - VIEW MODEL includes only REPOSITORY for interacting with data (Service layer in Clean Architecture)
 * - REPOSITORY includes only FIREBASE and other 3rd stuffs for handling logic (Repository layer in Clean Architecture)
 * <p>
 * HATE me if you want, idc, but
 * - Build a better person for your future career
 * - or you are fired
 * <p>
 * Study Study and Study more (Mr. Uncle Ho)
 */
@AndroidEntryPoint
public class HDashboardManagementFragment extends Fragment {

    // ==========================
    // == Fields
    // ==========================

    private HDashboardManagementVM hDashboardManagementVM;
    private FragmentHDashboardManagementBinding binding;
    private HAuthVM hAuthVM;

    @Inject
    public AuthRepository authRepository;

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

        hAuthVM = new ViewModelProvider(requireActivity()).get(HAuthVM.class);
        hDashboardManagementVM = new ViewModelProvider(this).get(HDashboardManagementVM.class);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            authRepository.getUserByUid(uid, user -> {
                binding.tvName.setText(user.getName());
            }, () -> {
                Log.e("ADMIN", "Không tìm thấy user trong Firestore");
            });
        }

        binding.ivLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            hAuthVM.fetchUserAndEmitNextDestination(null);
        });
    }
}