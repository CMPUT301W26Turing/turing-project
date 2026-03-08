package com.example.turing_eventlottery.model;

public class Event {
    private String id;
    private String eventName;
    private String category;
    private String location;
    private String date;
    private String time;
    private String posterUrl;
    private String description;

    public Event() {}
    public Event(String id, String eventName, String category, String location, String date, String time, String posterUrl, String description) {
        this.id = id;
        this.eventName = eventName;
        this.category = category;
        this.location = location;
        this.date = date;
        this.time = time;
        this.posterUrl = posterUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }
    public String getEventName() {
        return eventName;
    }
    public String getCategory() {
        return category;
    }
    public String getLocation() {
        return location;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getDescription() {
        return description;
    }
}
