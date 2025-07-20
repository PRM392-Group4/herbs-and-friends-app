package com.group4.herbs_and_friends_app.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.Write;
import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.Order;
import com.group4.herbs_and_friends_app.data.model.OrderItem;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRepository {
    
    private final FirebaseFirestore firestore;
    private final CollectionReference ordersRef;
    private final FirebaseAuth auth;

    public OrderRepository(FirebaseFirestore firestore, FirebaseAuth auth) {
        this.firestore = firestore;
        this.auth = auth;
        this.ordersRef = firestore.collection("orders");
    }

    public LiveData<Order> getOrderWithItems(String orderId) {
        MutableLiveData<Order> result = new MutableLiveData<>();
        ordersRef.document(orderId).get()
                .addOnSuccessListener(orderDoc -> {
                    if (orderDoc.exists()) {
                        Order order = orderDoc.toObject(Order.class);
                        loadItemsAndCouponForOrder(order, () -> {
                            result.setValue(order);
                        });
                    } else {
                        result.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    result.setValue(null);
                });
        
        return result;
    }

    public LiveData<List<Order>> getUserOrders() {
        MutableLiveData<List<Order>> result = new MutableLiveData<>();
        
        loadOrdersWithAllData(result);
        
        return result;
    }

    private void loadOrdersWithAllData(MutableLiveData<List<Order>> result) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            result.setValue(Collections.emptyList());
            return;
        }
        
        String userId = currentUser.getUid();
        ordersRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    List<Order> orders = query.toObjects(Order.class);
                    sortOrdersByDate(orders);
                    
                    // Load items and coupons for all orders
                    loadItemsForAllOrders(orders, result);
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderRepository", "Failed to load orders", e);
                    result.setValue(Collections.emptyList());
                });
    }

    private void loadItemsForAllOrders(List<Order> orders, MutableLiveData<List<Order>> result) {
        if (orders.isEmpty()) {
            result.setValue(orders);
            return;
        }
        
        Log.d("OrderRepository", "Loading items for " + orders.size() + " orders");
        
        AtomicInteger completed = new AtomicInteger(0);
        final int totalOrders = orders.size();
        
        for (Order order : orders) {
            Log.d("quantity of order id: ", order.getId() + " is " + order.getTotalItemCount());
            
            loadItemsAndCouponForOrder(order, () -> {
                int completedCount = completed.incrementAndGet();
                Log.d("OrderRepository", "Completed " + completedCount + "/" + totalOrders + " orders");
                
                if (completedCount == totalOrders) {
                    Log.d("OrderRepository", "All orders loaded, updating UI");
                    result.setValue(orders);
                }
            });
        }
    }

    private void loadItemsAndCouponForOrder(Order order, Runnable onComplete) {
        ordersRef.document(order.getId())
                .collection("items")
                .get()
                .addOnSuccessListener(itemsQuery -> {
                    List<OrderItem> items = itemsQuery.toObjects(OrderItem.class);
                    order.setItems(items);
                    
                    // Load coupon if exists
                    loadCouponForOrder(order, onComplete);
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderRepository", "Failed to load items for order " + order.getId(), e);
                    // Continue even if items fail to load
                    loadCouponForOrder(order, onComplete);
                });
    }

    private void loadCouponForOrder(Order order, Runnable onComplete) {
        if (order.getCoupon() != null) {
            order.getCoupon()
                    .get()
                    .addOnSuccessListener(couponDoc -> {
                        if (couponDoc.exists()) {
                            Log.d("coupon: ", couponDoc.toString());
                            Coupon coupon = couponDoc.toObject(Coupon.class);
                            order.setAppliedCoupon(coupon);
                            Log.d("coupon: ", order.getAppliedCoupon().toString());
                        } else {
                            order.setAppliedCoupon(null);
                            Log.d("coupon: ", "Coupon document doesn't exist");
                        }
                        onComplete.run();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrderRepository", "Failed to load coupon for order " + order.getId(), e);
                        order.setAppliedCoupon(null);
                        onComplete.run();
                    });
        } else {
            // No coupon reference, complete immediately
            onComplete.run();
        }
    }

    public LiveData<List<Coupon>> loadCoupons(){
        MutableLiveData<List<Coupon>> couponsLiveData = new MutableLiveData<>();
        firestore.collection("coupons").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                couponsLiveData.setValue(null);
                return;
            }

            List<Coupon> items = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Coupon item = doc.toObject(Coupon.class);
//                if (item!=null && item.getExpiryDate().after(new Date())){
                    item.setId(doc.getId());
                    items.add(item);
//                }
            }
            couponsLiveData.setValue(items);
        });
        return couponsLiveData;
    }

    private void sortOrdersByDate(List<Order> orders) {
        orders.sort((o1, o2) -> {
            if (o1.getPlacedAt() == null && o2.getPlacedAt() == null) return 0;
            if (o1.getPlacedAt() == null) return 1;
            if (o2.getPlacedAt() == null) return -1;
            return o2.getPlacedAt().compareTo(o1.getPlacedAt()); // Newest first
        });
    }

    public LiveData<List<OrderItem>> getOrderItems(String orderId) {
        MutableLiveData<List<OrderItem>> result = new MutableLiveData<>();
        
        ordersRef.document(orderId)
                .collection("items")                    
                .get()
                .addOnSuccessListener(query -> {
                    List<OrderItem> items = query.toObjects(OrderItem.class);
                    result.setValue(items);
                })
                .addOnFailureListener(e -> {
                    result.setValue(Collections.emptyList());
                });
        
        return result;
    }


    public LiveData<Boolean> updateOrderStatus(String orderId, String newStatus) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        ordersRef.document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        
        return result;
    }

    /**
     * Get all orders (for admin/management purposes)
     * This loads all orders from all users with their items and coupons
     */
    public LiveData<List<Order>> getAllOrders() {
        MutableLiveData<List<Order>> result = new MutableLiveData<>();
        
        ordersRef.get()
                .addOnSuccessListener(query -> {
                    List<Order> orders = query.toObjects(Order.class);
                    sortOrdersByDate(orders);
                    
                    // Load items and coupons for all orders
                    loadItemsForAllOrders(orders, result);
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderRepository", "Failed to load all orders", e);
                    result.setValue(Collections.emptyList());
                });
        
        return result;
    }

    public LiveData<String> createOrder(@NonNull Order order) {
        MutableLiveData<String> result = new MutableLiveData<>(null);
        DocumentReference orderDoc = ordersRef.document();

        order.setPlacedAt(new Date());

        orderDoc.set(order)
                .addOnSuccessListener(unused -> {
                    CollectionReference itemsRef = orderDoc.collection("items");
                    WriteBatch batch = firestore.batch();
                    for (OrderItem item : order.getItems()) {
                        DocumentReference itemDoc = itemsRef.document(item.getProductId());
                        batch.set(itemDoc, item);
                    }
                    batch.commit().addOnCompleteListener(aVoid -> {
                        Log.d("OrderRepository", "Batch commit successful for order: " + orderDoc.getId());
                        result.setValue(orderDoc.getId());
                    });
                })
                .addOnFailureListener(aVoid -> {
                    result.setValue(null);
                    Log.d("Order created failed", "Error: "+aVoid.getMessage());
                });
        return result;
    }
}
