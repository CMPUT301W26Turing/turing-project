package com.example.turing_eventlottery.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;

import java.util.List;

public class SelectedUsersAdapter extends RecyclerView.Adapter<SelectedUsersAdapter.ViewHolder> {
    private final List<User> selectedUsers;
    private final OnUserRemoveListener listener;

    public interface OnUserRemoveListener {
        void onUserRemoved(User user);
    }

    public SelectedUsersAdapter(List<User> selectedUsers, OnUserRemoveListener listener) {
        this.selectedUsers = selectedUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = selectedUsers.get(position);
        holder.userName.setText(user.getUserName());
        holder.userId.setText(user.getUserId());
        holder.removeButton.setOnClickListener(v -> listener.onUserRemoved(user));
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userId;
        ImageView removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userId = itemView.findViewById(R.id.userId);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
