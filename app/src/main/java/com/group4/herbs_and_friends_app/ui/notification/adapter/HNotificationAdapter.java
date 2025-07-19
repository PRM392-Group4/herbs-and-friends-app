package com.group4.herbs_and_friends_app.ui.notification.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;
import com.group4.herbs_and_friends_app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;

public class HNotificationAdapter extends RecyclerView.Adapter<HNotificationAdapter.NotificationViewHolder> {
    private List<NotificationDto> notifications;

    public HNotificationAdapter(List<NotificationDto> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationDto notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.type.setText(notification.getType() != null ? notification.getType().name() : "");
        // Format and display sendDate
        if (notification.getSendDate() != null) {
            long millis = notification.getSendDate().getTime();
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            holder.sendDate.setText(sdf.format(date));
        } else {
            holder.sendDate.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public void setNotifications(List<NotificationDto> notifications) {
        if (notifications != null) {
            notifications.sort((a, b) -> {
                if (a.getSendDate() == null && b.getSendDate() == null) return 0;
                if (a.getSendDate() == null) return 1;
                if (b.getSendDate() == null) return -1;
                long t1 = a.getSendDate().getTime();
                long t2 = b.getSendDate().getTime();
                return Long.compare(t2, t1); // Descending
            });
        }
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, type, sendDate;
        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvNotificationTitle);
            message = itemView.findViewById(R.id.tvNotificationMessage);
            type = itemView.findViewById(R.id.tvNotificationType);
            sendDate = itemView.findViewById(R.id.tvNotificationSendDate);
        }
    }
} 