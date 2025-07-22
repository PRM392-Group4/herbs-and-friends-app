package com.group4.herbs_and_friends_app.ui.customer_side.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.group4.herbs_and_friends_app.R;

import javax.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

@AndroidEntryPoint
public class HHomeContainerFragment extends Fragment {
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationRationaleDialog();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_home_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handlePushNotificationPermission();
    }

    private void handlePushNotificationPermission() {
        SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        int permissionStatus = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS);
        boolean neverAsk = prefs.getBoolean("notif_permission_never_ask", false);
        Log.i("HHomeContainerFragment", "POST_NOTIFICATIONS permission status: " + permissionStatus + ", notif_permission_never_ask: " + neverAsk);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED && !neverAsk) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.notif_enable_title))
                    .setMessage(getString(R.string.notif_enable_message))
                    .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    })
                    .setNegativeButton(getString(R.string.deny), (dialog, which) -> {
                        showNotificationRationaleDialog();
                    })
                    .setNeutralButton(getString(R.string.dont_ask_again), (dialog, which) -> {
                        prefs.edit().putBoolean("notif_permission_never_ask", true).apply();
                    })
                    .show();
        }
    }

    private void showNotificationRationaleDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.notif_rationale_title))
                .setMessage(getString(R.string.notif_rationale_message))
                .setPositiveButton(getString(R.string.enable_notifications), (dialog, which) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}