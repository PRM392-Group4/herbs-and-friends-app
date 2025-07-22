package com.group4.herbs_and_friends_app.data.communication;

import android.bluetooth.BluetoothClass;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.data.model.PushNotificationToken;
import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;
import com.group4.herbs_and_friends_app.data.repository.DevicePushNotificationTokenRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public class NotificationPublisher {
    public static final String TAG = "NotificationPublisher";
    private final FirebaseDatabase firebaseRealtimeInstance;
    private final DevicePushNotificationTokenRepository notificationTokenRepository;
    public NotificationPublisher(FirebaseDatabase firebaseRealtimeInstance,
                                 DevicePushNotificationTokenRepository notificationTokenRepository) {
        this.firebaseRealtimeInstance = firebaseRealtimeInstance;
        this.notificationTokenRepository = notificationTokenRepository;
    }

    public void tryPublishToAllUsers(NotificationDto notificationDto) {
        DatabaseReference presenceRef = firebaseRealtimeInstance.getReference("presence");
        presenceRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    List<String> userIds = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        userIds.add(userId);
                    }

                    if (userIds.isEmpty()) {
                        Log.i(TAG, "No users found in presence");
                        return;
                    }

                    Set<String> offlineUsersHash = new HashSet<>();
                    int[] pendingCallbacks = {0};
                    boolean[] anyOffline = {false};

                    for (String userId : userIds) {
                        String status = dataSnapshot.child(userId).getValue(String.class);
                        SaveNotification(notificationDto, userId);
                        if ("offline".equals(status)) {
                            anyOffline[0] = true;
                            pendingCallbacks[0]++;
                            notificationTokenRepository.getDeviceTokensByUserId(userId, new DevicePushNotificationTokenRepository.TokensCallback() {
                                @Override
                                public void onTokensReceived(List<String> tokens) {
                                    synchronized (offlineUsersHash) {
                                        if (tokens != null) {
                                            offlineUsersHash.addAll(tokens);
                                        }
                                        pendingCallbacks[0]--;
                                        if (pendingCallbacks[0] == 0) {
                                            Log.i(TAG, "hash list size: " + offlineUsersHash.size());
                                            if (!offlineUsersHash.isEmpty()) {
                                                SendPushNotification(notificationDto, new ArrayList<>(offlineUsersHash));
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    synchronized (offlineUsersHash) {
                                        pendingCallbacks[0]--;
                                        if (pendingCallbacks[0] == 0) {
                                            Log.i(TAG, "hash list size: " + offlineUsersHash.size());
                                            if (!offlineUsersHash.isEmpty()) {
                                                SendPushNotification(notificationDto, new ArrayList<>(offlineUsersHash));
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                    // If there are no offline users, nothing to wait for
                    if (!anyOffline[0]) {
                        Log.i(TAG, "No offline users to notify");
                    }
                }
            }
        });
    }

    public void tryPublishToOneUser(String receiverUserId, NotificationDto notificationDto) {
        DatabaseReference presenceRef = FirebaseDatabase.getInstance()
                .getReference("presence")
                .child(receiverUserId);

        presenceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String status = task.getResult().getValue(String.class);

                if (status == null) {
                    Log.i(TAG, "user does not exist");
                    return;
                }

                Log.i(TAG, "publishing message");
                SaveNotification(notificationDto, receiverUserId);
                if ("offline".equals(status)) {
                    notificationTokenRepository.getDeviceTokensByUserId(receiverUserId, new DevicePushNotificationTokenRepository.TokensCallback() {
                        @Override
                        public void onTokensReceived(List<String> tokenList) {
                            SendPushNotification(notificationDto, tokenList);
                        }
                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void SaveNotification(NotificationDto notificationDto, String receiverId) {
        DatabaseReference notifyRef = firebaseRealtimeInstance.getReference("notify").child(receiverId);
        notifyRef.push().setValue(notificationDto);
    }

    private void SendPushNotification(NotificationDto notificationDto, List<String> tokenList) {
        Log.i(TAG, "Sending push notifications");
        var fcmClient = new FCMClient();
        fcmClient.sendPushNotification(
                notificationDto.getTitle(),
                notificationDto.getMessage(),
                tokenList,
                null);
    }
}
