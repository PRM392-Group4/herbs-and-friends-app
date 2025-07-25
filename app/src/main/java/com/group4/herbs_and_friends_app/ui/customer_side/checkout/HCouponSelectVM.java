package com.group4.herbs_and_friends_app.ui.customer_side.checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.repository.CartRepository;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HCouponSelectVM extends ViewModel {

    private final LiveData<List<Coupon>> couponListLive;

    @Inject
    public HCouponSelectVM(CouponRepository couponRepository, CartRepository cartRepository) {
        couponListLive = couponRepository.getAllCoupons(false);
    }

    public LiveData<List<Coupon>> getCouponListLive() {
        return couponListLive;
    }
}