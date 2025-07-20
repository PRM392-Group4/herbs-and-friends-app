package com.group4.herbs_and_friends_app;

import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.group4.herbs_and_friends_app.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPaySDK;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    // ================================
    // === Fields
    // ================================

    private NavController navController;
    private ActivityMainBinding binding;

    // ================================
    // === Constructors
    // ================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup View Compat Paddings
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Setup NavHost Controller
        setupNavHostFragment();

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        Intent intent = getIntent();
        if (intent != null) {
            ZaloPaySDK.getInstance().onResult(intent);
        }
    }

    @Override
    public void onNewIntent(@NonNull Intent intent, @NonNull ComponentCaller caller) {
        super.onNewIntent(intent, caller);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    // ================================
    // === Methods
    // ================================

    public void setupNavHostFragment() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.herbNavHostFragment.getId());

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.herbBottomNavigation, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destinationId = destination.getId();
                if (destinationId == R.id.HCheckoutFragment || destinationId == R.id.HOrderResultFragment) {
                    binding.herbBottomNavigation.setVisibility(View.GONE);
//                    binding.herbNavHostFragment.setVisibility(View.GONE);
                } else {
                    binding.herbNavHostFragment.setVisibility(View.GONE);
//                    binding.herbBottomNavigation.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}