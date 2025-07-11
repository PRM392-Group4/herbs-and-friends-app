package com.group4.herbs_and_friends_app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.group4.herbs_and_friends_app.data.model.CartItem;

import java.util.Collections;
import java.util.List;

public class CartRepository {

    // ==============================
    // === Fields
    // ==============================

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final CollectionReference itemsRef;

    // ==============================
    // === Constructors
    // ==============================

    public CartRepository(FirebaseFirestore firestore, FirebaseAuth auth) {
        this.firestore = firestore;
        this.auth = auth;
        itemsRef = firestore
                .collection("carts")
                .document(auth.getUid())
                .collection("items");
    }

    // ==============================
    // === Methods
    // ==============================

    /**
     * Get live cart items
     *
     * @return
     */
    public LiveData<List<CartItem>> getLiveCartItems() {
        MutableLiveData<List<CartItem>> live = new MutableLiveData<>();
        itemsRef.addSnapshotListener((snap, error) ->
        {
            if (error != null || snap == null) {
                live.postValue(Collections.emptyList());
                return;
            }
            live.postValue(snap.toObjects(CartItem.class));
        });
        return live;
    }

    /**
     * Update field quantity based on delta
     */
    public LiveData<Boolean> updateQuantity(String productId, int delta) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        itemsRef
                .document(productId)
                .update("quantity", FieldValue.increment(delta))
                .addOnSuccessListener(aVoid -> result.postValue(true))
                .addOnFailureListener(e -> result.postValue(false));
        return result;
    }

    /**
     * Add or update the new cart item
     */
    public LiveData<Boolean> addOrUpdateItemToCart(CartItem item) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        itemsRef
                .document(item.getProductId())
                .set(item)
                .addOnSuccessListener(aVoid -> result.postValue(true))
                .addOnFailureListener(e -> result.postValue(false));
        return result;
    }

    /**
     * Remove the cart item from the cart
     */
    public LiveData<Boolean> removeItem(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        itemsRef
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> result.postValue(true))
                .addOnFailureListener(e -> result.postValue(false));
        return result;
    }

    /**
     * Remove all cart items
     */
    public LiveData<Boolean> clearCart() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        itemsRef
                .get()
                .addOnSuccessListener(query -> {
                    WriteBatch batch = firestore.batch();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener(aVoid -> result.postValue(true))
                            .addOnFailureListener(e -> result.postValue(false));
                })
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }
}
