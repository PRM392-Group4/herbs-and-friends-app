package com.group4.herbs_and_friends_app.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private final LiveData<List<CartItem>> cartItemsLive;
    private final LiveData<Boolean> modifyQuantityLive;
    private final LiveData<Long> totalPriceLive;

    // =================================
    // === Constructors
    // =================================

    @Inject
    public HCartVM(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
        this.cartItemsLive = this.cartRepository.getLiveCartItems();
        this.totalPriceLive = this.cartRepository.get
        modifyQuantityLive = new MutableLiveData<>(false);


    }

    // =================================
    // === Methods
    // =================================

    public LiveData<List<CartItem>> getCartItems() {
        return cartItemsLive;
    }

    public LiveData<Boolean> modifyQuantity(String productId, int delta) {
        MutableLiveData<Boolean> result = new MutableLiveData<>(false);


    }
}