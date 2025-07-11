package com.group4.herbs_and_friends_app.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.data.repository.CartRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HCartVM extends ViewModel {

    // =================================
    // === Fields
    // =================================

    private final CartRepository cartRepository;
    private LiveData<List<CartItem>> cartItems;

    // =================================
    // === Constructors
    // =================================

    @Inject
    public HCartVM(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
        this.cartItems = this.cartRepository.getCartItems();
    }

    // =================================
    // === Methods
    // =================================

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void modifyQuantity(String productId, int delta) {
        cartRepository.updateQuantity(productId, delta);
    }
}