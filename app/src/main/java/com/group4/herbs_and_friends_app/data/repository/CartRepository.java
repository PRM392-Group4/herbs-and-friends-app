package com.group4.herbs_and_friends_app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private CollectionReference cartItemsRef = null;
    private DocumentReference cartRef = null;

    // ==============================
    // === Constructors
    // ==============================

    public CartRepository(FirebaseFirestore firestore,
                          FirebaseAuth firebaseAuth) {
        this.firestore = firestore;
        this.auth = firebaseAuth;

        // Get uid for specific cart
        FirebaseUser currentUser = auth.getCurrentUser();

        // If no uid, which mean not loggin, then return null
        if (currentUser != null) {
            String uid = currentUser.getUid();
            cartItemsRef = firestore
                    .collection("carts")
                    .document(uid)
                    .collection("items");
            cartRef = firestore
                    .collection("carts")
                    .document(uid);
        }
    }

    // ==============================
    // === Methods
    // ==============================

    /**
     * Get live cart items
     *
     * @return live data cart items object
     */
    public LiveData<List<CartItem>> getLiveCartItems() {
        MutableLiveData<List<CartItem>> live = new MutableLiveData<>();

        // Observing real time changes in cart items with single source of truth
        cartItemsRef.addSnapshotListener((snap, error) ->
        {
            if (error != null || snap == null) {
                live.postValue(Collections.emptyList());
                return;
            }
            live.postValue(snap.toObjects(CartItem.class));

            // Recalculate & write totalPrice everytime the cart changes
            long sum = 0L;
            for (CartItem item : snap.toObjects(CartItem.class)) {
                sum += item.getPrice() * item.getQuantity();
            }
            cartRef.update("totalPrice", sum);
        });
        return live;
    }

    /**
     * Get live total price
     *
     * @return live data total price object
     */
    public LiveData<Long> getLiveTotalPrice() {
        MutableLiveData<Long> live = new MutableLiveData<>(0L);
        cartRef.addSnapshotListener((snap, err) -> {
            if (err != null || snap == null || !snap.exists()) {
                live.postValue(0L);
                return;
            }

            // get snap from firebase
            Long snapTp = snap.getLong("totalPrice");
            live.postValue(snapTp != null ? snapTp.longValue() : 0L);
        });

        return live;
    }

    /**
     * Update field quantity based on delta
     */
    public LiveData<Boolean> updateQuantity(String productId, int delta) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        cartItemsRef
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
        cartItemsRef
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
        cartItemsRef
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
        cartItemsRef
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
