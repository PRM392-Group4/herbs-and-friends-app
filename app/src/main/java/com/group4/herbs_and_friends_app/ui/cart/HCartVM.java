package com.group4.herbs_and_friends_app.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.data.repository.CartRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HCartVM extends ViewModel {

    // =================================
    // === Fields
    // =================================

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private LiveData<List<CartItem>> cartItemsLive;
    private LiveData<Long> totalPriceLive;

    private final LiveData<Boolean> isCartEmpty;

    // =================================
    // === Constructors
    // =================================

    @Inject
    public HCartVM(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
        this.cartItemsLive = this.cartRepository.getLiveCartItems();
        this.totalPriceLive = this.cartRepository.getLiveTotalPrice();

        this.isCartEmpty = Transformations.map(cartItemsLive, list -> {
            return list.isEmpty() || list == null;
        });
    }

    // =================================
    // === Methods
    // =================================

    public LiveData<Boolean> getIsCartEmptyLive() {
        return isCartEmpty;
    }

    /**
     * Get live cart items
     *
     * @return
     */
    public LiveData<List<CartItem>> getCartItemsLive() {
        if (cartItemsLive == null) return null;
        return cartItemsLive;
    }

    /**
     * Get live total price
     *
     * @return
     */
    public LiveData<Long> getTotalPriceLive() {
        if (cartItemsLive == null) return null;
        return totalPriceLive;
    }

    /**
     * Change quantity by +1 or -1
     *
     * @param productId
     * @param delta
     * @return
     */
    public LiveData<Boolean> modifyQuantity(String productId, int delta) {
        return cartRepository.updateQuantity(productId, delta);
    }
}