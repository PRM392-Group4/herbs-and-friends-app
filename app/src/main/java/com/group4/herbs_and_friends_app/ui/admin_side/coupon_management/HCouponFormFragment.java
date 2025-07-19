package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Coupon;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;
import com.group4.herbs_and_friends_app.databinding.FragmentHCouponFormBinding;
import com.group4.herbs_and_friends_app.ui.admin_side.product_management.HProductManagementVM;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCouponFormFragment extends Fragment {

    // ================================
    // === Fields
    // ================================
    private FragmentHCouponFormBinding binding;
    private HCouponManagementVM hCouponManagementVM;
    private Date effectiveDate = null;
    private Date expiryDate = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();

    private String editingCouponId;
    private Coupon currentCoupon = null;

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHCouponFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hCouponManagementVM = new ViewModelProvider(requireActivity()).get(HCouponManagementVM.class);

        setupActionBar();
        setupDatePickers();
        setupTimePickers();
        setupQuickPresets();
        setupButtons();
        setupFormListeners();

        if (getArguments() != null && getArguments().containsKey("couponId")) {
            String argId = getArguments().getString("couponId");
            if (argId != null) {
                editingCouponId = argId;
                hCouponManagementVM.getCouponLiveData(editingCouponId)
                        .observe(getViewLifecycleOwner(), coupon -> {
                            currentCoupon = coupon;
                            if (coupon != null) {
                                populateForm(coupon);
                            }
                        });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Setup Methods
    // ================================

    private void setupActionBar() {
        binding.includeActionBarCouponForm.actionBarTitle.setText("Tạo mã giảm giá");
        binding.includeActionBarCouponForm.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    private void setupDatePickers() {
        // Effective date picker
        binding.etEffectiveDate.setOnClickListener(v -> showDatePicker(true));
        
        // Expiry date picker
        binding.etExpiryDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupTimePickers() {
        // Effective time picker
        binding.etEffectiveTime.setOnClickListener(v -> showTimePicker(true));
        
        // Expiry time picker
        binding.etExpiryTime.setOnClickListener(v -> showTimePicker(false));
    }

    private void setupQuickPresets() {
        binding.chip7Days.setOnClickListener(v -> applyQuickPreset(7));
        binding.chip30Days.setOnClickListener(v -> applyQuickPreset(30));
        binding.chip90Days.setOnClickListener(v -> applyQuickPreset(90));
        binding.chip1Year.setOnClickListener(v -> applyQuickPreset(365));
    }

    private void setupButtons() {
        binding.btnCancel.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        binding.btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                saveCoupon();
            }
        });
    }

    private void setupFormListeners() {
        // Create TextWatcher instances and store them as tags to avoid memory leaks
        TextWatcher nameWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (currentCoupon != null) {
                    currentCoupon.setName(s.toString().trim());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        
        TextWatcher codeWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (currentCoupon != null) {
                    currentCoupon.setCode(s.toString().trim().toUpperCase());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        
        TextWatcher discountWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (currentCoupon != null && !s.toString().trim().isEmpty()) {
                    try {
                        double discount = Double.parseDouble(s.toString().trim());
                        currentCoupon.setDiscount(discount);
                    } catch (NumberFormatException e) {
                        // Ignore invalid input
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        
        // Store watchers as tags and add them
        binding.etCouponName.setTag(nameWatcher);
        binding.etCouponCode.setTag(codeWatcher);
        binding.etDiscountPercentage.setTag(discountWatcher);
        
        binding.etCouponName.addTextChangedListener(nameWatcher);
        binding.etCouponCode.addTextChangedListener(codeWatcher);
        binding.etDiscountPercentage.addTextChangedListener(discountWatcher);
    }
    private void populateForm(Coupon coupon) {
        // Temporarily remove listeners to avoid triggering updates during population
        binding.etCouponName.removeTextChangedListener(binding.etCouponName.getTag() != null ? 
            (TextWatcher) binding.etCouponName.getTag() : null);
        binding.etCouponCode.removeTextChangedListener(binding.etCouponCode.getTag() != null ? 
            (TextWatcher) binding.etCouponCode.getTag() : null);
        binding.etDiscountPercentage.removeTextChangedListener(binding.etDiscountPercentage.getTag() != null ? 
            (TextWatcher) binding.etDiscountPercentage.getTag() : null);
        
        binding.etCouponName.setText(coupon.getName());
        binding.etCouponCode.setText(coupon.getCode());
        binding.etDiscountPercentage.setText(String.valueOf(coupon.getDiscount()));
        binding.etEffectiveDate.setText(dateFormat.format(coupon.getEffectiveDate()));
        binding.etEffectiveTime.setText(timeFormat.format(coupon.getEffectiveDate()));
        binding.etExpiryDate.setText(dateFormat.format(coupon.getExpiryDate()));
        binding.etExpiryTime.setText(timeFormat.format(coupon.getExpiryDate()));
        
        // Update date fields
        effectiveDate = coupon.getEffectiveDate();
        expiryDate = coupon.getExpiryDate();
        
        // Re-add listeners
        setupFormListeners();
    }

    // ================================
    // === Date/Time Picker Methods
    // ================================

    private void showDatePicker(boolean isEffectiveDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    
                    if (isEffectiveDate) {
                        effectiveDate = selectedDate;
                        binding.etEffectiveDate.setText(dateFormat.format(selectedDate));
                        if (currentCoupon != null) {
                            currentCoupon.setEffectiveDate(selectedDate);
                        }
                    } else {
                        expiryDate = selectedDate;
                        binding.etExpiryDate.setText(dateFormat.format(selectedDate));
                        if (currentCoupon != null) {
                            currentCoupon.setExpiryDate(selectedDate);
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isEffectiveTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    Date selectedTime = calendar.getTime();
                    
                    if (isEffectiveTime) {
                        if (effectiveDate != null) {
                            Calendar effectiveCal = Calendar.getInstance();
                            effectiveCal.setTime(effectiveDate);
                            effectiveCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            effectiveCal.set(Calendar.MINUTE, minute);
                            effectiveDate = effectiveCal.getTime();
                            if (currentCoupon != null) {
                                currentCoupon.setEffectiveDate(effectiveDate);
                            }
                        }
                        binding.etEffectiveTime.setText(timeFormat.format(selectedTime));
                    } else {
                        if (expiryDate != null) {
                            Calendar expiryCal = Calendar.getInstance();
                            expiryCal.setTime(expiryDate);
                            expiryCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            expiryCal.set(Calendar.MINUTE, minute);
                            expiryDate = expiryCal.getTime();
                            if (currentCoupon != null) {
                                currentCoupon.setExpiryDate(expiryDate);
                            }
                        }
                        binding.etExpiryTime.setText(timeFormat.format(selectedTime));
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24-hour format
        );
        
        timePickerDialog.show();
    }

    private void applyQuickPreset(int days) {
        Calendar now = Calendar.getInstance();
        
        // Set effective date to now
        effectiveDate = now.getTime();
        binding.etEffectiveDate.setText(dateFormat.format(effectiveDate));
        binding.etEffectiveTime.setText(timeFormat.format(effectiveDate));
        
        // Set expiry date to now + days
        now.add(Calendar.DAY_OF_MONTH, days);
        expiryDate = now.getTime();
        binding.etExpiryDate.setText(dateFormat.format(expiryDate));
        binding.etExpiryTime.setText(timeFormat.format(expiryDate));
        
        // Update currentCoupon if it exists
        if (currentCoupon != null) {
            currentCoupon.setEffectiveDate(effectiveDate);
            currentCoupon.setExpiryDate(expiryDate);
        }
        
        // Clear chip selection
        binding.chipGroupDatePresets.clearCheck();
    }

    // ================================
    // === Validation Methods
    // ================================

    private boolean validateForm() {
        String name = binding.etCouponName.getText().toString().trim();
        String code = binding.etCouponCode.getText().toString().trim();
        String discountStr = binding.etDiscountPercentage.getText().toString().trim();

        // Validate name
        if (TextUtils.isEmpty(name)) {
            binding.etCouponName.setError("Vui lòng nhập tên mã giảm giá");
            return false;
        }

        // Validate code
        if (TextUtils.isEmpty(code)) {
            binding.etCouponCode.setError("Vui lòng nhập mã giảm giá");
            return false;
        }

        // Validate discount
        if (TextUtils.isEmpty(discountStr)) {
            binding.etDiscountPercentage.setError("Vui lòng nhập phần trăm giảm giá");
            return false;
        }

        try {
            double discount = Double.parseDouble(discountStr);
            if (discount <= 0 || discount > 1) {
                binding.etDiscountPercentage.setError("Phần trăm giảm giá phải từ 0.01 đến 1.0");
                return false;
            }
        } catch (NumberFormatException e) {
            binding.etDiscountPercentage.setError("Phần trăm giảm giá không hợp lệ");
            return false;
        }

        // Validate dates
        if (effectiveDate == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày hiệu lực", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (expiryDate == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày hết hạn", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (expiryDate.before(effectiveDate)) {
            Toast.makeText(requireContext(), "Ngày hết hạn phải sau ngày hiệu lực", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // ================================
    // === Save Methods
    // ================================

    private void saveCoupon() {
        showLoading(true);
        
        // Create new coupon if currentCoupon is null (new coupon creation)
        if (currentCoupon == null) {
            currentCoupon = new Coupon();
            currentCoupon.setId(UUID.randomUUID().toString());
            
            // Extract form data for new coupon
            String name = binding.etCouponName.getText().toString().trim();
            String code = binding.etCouponCode.getText().toString().trim().toUpperCase();
            String discountStr = binding.etDiscountPercentage.getText().toString().trim();
            
            currentCoupon.setName(name);
            currentCoupon.setCode(code);
            currentCoupon.setDiscount(Double.parseDouble(discountStr));
            currentCoupon.setEffectiveDate(effectiveDate);
            currentCoupon.setExpiryDate(expiryDate);
        }
        
        hCouponManagementVM.saveCoupon(currentCoupon).observe(getViewLifecycleOwner(), success -> {
            showLoading(false);
            if (success) {
                String message = editingCouponId != null ? 
                    "Cập nhật mã giảm giá thành công" : 
                    "Tạo mã giảm giá thành công";
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                
                // Trigger data refresh before navigating back
                hCouponManagementVM.refreshData();
                
                NavHostFragment.findNavController(this).navigateUp();
            } else {
                Toast.makeText(requireContext(), "Có lỗi xảy ra khi lưu mã giảm giá", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            binding.progressSave.setVisibility(View.VISIBLE);
            binding.btnSave.setEnabled(false);
            binding.btnCancel.setEnabled(false);
        } else {
            binding.progressSave.setVisibility(View.INVISIBLE);
            binding.btnSave.setEnabled(true);
            binding.btnCancel.setEnabled(true);
        }
    }
}