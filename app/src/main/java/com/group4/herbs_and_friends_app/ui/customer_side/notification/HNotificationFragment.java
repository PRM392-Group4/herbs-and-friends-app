package com.group4.herbs_and_friends_app.ui.customer_side.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.ui.notification.adapter.HNotificationAdapter;
import java.util.ArrayList;
import java.util.List;

import com.group4.herbs_and_friends_app.databinding.FragmentHNotificationBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HNotificationFragment extends Fragment {

    private FragmentHNotificationBinding binding;
    private HNotificationVM hNotificationVM;
    private HNotificationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new HNotificationAdapter(new ArrayList<>());
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifications.setAdapter(adapter);
        hNotificationVM = new ViewModelProvider(this).get(HNotificationVM.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupInit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Centralized setup method, called in onResume
     */
    private void setupInit() {
        setupObserverNotifications();
        // Add more setup methods here if needed (e.g., action bar)
    }

    /**
     * Observe notifications LiveData
     */
    private void setupObserverNotifications() {
        hNotificationVM.getNotificationsLiveData().observe(getViewLifecycleOwner(), notifications -> {
            adapter.setNotifications(notifications != null ? notifications : new ArrayList<>());
        });
    }
}
