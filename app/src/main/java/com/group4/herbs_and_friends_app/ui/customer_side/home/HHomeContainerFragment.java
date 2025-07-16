package com.group4.herbs_and_friends_app.ui.customer_side.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;

import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HHomeContainerFragment extends Fragment {
    @Inject
    AuthRepository authRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_home_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchUser(currentUser.getUid(),
                    user -> {
                        Log.d("FIREBASE_USER", "Tải user thành công: " + user.getEmail() + ", role = " + user.getRole());
                        goToHome(user);
                    },
                    () -> Log.e("FIREBASE_USER", "Không tìm thấy user trong Firestore")
            );
        }
    }

    public void fetchUser(String uid, Consumer<User> onUserLoaded, Runnable onFailure) {
        authRepository.getUserByUid(uid, onUserLoaded, onFailure);
    }

    private void goToHome(User user) {
        NavController navController = NavHostFragment.findNavController(this);

        if ("admin".equalsIgnoreCase(user.getRole())) {
            Log.d("ROLE_CHECK", "Chuyển đến admin fragment");

            // Ẩn bottom navigation
            requireActivity().findViewById(R.id.herb_bottom_navigation).setVisibility(View.GONE);

            navController.navigate(R.id.action_homeFragment_to_HAdminFragment);

        } else {
            Log.d("ROLE_CHECK", "Giữ nguyên profile fragment");
            requireActivity().findViewById(R.id.herb_bottom_navigation).setVisibility(View.VISIBLE);
        }
    }

}