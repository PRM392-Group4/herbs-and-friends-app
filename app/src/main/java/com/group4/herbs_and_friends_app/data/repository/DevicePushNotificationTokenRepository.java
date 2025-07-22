package com.group4.herbs_and_friends_app.data.repository;

import android.os.Build;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.data.model.PushNotificationToken;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DevicePushNotificationTokenRepository {
    private final String collectionName = "pushNotificationTokens";
    private final CollectionReference tokens;

    public DevicePushNotificationTokenRepository(FirebaseFirestore firestore) {
        this.tokens = firestore.collection(collectionName);
    }

    public void getDeviceTokensByUserId(String userId, TokensCallback callback) {
        ArrayList<String> userDeviceTokens = new ArrayList<>();
        tokens.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PushNotificationToken> userTokenList = querySnapshot.toObjects(PushNotificationToken.class);
                    for (var token :
                            userTokenList) {
                        userDeviceTokens.add(token.getToken());
                    }
                    callback.onTokensReceived(userDeviceTokens);
                })
                .addOnFailureListener(e -> {
                    userDeviceTokens.clear();
                    callback.onError(e);
                });
    }

    public LiveData<Boolean> addDeviceTokenForUser(String userId, String deviceToken) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        tokens.whereEqualTo("userId", userId)
                .whereEqualTo("token", deviceToken)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (var doc : querySnapshot.getDocuments()) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("lastActive", new Date());
                            tokens.document(doc.getId()).set(updates, SetOptions.merge());
                        }
                        result.setValue(true);
                    } else {
                        PushNotificationToken newToken = new PushNotificationToken(userId, deviceToken, Build.MODEL);
                        tokens.add(newToken)
                                .addOnSuccessListener(aVoid -> result.setValue(true))
                                .addOnFailureListener(e -> result.setValue(false));
                    }
                })
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    public LiveData<Boolean> removeDeviceTokenForUser(String userId, String deviceToken) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        tokens.whereEqualTo("userId", userId)
                .whereEqualTo("token", deviceToken)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (var doc : querySnapshot.getDocuments()) {
                            Map<String, Object> updates = new HashMap<>();
                            tokens.document(doc.getId()).set(updates, SetOptions.merge());
                        }
                        result.setValue(true);
                    } else {
                        result.setValue(false);
                    }
                })
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }
    public interface TokensCallback {
        void onTokensReceived(List<String> tokens);
        void onError(Exception e);
    }
}
