package com.group4.herbs_and_friends_app.ui.customer_side.order;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.group4.herbs_and_friends_app.ui.customer_side.profile.HProfileFragmentDirections;

public class OrderHistoryClickHandler {

    /**
     * Navigate to OrderDetails from Profile Fragment (Order History)
     * Call this method when user clicks on an order history item
     */
    public static void navigateToOrderDetailsFromProfile(Fragment fragment, String orderId) {
        NavController navController = NavHostFragment.findNavController(fragment);
        HProfileFragmentDirections.ActionProfileFragmentToHOrderDetailFragment action =
                HProfileFragmentDirections.actionProfileFragmentToHOrderDetailFragment(orderId);
        navController.navigate(action);
    }

} 