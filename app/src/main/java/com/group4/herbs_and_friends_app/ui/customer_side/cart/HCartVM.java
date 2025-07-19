package com.group4.herbs_and_friends_app.ui.customer_side.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.CartItem;
import com.group4.herbs_and_friends_app.data.model.Product;
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
    private LiveData<List<CartItem>> cartItemsLive;
    private LiveData<Long> totalPriceLive;
    private ProductRepository productRepository;

    // =================================
    // === Constructors
    // =================================

    @Inject
    public HCartVM(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemsLive = this.cartRepository.getLiveCartItems();
        this.totalPriceLive = this.cartRepository.getLiveTotalPrice();
    }

    // =================================
    // === Methods
    // =================================

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
    public LiveData<Boolean> modifyQuantity(String productId, int currentQty, int delta) {
        if (currentQty + delta <= 0) {
            return cartRepository.removeItem(productId);
        } else {
            return cartRepository.updateQuantity(productId, delta);
        }
    }

    /**
     * Add or update the item to cart
     *
     * @param productId
     * @param quantity
     * @return
     */
    public LiveData<Boolean> addOrUpdateItemToCart(String productId, int quantity) {
        LiveData<Product> productLive = productRepository.getProductById(productId);

        return Transformations.switchMap(productLive, product -> {

            // If don't have any item, emit false
            if (product == null) {
                MutableLiveData<Boolean> failed = new MutableLiveData<>();
                failed.setValue(false);
                return failed;
            }

            // build cart item and hand it off
            CartItem item = new CartItem(
                    productId,
                    product.getName(),
                    product.getPrice(),
                    product.getThumbnail(),
                    quantity
            );

            return cartRepository.addOrUpdateItemToCart(item);
        });
    }
}