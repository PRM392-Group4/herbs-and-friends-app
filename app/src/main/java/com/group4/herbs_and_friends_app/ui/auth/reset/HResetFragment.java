package com.group4.herbs_and_friends_app.ui.auth.reset;

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
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.FragmentHResetBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HResetFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    private FragmentHResetBinding binding;
    private HResetVM hResetVM;

    @Inject
    FirebaseFirestore firestore;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHResetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hResetVM = new ViewModelProvider(this).get(HResetVM.class);

        EditText etEmail = binding.etEmail;

        Drawable clearIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear);
        if (clearIcon != null) {
            clearIcon.setBounds(0, 0, clearIcon.getIntrinsicWidth(), clearIcon.getIntrinsicHeight());
        }

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etEmail.setCompoundDrawables(null, null, clearIcon, null);
                } else {
                    etEmail.setCompoundDrawables(null, null, null, null);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
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


        binding.btnNReset.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            hResetVM.resetPassword(
                    email,
                    () -> {
                        Toast.makeText(requireContext(), "Hãy kiểm tra email của bạn", Toast.LENGTH_LONG).show();
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_HResetFragment_to_HLoginFragment);
                    },
                    () -> Toast.makeText(requireContext(), "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show(),
                    () -> Toast.makeText(requireContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
            );
        });

        binding.btnReturn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_HResetFragment_to_HLoginFragment);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Methods
    // ================================
    // Add UI interactions, listeners, or LiveData observers here
}
