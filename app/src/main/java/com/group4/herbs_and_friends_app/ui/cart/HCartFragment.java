package com.group4.herbs_and_friends_app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.databinding.FragmentHCartBinding;
import com.group4.herbs_and_friends_app.ui.cart.adapter.HCartRecycleViewAdapter;
import com.group4.herbs_and_friends_app.ui.cart.viewholder.IViewHolderListeners;
import com.group4.herbs_and_friends_app.ui.checkout.HCheckoutFragment;
import com.group4.herbs_and_friends_app.ui.manage.HProductManageFragment;
import com.group4.herbs_and_friends_app.ui.manage.HProductManageFragmentDirections;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCartFragment extends Fragment {

    // ================================
    // === Fields
    // ================================

    private FragmentHCartBinding binding;
    private HCartRecycleViewAdapter hCartRecycleViewAdapter;
    private HCartVM hCartVM;
    private FirebaseUser currentUser;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupEnsureLoggedInAndInit();
    }

    // ================================
    // === Methods
    // ================================

    /**
     * Re calling when user login and first time open app
     */
    private void setupEnsureLoggedInAndInit() {
        // Check login state
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean loggedIn = currentUser != null;

        // Toggle UI for the login state
        binding.llNotLoggedIn.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        binding.rvCart.setVisibility(loggedIn ? View.VISIBLE : View.INVISIBLE);
        binding.llCartSummary.setVisibility(loggedIn ? View.VISIBLE : View.INVISIBLE);
        binding.btnCheckout.setEnabled(loggedIn);

        // If not loggin, binding event fro btn go to login
        if (!loggedIn) {
            return;
        }

        // Setup View Model
        hCartVM = new ViewModelProvider(requireActivity()).get(HCartVM.class);

//        // Setup Action Bar
//        setupActionBar();
//
        // Setup Adapters
        setupAdapterAndRecyclerView();
//
//        // Setup observer cart items
        setupObserverCartItems();
//
//        // Setup observer total price
        setupObserverTotalPrice();
//
//        // Setup button checkout
        setupButtonCheckout();
    }

    /**
     * Observe cart items
     */
    private void setupObserverCartItems() {
        hCartVM.getCartItemsLive().observe(getViewLifecycleOwner(), cartItems -> {
            hCartRecycleViewAdapter.submitList(cartItems);
        });
    }

    /**
     * Observe total price
     */
    private void setupObserverTotalPrice() {
        hCartVM.getTotalPriceLive().observe(getViewLifecycleOwner(), sum -> {
            binding.tvSubtotalPrice.setText(DisplayFormat.toMoneyDisplayString(sum));
        });
    }

    /**
     * Setup adapter and recycler view
     */
    private void setupAdapterAndRecyclerView() {
        hCartRecycleViewAdapter = new HCartRecycleViewAdapter(new IViewHolderListeners() {
            @Override
            public void onItemModifyQuantity(String cartItemId, int currQuantity, int quantityChange) {
                hCartVM.modifyQuantity(cartItemId, currQuantity, quantityChange).observe(getViewLifecycleOwner(), success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Cập nhập số lượng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.rvCart.setAdapter(hCartRecycleViewAdapter);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * Setup button checkout
     */
    private void setupButtonCheckout() {
        binding.btnCheckout.setOnClickListener(v -> {
            NavHostFragment.findNavController(HCartFragment.this).navigate(
                    HCartFragmentDirections.fromCartToCheckoutNavigation()
            );;
        });
    }
}