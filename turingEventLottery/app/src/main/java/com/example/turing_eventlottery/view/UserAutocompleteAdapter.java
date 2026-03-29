package com.example.turing_eventlottery.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ArrayAdapter for User autocomplete.
 * Searches users by username via UserViewModel and displays both username and ID.
 */
public class UserAutocompleteAdapter extends ArrayAdapter<User> {
    private List<User> userList;
    private final UserViewModel userViewModel;

    public UserAutocompleteAdapter(@NonNull Context context, UserViewModel userViewModel) {
        super(context, R.layout.item_user_autocomplete);
        this.userList = new ArrayList<>();
        this.userViewModel = userViewModel;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_autocomplete, parent, false);
        }

        User user = getItem(position);
        if (user != null) {
            TextView usernameText = convertView.findViewById(R.id.autocomplete_username);
            TextView useridText = convertView.findViewById(R.id.autocomplete_userid);

            usernameText.setText(user.getUserName());
            useridText.setText(user.getUserId());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (constraint != null && constraint.length() > 0) {
                    userViewModel.searchUsersByUsername(constraint.toString(), users -> {
                        userList = users;
                        notifyDataSetChanged();
                    });
                } else {
                    userList = new ArrayList<>();
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((User) resultValue).getUserName();
            }
        };
    }
}
