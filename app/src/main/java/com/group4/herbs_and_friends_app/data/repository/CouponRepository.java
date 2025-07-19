package com.group4.herbs_and_friends_app.data.repository;

import android.os.Debug;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.group4.herbs_and_friends_app.data.model.Coupon;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CouponRepository {
    private final String COLLECTION_NAME = "coupons";
    private final CollectionReference coupons;

    public CouponRepository(FirebaseFirestore firestore) {
        this.coupons = firestore.collection(COLLECTION_NAME);
    }

    public LiveData<List<Coupon>> getAllCoupons() {
        MutableLiveData<List<Coupon>> couponListLive = new MutableLiveData<>();
        coupons.get()
                .addOnSuccessListener(query -> {
                    Log.d("CouponRepository", "Success getting coupons: " + query.size());
                    List<Coupon> couponList = query.toObjects(Coupon.class);
                    couponListLive.setValue(couponList);
                })
                .addOnFailureListener(query -> {
                    Log.e("CouponRepository", "Error getting coupons: " + query.getMessage());
                    couponListLive.setValue(Collections.emptyList());
                });

        return couponListLive;
    }

    // TODO: add get coupons by criteria: e.g get active, get non-expired, get near expired, get new coupons, sort by expiry date, etc

    public LiveData<Coupon> getCouponById(String couponId) {
        MutableLiveData<Coupon> couponLive = new MutableLiveData<>();
        coupons.document(couponId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Coupon coupon = doc.toObject(Coupon.class);
                        couponLive.setValue(coupon);
                    } else {
                        couponLive.setValue(null);
                    }
                });
        return couponLive;
    }

    public LiveData<Boolean> addCoupon(Coupon coupon) {
        MutableLiveData<Boolean> isAddSuccess = new MutableLiveData<>();

        coupons.document(coupon.getId())
                .set(coupon)
                .addOnSuccessListener(doc -> isAddSuccess.setValue(true))
                .addOnFailureListener(e -> isAddSuccess.setValue(false));

        return isAddSuccess;
    }

    public LiveData<Boolean> updateCoupon(String couponId, Map<String,Object> updatedCouponFields) {
        MutableLiveData<Boolean> isDeleteSuccess = new MutableLiveData<>();

        coupons.document(couponId)
                .update(updatedCouponFields)
                .addOnSuccessListener(doc -> isDeleteSuccess.setValue(true))
                .addOnFailureListener(e -> isDeleteSuccess.setValue(false));

        return isDeleteSuccess;
    }

    public LiveData<Boolean> deleteCoupon(String couponId) {
        MutableLiveData<Boolean> isDeleteSuccess = new MutableLiveData<>();

        coupons.document(couponId)
                .delete()
                .addOnSuccessListener(doc -> isDeleteSuccess.setValue(true))
                .addOnFailureListener(e -> isDeleteSuccess.setValue(false));

        return isDeleteSuccess;
    }
}
