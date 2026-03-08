package com.example.turing_eventlottery.model;

import java.util.List;

public interface EventCallback<T> {
    void onCallback(T result);
}
