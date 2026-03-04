package com.example.turing_eventlottery;

import java.util.Map;
import java.util.HashMap;

public class UserDB {
    private static final String TAG = "UserDB";
    private final FirebaseFirestone db;

    public UserDB() {
        this.db = FirebaseFireStone.getInstance();
    }

    public boolean addUser(User user) {
        if (users.containsKey(user.getUserId())) {
            return false;
        }
        users.put(user.getUserId(), user);
        return true;
    }

}
