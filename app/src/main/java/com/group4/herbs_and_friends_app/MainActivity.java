package com.group4.herbs_and_friends_app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.data.model.enums.Role;
import com.group4.herbs_and_friends_app.databinding.ActivityMainBinding;
import com.group4.herbs_and_friends_app.ui.auth.login.HAuthVM;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    // ================================
    // === Fields
    // ================================

    private NavController navController;
    private ActivityMainBinding binding;
    private HAuthVM hAuthVM;

    // ================================
    // === Constructors
    // ================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup view models
        hAuthVM = new ViewModelProvider(this).get(HAuthVM.class);

        // Setup View Compat Paddings
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Setup NavHost Controller
        setupNavHostFragment();

        // Setup listeners on already login user
        setupOnListeningForCurrentLoginUser();
    }

    // ================================
    // === Methods
    // ================================

    /**
     * Setup listeners on current login user
     */
    private void setupOnListeningForCurrentLoginUser() {

        // Listening on next destination on different user role
        hAuthVM.getNextDestinationLive().observe(this, user -> {

            // By default, goes to anonymous
            if (user == null) {
                onLoginAsGuest();
                return;
            }

            if (Role.ADMIN.getValue().equalsIgnoreCase(user.getRole())) {
                onLoginAsAdmin();
            } else {
                onLoginAsCustomer();
            }
        });

        // If there’s already a FirebaseUser, emit to the VM to trigger that observer:
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            hAuthVM.fetchUserAndEmitNextDestination(currentUser.getUid());
        }
    }

    /**
     * Setup the Nav Host system
     */
    public void setupNavHostFragment() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.herbNavHostFragment.getId());

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.herbBottomNavigation, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destinationId = destination.getId();
                if (destinationId == R.id.HCheckoutFragment) {
                    binding.herbBottomNavigation.setVisibility(View.GONE);
                } else {
                    binding.herbBottomNavigation.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Setup the Nav Host system if current user is admin
     */
    public void onLoginAsAdmin() {
        navController.setGraph(R.navigation.navigation_h_admin_main, null);

        // clear the current menu and inflate new navigation
        binding.herbBottomNavigation.getMenu().clear();
        binding.herbBottomNavigation.inflateMenu(R.menu.view_h_admin_bottom_nav_menu);

        NavigationUI.setupWithNavController(binding.herbBottomNavigation, navController);

        // redirect to dashboard fragment
        navController.navigate(R.id.dashboardFragment);
    }

    /**
     * Setup the Nav Host system if current user is customer
     */
    public void onLoginAsCustomer() {
        navController.setGraph(R.navigation.navigation_h_main, null);

        // clear the current menu and inflate new navigation
        binding.herbBottomNavigation.getMenu().clear();
        binding.herbBottomNavigation.inflateMenu(R.menu.view_h_bottom_nav_menu);

        NavigationUI.setupWithNavController(binding.herbBottomNavigation, navController);

        // redirect to profile fragment
        navController.navigate(R.id.profileFragment);
    }

    /**
     * Guest flow: nav graph stays on customer graph start (home)
     */
    private void onLoginAsGuest() {
        navController.setGraph(R.navigation.navigation_h_main, null);

        binding.herbBottomNavigation.getMenu().clear();
        binding.herbBottomNavigation.inflateMenu(R.menu.view_h_bottom_nav_menu);

        NavigationUI.setupWithNavController(binding.herbBottomNavigation, navController);
    }
}