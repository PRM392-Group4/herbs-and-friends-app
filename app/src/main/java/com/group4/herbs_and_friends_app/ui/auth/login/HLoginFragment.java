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
import com.group4.herbs_and_friends_app.MainActivity;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.enums.LoginMethod;
import com.group4.herbs_and_friends_app.data.model.enums.Role;
import com.group4.herbs_and_friends_app.databinding.FragmentHLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HLoginFragment extends Fragment {

    // ================================
    // === Methods
    // ================================

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FragmentHLoginBinding binding;
    private HAuthVM hAuthVM;
    /**
     * Configure Google Sign-In launcher
     */
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                // Early exit if result code is canceled
                if (result.getResultCode() == Activity.RESULT_CANCELED || result.getData() == null) {
                    Toast.makeText(requireContext(), "Google sign-in bị hủy", Toast.LENGTH_SHORT).show();
                }

                // Execute code when result code is ok
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        // If no account then return nothing
                        if (account == null) return;

                        String idToken = account.getIdToken();
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        // Early exit if dont have current user
                        if (currentUser == null) {
                            loginAsUser(LoginMethod.GOOGLE, idToken, null);
                            return;
                        }

                        // Execute google credential
                        AuthCredential googleCredential = GoogleAuthProvider.getCredential(idToken, null);
                        currentUser.linkWithCredential(googleCredential)
                                .addOnSuccessListener(authResult -> {
                                    Toast.makeText(requireContext(), "Liên kết Google thành công", Toast.LENGTH_SHORT).show();

                                    FirebaseUser currentUserAfterLink = FirebaseAuth.getInstance().getCurrentUser();
                                    if (currentUserAfterLink != null) {

                                        // Fetching user after login successfully
                                        hAuthVM.fetchUser(currentUserAfterLink.getUid(), usr -> hAuthVM.fetchUserAndEmitNextDestination(usr.getUid()),
                                                () -> Toast.makeText(requireContext(), "Không tìm thấy người dùng sau khi liên kết", Toast.LENGTH_SHORT).show()
                                        );
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    if (e instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(requireContext(), "Google đã liên kết với tài khoản khác", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e("LINK", "Lỗi khi liên kết Google", e);
                                    }
                                });
                    } catch (ApiException ignored) {
                        Toast.makeText(requireContext(), "Google sign-in thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    private NavController navController;



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Methods
    // ================================

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add View Model
        hAuthVM = new ViewModelProvider(this).get(HAuthVM.class);

        // Add Nav Controller
        navController = NavHostFragment.findNavController(this);

        // Observe login result and delegate to MainActivity
        setupObserveDestionationAfterLogin();

        // Add clear icon to email input
        Drawable clearIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear);
        if (clearIcon != null) {
            clearIcon.setBounds(0, 0, clearIcon.getIntrinsicWidth(), clearIcon.getIntrinsicHeight());
        }

        // Event on text changing
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.etEmail.setCompoundDrawables(null, null, clearIcon, null);
                } else {
                    binding.etEmail.setCompoundDrawables(null, null, null, null);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Event on touch edit text
        binding.etEmail.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (binding.etEmail.getCompoundDrawables()[2] != null) {
                    int iconStart = binding.etEmail.getWidth() - binding.etEmail.getPaddingEnd() - clearIcon.getIntrinsicWidth();
                    if (event.getX() >= iconStart) {
                        binding.etEmail.setText("");
                        return true;
                    }
                }
            }
            return false;
        });

        // Event on toggle password
        binding.cbShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        // Sign-out any existing session (i guess)
        FirebaseAuth.getInstance().signOut();
        mAuth = FirebaseAuth.getInstance();

        // Auto login if user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            hAuthVM.fetchUser(currentUser.getUid(),
                    user -> {
                        Log.d("FIREBASE_USER", "Tải user thành công: " + user.getEmail() + ", role = " + user.getRole());

                        // Fetching user after login successfullly
                        hAuthVM.fetchUser(currentUser.getUid(),
                                usr -> hAuthVM.fetchUserAndEmitNextDestination(usr.getUid()),
                                () -> Toast.makeText(requireContext(), "Không tìm thấy người dùng sau khi liên kết", Toast.LENGTH_SHORT).show()
                        );
                    },
                    () -> Log.e("FIREBASE_USER", "Không tìm thấy user trong Firestore")
            );
            return;
        }

        // Setup Google Login
        setupGoogleLogin();

        // Setup all binding buttons
        setupBindingButtons();
    }

    /**
     * Setup all binding buttons
     */
    private void setupBindingButtons() {
        binding.btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            Log.d("LOGIN_FLOW", "Launching Google Sign-In intent");
            googleSignInLauncher.launch(signInIntent);
        });

        Log.d("CHECK_FIREBASE", "Client ID: " + getString(R.string.default_client_id));

        binding.tvRegister.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_resetFragment);
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

    /**
     * Configure Google Sign-In launcher
     */
    private void setupGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_client_id)) // Client ID từ Firebase
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> Log.d("LOGIN_FLOW", "Google Sign-In state reset done"));
    }

    /**
     * Observe destination after login
     */
    private void setupObserveDestionationAfterLogin() {
        hAuthVM.getNextDestinationLive().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            MainActivity mainActivity = (MainActivity) requireActivity();
            if (Role.ADMIN.getValue().equalsIgnoreCase(user.getRole())) {
                mainActivity.onLoginAsAdmin();
            } else {
                mainActivity.onLoginAsCustomer();
            }
        });
    }

    private void loginAsUser(LoginMethod method, String key, @Nullable String optionalPassword) {
        hAuthVM.login(method, key, optionalPassword,
                () -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {

                        // Fetching user after login successfullly
                        hAuthVM.fetchUser(currentUser.getUid(),
                                usr -> hAuthVM.fetchUserAndEmitNextDestination(usr.getUid()),
                                () -> Toast.makeText(requireContext(), "Không tìm thấy người dùng sau khi liên kết", Toast.LENGTH_SHORT).show()
                        );
                    }
                },
                () -> Toast.makeText(requireContext(), "Sai thông tin hoặc tài khoản không tồn tại", Toast.LENGTH_SHORT).show(),
                () -> Toast.makeText(requireContext(), "Lỗi hệ thống, thử lại sau!", Toast.LENGTH_SHORT).show()
        );
    }

}
