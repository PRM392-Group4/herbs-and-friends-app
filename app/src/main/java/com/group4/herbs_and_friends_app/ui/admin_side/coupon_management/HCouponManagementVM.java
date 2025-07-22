package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.model.CouponParams;
import com.group4.herbs_and_friends_app.data.model.enums.CouponSortOptions;
import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;

import java.util.Date;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class HCouponManagementVM extends ViewModel {
    private final CouponRepository couponRepository;
    private final MutableLiveData<CouponParams> paramsLive = new MutableLiveData<>();
    private final MutableLiveData<List<Coupon>> filteredCouponsLive = new MutableLiveData<>();
    private final NotificationPublisher notificationPublisher;
    
    @Inject
    public HCouponManagementVM(CouponRepository couponRepository, NotificationPublisher notificationPublisher) {
        this.couponRepository = couponRepository;
        // Initialize with default params
        CouponParams defaultParams = new CouponParams();
        paramsLive.setValue(defaultParams);
        
        // Observe params changes and update coupons
        paramsLive.observeForever(this::updateCoupons);

        this.notificationPublisher = notificationPublisher;
    }
    
    public LiveData<CouponParams> getParamsLive() {
        return paramsLive;
    }
    public LiveData<Coupon> getCouponLiveData(String couponId) {
        return couponRepository.getCouponById(couponId);
    }

    public LiveData<List<Coupon>> getCouponListLive() {
        return filteredCouponsLive;
    }

    public LiveData<Boolean> saveCoupon(Coupon coupon) {
        var title = "Mã khuyến mãi mới: " + coupon.getCode();
        var newNotification = new NotificationDto(title, NotificationTypes.NEW_COUPON_ADDED, new Date());
        notificationPublisher.tryPublishToAllUsers(newNotification);

        return couponRepository.addCoupon(coupon);
    }
    public void setParamsLive(CouponParams params) {
        paramsLive.setValue(params);
    }
    
    public void setSearch(String search) {
        CouponParams currentParams = paramsLive.getValue();
        if (currentParams == null) {
            currentParams = new CouponParams();
        }
        currentParams.setSearch(search);
        paramsLive.setValue(currentParams);
    }
    
    public void setDateRange(Date startDate, Date endDate) {
        CouponParams currentParams = paramsLive.getValue();
        if (currentParams == null) {
            currentParams = new CouponParams();
        }
        currentParams.setEffectiveDate(startDate);
        currentParams.setExpiryDate(endDate);
        paramsLive.setValue(currentParams);
    }
    
    public void clearFilters() {
        CouponParams defaultParams = new CouponParams();
        paramsLive.setValue(defaultParams);
    }
    
    public LiveData<Boolean> deleteCoupon(String couponId) {
        return couponRepository.deleteCoupon(couponId);
    }
    
    public void refreshData() {
        CouponParams currentParams = paramsLive.getValue();
        if (currentParams != null) {
            paramsLive.setValue(currentParams);
        }
    }

    private void updateCoupons(CouponParams params) {
        if (params == null) return;
        
        // Get coupons with search and sort criteria
        couponRepository.getCouponWithCriteria(params, true).observeForever(coupons -> {
            if (coupons != null) {
                // Apply date range filtering if dates are set
                List<Coupon> filteredCoupons = applyDateRangeFilter(coupons, params);
                filteredCouponsLive.setValue(filteredCoupons);
            }
        });
    }
    
    private List<Coupon> applyDateRangeFilter(List<Coupon> coupons, CouponParams params) {
        if (params.getEffectiveDate() == null && params.getExpiryDate() == null) {
            return coupons;
        }
        
        List<Coupon> filteredList = new java.util.ArrayList<>();
        Date now = new Date();
        
        for (Coupon coupon : coupons) {
            boolean includeCoupon = true;
            
            // Filter by start date (effective date)
            if (params.getEffectiveDate() != null) {
                if (coupon.getEffectiveDate() == null || 
                    coupon.getEffectiveDate().before(params.getEffectiveDate())) {
                    includeCoupon = false;
                }
            }
            
            // Filter by end date (expiry date)
            if (params.getExpiryDate() != null) {
                if (coupon.getExpiryDate() == null || 
                    coupon.getExpiryDate().after(params.getExpiryDate())) {
                    includeCoupon = false;
                }
            }
            
            if (includeCoupon) {
                filteredList.add(coupon);
            }
        }
        
        return filteredList;
    }
}