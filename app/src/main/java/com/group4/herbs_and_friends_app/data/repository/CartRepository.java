package com.group4.herbs_and_friends_app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.group4.herbs_and_friends_app.data.model.CartItem;

import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class CartRepository {

    // ==============================
    // === Fields
    // ==============================

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final CollectionReference collectionReference;

    // ==============================
    // === Constructors
    // ==============================

    public CartRepository(FirebaseFirestore firestore, FirebaseAuth auth) {
        this.firestore = firestore;
        this.auth = auth;
        collectionReference = firestore
                .collection("carts")
                .document(auth.getUid())
                .collection("items");
    }

    // ==============================
    // === Methods
    // ==============================

    /**
     * Get a current cart items
     *
     * @return
     */
    public LiveData<List<CartItem>> getCartItems() {
        MutableLiveData<List<CartItem>> live = new MutableLiveData<>();
        collectionReference.get()
                .addOnSuccessListener(query -> {

                    if (query != null) {
                        live.setValue(Collections.emptyList());
                        return;
                    }

                    List<CartItem> cartItems = query.toObjects(CartItem.class);
                    live.setValue(cartItems);
                });
        return live;
    }

    /**
     * Add or update the new cart item
     */
    public LiveData<Boolean> addOrUpdateItem(CartItem item) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        collectionReference
                .document(item.getProductId())
                .set(item)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    /**
     * Update field quantity on the cart item
     */
    public LiveData<Boolean> updateQuantity(String productId, int newQty) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        collectionReference
                .document(productId)
                .update("quantity", newQty)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    /**
     * Remove the cart item from the cart
     */
    public LiveData<Boolean> removeItem(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        collectionReference
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    /**
     * Remove all cart items
     */
    public LiveData<Boolean> clearCart() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        collectionReference
                .get()
                .addOnSuccessListener(query -> {
                    WriteBatch batch = firestore.batch();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener(aVoid -> result.setValue(true))
                            .addOnFailureListener(e -> result.setValue(false));
                })
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }
}
