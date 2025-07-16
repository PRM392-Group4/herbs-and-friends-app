package com.group4.herbs_and_friends_app.ui.admin;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.databinding.FragmentHAdminBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HAdminFragment extends Fragment {

    private FragmentHAdminBinding binding;
    private HAdminViewModel mViewModel;

    @Inject
    AuthRepository authRepository;

    @Inject
    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HAdminViewModel.class);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

            // Hiện lại bottom navigation
            requireActivity().findViewById(R.id.herb_bottom_navigation).setVisibility(View.VISIBLE);

            // Điều hướng về login
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_HAdminFragment_to_HLoginFragment);
        });
    }
}
