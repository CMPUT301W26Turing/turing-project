package com.example.turing_eventlottery.model;

public interface UserCallback {
    void onSuccess(User user);
    void onFailure(Exception e);
}
