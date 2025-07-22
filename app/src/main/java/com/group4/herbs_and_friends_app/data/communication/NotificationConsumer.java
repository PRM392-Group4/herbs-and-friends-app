package com.group4.herbs_and_friends_app.data.communication;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.group4.herbs_and_friends_app.data.repository.DevicePushNotificationTokenRepository;
import com.group4.herbs_and_friends_app.di.PermissionManager;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

public class NotificationConsumer implements DefaultLifecycleObserver {
    public static final String TAG = "NotificationConsumer";

    private String storedUserId;
    private FirebaseDatabase firebaseRealtimeInstance;
    private DatabaseReference notifyDbRef;
    private DatabaseReference presenceDbRef;
    private final ValueEventListener onNewValueAddedCallback;
    private MutableLiveData<List<NotificationDto>> notificationsLiveData = new MutableLiveData<>(new ArrayList<>());

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onStart");
        if (presenceDbRef != null) {
            presenceDbRef.setValue("online");
        }
        DefaultLifecycleObserver.super.onStart(owner);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onStop");
        if (presenceDbRef != null) {
            presenceDbRef.setValue("offline");
        }
        DefaultLifecycleObserver.super.onStop(owner);
    }

    public NotificationConsumer(FirebaseDatabase firebaseRealtimeInstance,
                                FirebaseAuth firebaseAuthInstance,
                                DevicePushNotificationTokenRepository notificationTokenRepository,
                                FirebaseMessaging firebaseMessaging,
                                PermissionManager permissionManager) {
        this.firebaseRealtimeInstance = firebaseRealtimeInstance;
        onNewValueAddedCallback = initializeOnDataChangeCallback();

        firebaseAuthInstance.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.i(TAG, "AuthState changed to null");
                    stopListenToDataChanges();
                    notificationsLiveData.postValue(new ArrayList<>());
                    if (presenceDbRef != null) {
                        presenceDbRef.setValue("offline");
                    }
                } else {
                    Log.i(TAG, "AuthState changed to logged in");
                    storedUserId = user.getUid();
                    presenceDbRef = firebaseRealtimeInstance
                            .getReference("presence")
                            .child(storedUserId);
                    presenceDbRef.setValue("online");
                    presenceDbRef.onDisconnect().setValue("offline"); // handle when user lost wifi connection
                    startListenToDataChanges();
                    notificationsLiveData.postValue(fetchNotifications());

                    if (permissionManager.isPushNotificationAllowed()) {
                        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                var deviceToken = task.getResult();
                                Log.i(TAG, deviceToken);
                                notificationTokenRepository.addDeviceTokenForUser(storedUserId, deviceToken);
                            }
                        });
                    }
                }
            }
        });
    }
    public void startListenToDataChanges() {
        notifyDbRef = firebaseRealtimeInstance
                .getReference("notify")
                .child(storedUserId);

        Log.i(TAG, "adding listener for notifications");
        notifyDbRef.addValueEventListener(onNewValueAddedCallback);
    }

    public void stopListenToDataChanges() {
        if (notifyDbRef != null && onNewValueAddedCallback != null) {
            Log.i(TAG, "removing listener for private notifications");
            notifyDbRef.removeEventListener(onNewValueAddedCallback);
        }
    }

    private List<NotificationDto> fetchNotifications() {
        List<NotificationDto> list = new ArrayList<>();
        notifyDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        NotificationDto obj = childSnapshot.getValue(NotificationDto.class);
                        if (obj != null) {
                            list.add(obj);
                        }
                    }
                } else {
                    Exception e = task.getException();
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        return list;
    }

    private ValueEventListener initializeOnDataChangeCallback() {
         return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange");
                List<NotificationDto> notifications = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    NotificationDto dto = child.getValue(NotificationDto.class);
                    if (dto != null) {
                        notifications.add(dto);
                    }
                }
                notificationsLiveData.postValue(notifications);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
    }

    public LiveData<List<NotificationDto>> getNotificationsLiveData() {
        return notificationsLiveData;
    }
}