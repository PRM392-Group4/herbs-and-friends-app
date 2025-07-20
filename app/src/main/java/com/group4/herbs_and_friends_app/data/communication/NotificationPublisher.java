package com.group4.herbs_and_friends_app.data.communication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

public class NotificationPublisher {
    public static final String TAG = "NotificationConsumer";
    private DatabaseReference databaseReference;
    private final FirebaseDatabase firebaseRealtimeInstance;
    public NotificationPublisher(FirebaseDatabase firebaseRealtimeInstance, FirebaseAuth firebaseAuthInstance) {
        this.firebaseRealtimeInstance = firebaseRealtimeInstance;
        // for testing only
        firebaseAuthInstance.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.i("firebase", "AuthState changed to null");
                    databaseReference = null;
                } else{
                    Log.i("firebase", "AuthState changed to " + user.getUid());
                    databaseReference = firebaseRealtimeInstance.getReference("notify").child(user.getUid());
                    //for testing only
                    databaseReference.push().setValue(new NotificationDto("title", "body", NotificationTypes.ORDER_STATUS_UPDATED, new Date()));
                }
            }
        });
    }

    public void publishNotification(String receiverUserId, NotificationDto notificationDto) {
        DatabaseReference presenceRef = FirebaseDatabase.getInstance()
                .getReference("presence")
                .child(receiverUserId);

        presenceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String status = task.getResult().getValue(String.class);

                Log.i(TAG, "publishing message");
                var databaseReference = firebaseRealtimeInstance
                        .getReference("notify")
                        .child(receiverUserId);
                databaseReference.push().setValue(notificationDto);

                if (!"online".equals(status)) {
                    // maybe add fcm here
                }
            }
        });
    }
}
