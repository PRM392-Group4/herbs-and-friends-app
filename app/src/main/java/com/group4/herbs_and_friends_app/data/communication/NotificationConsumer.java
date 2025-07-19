package com.group4.herbs_and_friends_app.data.communication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

public class NotificationConsumer {
    public static final String TAG = "NotificationConsumer";

    private String storedUserId;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseRealtimeInstance;

    private final ValueEventListener valueEventListenerCallback;
    private final MutableLiveData<List<NotificationDto>> notificationsLiveData = new MutableLiveData<>(new ArrayList<>());

    public NotificationConsumer(FirebaseDatabase firebaseRealtimeInstance, FirebaseAuth firebaseAuthInstance) {
        this.firebaseRealtimeInstance = firebaseRealtimeInstance;
        if (firebaseAuthInstance.getUid() != null) {
            Log.i(TAG, "haha" + firebaseAuthInstance.getUid().toString());
        }
        valueEventListenerCallback = initializeOnDataChangeCallback();
        //For testing only
//        var databaseReference = firebaseRealtimeInstance.getReference("notify/123");
//        databaseReference.addValueEventListener(valueEventListenerCallback);


        firebaseAuthInstance.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.i("firebase", "AuthState changed to null");
                    stopListenToDataChanges();
                } else {
                    storedUserId = user.getUid();
                    Log.i("firebase", "AuthState changed to ");
                    startListenToDataChanges(storedUserId);

                    //disconnection already handled in method
                    setUserOnlineStatus(storedUserId);
                }

            }
        });
    }
    public void startListenToDataChanges(String uid) {
        if (databaseReference == null) {
            var databaseReference = firebaseRealtimeInstance
                    .getReference("notify")
                    .child(uid);
            Log.i(TAG, "adding listener");
            databaseReference.addValueEventListener(valueEventListenerCallback);
        }
    }

    public void stopListenToDataChanges() {
        if (databaseReference != null && valueEventListenerCallback != null) {
            databaseReference.removeEventListener(valueEventListenerCallback);
        }
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
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
    }

    private void setUserOnlineStatus(String currentUserId) {
        Log.i(TAG, "setUserOnlineStatus");
        DatabaseReference presenceRef = firebaseRealtimeInstance
                .getReference("presence")
                .child(currentUserId);

        DatabaseReference connectedRef = firebaseRealtimeInstance
                .getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    presenceRef.setValue("online");

                    presenceRef.onDisconnect().setValue("offline");
                } else {
                    Log.i(TAG, "not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    public LiveData<List<NotificationDto>> getNotificationsLiveData() {
        return notificationsLiveData;
    }
}