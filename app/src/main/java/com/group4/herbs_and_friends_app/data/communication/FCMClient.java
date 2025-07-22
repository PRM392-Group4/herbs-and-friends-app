package com.group4.herbs_and_friends_app.data.communication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FCMClient {
    private static final String ENDPOINT = "https://sendpushnotification-fpppwvlg5a-uc.a.run.app/sendPushNotification";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void sendPushNotification(String title, String body, List<String> registrationTokens, @Nullable FCMCallback callback) {
        executor.execute(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("title", title);
                json.put("body", body);
                JSONArray tokens = new JSONArray();
                for (String token : registrationTokens) {
                    tokens.put(token);
                }
                json.put("registrationTokens", tokens);
                RequestBody requestBody = RequestBody.create(json.toString(), JSON);
                Request request = new Request.Builder()
                        .url(ENDPOINT)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (callback != null) callback.onFailure(e);
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (callback != null) callback.onSuccess(response.body() != null ? response.body().string() : null);
                        response.close();
                    }
                });
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e);
            }
        });
    }

    public interface FCMCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }
}
