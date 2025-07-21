package com.group4.herbs_and_friends_app.ui.admin_side.coupon_management;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.FragmentHCouponManagementBinding;
import com.group4.herbs_and_friends_app.ui.admin_side.coupon_management.adapters.HCouponManagementAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HCouponManagementFragment extends Fragment implements HCouponManagementAdapter.ICouponActionListener {

    // ================================
    // === Fields
    // ================================

    private FragmentHCouponManagementBinding binding;
    private HCouponManagementAdapter adapter;
    private HCouponManagementVM hCouponManagementVM;
    private Date startDate = null;
    private Date endDate = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // ================================
    // === Lifecycle
    // ================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHCouponManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hCouponManagementVM = new ViewModelProvider(requireActivity()).get(HCouponManagementVM.class);

        setupRecyclerView();
        setupSearchFunctionality();
        setupDateFiltering();
        setupFilterButtons();
        setupFloatingActionButton();
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        hCouponManagementVM.refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ================================
    // === Setup Methods
    // ================================

    private void setupRecyclerView() {
        adapter = new HCouponManagementAdapter(new ArrayList<>(), this);
        binding.rvCoupons.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCoupons.setAdapter(adapter);
    }

    private void setupSearchFunctionality() {
        // Setup search text watcher
        binding.includeActionbarCouponManage.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update end icon based on text content
                if (s != null && !s.toString().trim().isEmpty()) {
                    binding.includeActionbarCouponManage.tilSearch.setEndIconDrawable(R.drawable.ic_cancel);
                } else {
                    binding.includeActionbarCouponManage.tilSearch.setEndIconDrawable(R.drawable.ic_search);
                }
            }
        });

        // Setup search action listener
        binding.includeActionbarCouponManage.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        // Setup end icon click listener
        binding.includeActionbarCouponManage.tilSearch.setEndIconOnClickListener(v -> {
            Editable editable = binding.includeActionbarCouponManage.etSearch.getText();
            if (editable != null && !editable.toString().trim().isEmpty()) {
                // Clear search
                binding.includeActionbarCouponManage.etSearch.setText("");
                hCouponManagementVM.setSearch(null);
            } else {
                performSearch();
            }
            hideKeyboard(binding.includeActionbarCouponManage.etSearch);
        });
    }

    private void setupDateFiltering() {
        // Setup start date picker
        binding.etStartDate.setOnClickListener(v -> showDatePicker(true));
        
        // Setup end date picker
        binding.etEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupFilterButtons() {
        // Apply date filter button
        binding.btnApplyDateFilter.setOnClickListener(v -> applyDateFilter());
        
        // Clear filter button
        binding.btnClearDateFilter.setOnClickListener(v -> clearDateFilter());
    }

    private void setupFloatingActionButton() {
        binding.btnAddCoupon.setOnClickListener(v -> {
            NavHostFragment.findNavController(HCouponManagementFragment.this).navigate(
                    HCouponManagementFragmentDirections.fromCouponListToCouponForm(null)
            );
        });
    }

    private void observeViewModel() {
        hCouponManagementVM.getCouponListLive().observe(getViewLifecycleOwner(), coupons -> {
            if (coupons != null) {
                adapter.setCouponList(coupons);
                updateEmptyState(coupons.isEmpty());
            }
        });
    }

    // ================================
    // === Search Methods
    // ================================

    private void performSearch() {
        Editable editable = binding.includeActionbarCouponManage.etSearch.getText();
        if (editable == null) return;

        String search = editable.toString().trim();
        hCouponManagementVM.setSearch(search.isEmpty() ? null : search);
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    // ================================
    // === Date Filtering Methods
    // ================================

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    
                    if (isStartDate) {
                        startDate = selectedDate;
                        binding.etStartDate.setText(dateFormat.format(selectedDate));
                    } else {
                        endDate = selectedDate;
                        binding.etEndDate.setText(dateFormat.format(selectedDate));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void applyDateFilter() {
        hCouponManagementVM.setDateRange(startDate, endDate);
    }

    private void clearDateFilter() {
        startDate = null;
        endDate = null;
        binding.etStartDate.setText("");
        binding.etEndDate.setText("");
        hCouponManagementVM.clearFilters();
    }

    // ================================
    // === UI Update Methods
    // ================================

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.rvCoupons.setVisibility(View.GONE);
        } else {
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.rvCoupons.setVisibility(View.VISIBLE);
        }
    }

    // ================================
    // === CouponActionListener Implementation
    // ================================

    @Override
    public void onCouponEditClick(String couponId) {
        NavHostFragment.findNavController(HCouponManagementFragment.this).navigate(
                HCouponManagementFragmentDirections.fromCouponListToCouponForm(couponId)
        );
    }

    @Override
    public void onCouponDeleteClick(String couponId, String couponName) {
        // Show confirmation dialog and delete coupon
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa mã giảm giá")
                .setMessage("Bạn có chắc muốn xóa '" + couponName + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    hCouponManagementVM.deleteCoupon(couponId).observe(getViewLifecycleOwner(), success -> {
                        if (Boolean.TRUE.equals(success)) {
                            Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                            // Refresh the list
                            hCouponManagementVM.clearFilters();
                        } else {
                            Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}