package com.group4.herbs_and_friends_app.ui.auth.register;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.FragmentHRegisterBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HRegisterFragment extends Fragment {

    @Inject
    FirebaseFirestore firestore;
    private FragmentHRegisterBinding binding;
    private HRegisterVM hRegisterVM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hRegisterVM = new ViewModelProvider(this).get(HRegisterVM.class);

        NavController navController = NavHostFragment.findNavController(this);

        EditText etEmail = binding.etEmail;

        Drawable clearIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear);
        if (clearIcon != null) {
            clearIcon.setBounds(0, 0, clearIcon.getIntrinsicWidth(), clearIcon.getIntrinsicHeight());
        }

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etEmail.setCompoundDrawables(null, null, clearIcon, null);
                } else {
                    etEmail.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etEmail.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etEmail.getCompoundDrawables()[2] != null) {
                    int iconStart = etEmail.getWidth() - etEmail.getPaddingEnd() - clearIcon.getIntrinsicWidth();
                    if (event.getX() >= iconStart) {
                        etEmail.setText("");
                        return true;
                    }
                }
            }
            return false;
        });

        binding.btnSignUp.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

            hRegisterVM.registerUser(email, password, confirmPassword,
                    () -> {
                        Toast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_registerFragment_to_profileFragment);
                    },
                    () -> Toast.makeText(requireContext(), "Lỗi khi đăng ký. Vui lòng thử lại!", Toast.LENGTH_SHORT).show(),
                    () -> Toast.makeText(requireContext(), "Email đã tồn tại. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show(),
                    () -> Toast.makeText(requireContext(), "Mật khẩu không hợp lệ hoặc không khớp!", Toast.LENGTH_SHORT).show()
            );
        });

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        });

        binding.tvLogin.setOnClickListener(v -> {
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }


    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
