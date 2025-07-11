package com.group4.herbs_and_friends_app.ui.auth.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.enums.LoginMethod;
import com.group4.herbs_and_friends_app.databinding.FragmentHLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HLoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FragmentHLoginBinding binding;
    private HLoginViewModel hLoginViewModel;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        binding.cbShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        FirebaseAuth.getInstance().signOut();

        hLoginViewModel = new ViewModelProvider(this).get(HLoginViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            goToHome();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id)) // Client ID từ Firebase
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(task ->
                Log.d("LOGIN_FLOW", "Google Sign-In state reset done")
        );

        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        NavController navController = NavHostFragment.findNavController(this);


        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_HLoginFragment);
        });

        Log.d("CHECK_FIREBASE", "Client ID: " + getString(R.string.default_client_id));

        TextView tvRegister = view.findViewById(R.id.tvRegister);
        TextView tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        tvRegister.setOnClickListener(v -> {
            navController.navigate(R.id.action_HLoginFragment_to_HRegisterFragment);
        });

        tvForgotPassword.setOnClickListener(v -> {
            navController.navigate(R.id.action_HLoginFragment_to_HResetFragment);
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            loginAsUser(LoginMethod.EMAIL_PASSWORD, email, password);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d("LOGIN_FLOW", "Launching Google Sign-In intent");
        googleSignInLauncher.launch(signInIntent);
    }

    private void goToHome() {
        NavHostFragment.findNavController(this).popBackStack();
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            String idToken = account.getIdToken();

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                AuthCredential googleCredential = GoogleAuthProvider.getCredential(idToken, null);
                                currentUser.linkWithCredential(googleCredential)
                                        .addOnSuccessListener(authResult -> {
                                            Toast.makeText(requireContext(), "Liên kết Google thành công", Toast.LENGTH_SHORT).show();
                                            goToHome();
                                        })
                                        .addOnFailureListener(e -> {
                                            if (e instanceof FirebaseAuthUserCollisionException) {
                                                Toast.makeText(requireContext(), "Google đã liên kết với tài khoản khác", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.e("LINK", "Lỗi khi liên kết Google", e);
                                            }
                                        });
                            } else {
                                // 👉 Chưa login, thực hiện đăng nhập bằng Google như cũ
                                loginAsUser(LoginMethod.GOOGLE, idToken, null);
                            }
                        }
                    } catch (ApiException ignored) {
                        Toast.makeText(requireContext(), "Google sign-in thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Google sign-in bị hủy", Toast.LENGTH_SHORT).show();
                }
            });

    private void loginAsUser(LoginMethod method, String key, @Nullable String optionalPassword) {
        hLoginViewModel.login(method, key, optionalPassword,
                () -> {
                    Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    goToHome();
                },
                () -> Toast.makeText(requireContext(), "Sai thông tin hoặc tài khoản không tồn tại", Toast.LENGTH_SHORT).show(),
                () -> Toast.makeText(requireContext(), "Lỗi hệ thống, thử lại sau!", Toast.LENGTH_SHORT).show()
        );
    }

}
