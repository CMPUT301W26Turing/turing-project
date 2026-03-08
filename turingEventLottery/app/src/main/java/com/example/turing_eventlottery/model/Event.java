package com.example.turing_eventlottery.model;

import com.google.firebase.firestore.Exclude;

public class Event {
    private String id;
    private String organizerId;
    private String name;
    private String category;
    private String location;
    private String date;
    private String time;
    private String posterUrl;
    private String description;
    private String regStart;
    private String regEnd;
    private int winnersToDraw;
    private int waitlistCap;
    private boolean geolocationRequired;

    public Event() {}

    public Event(String id, String organizerId, String name, String category, String location, String date, String time, String posterUrl, String description) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.category = category;
        this.location = location;
        this.date = date;
        this.time = time;
        this.posterUrl = posterUrl;
        this.description = description;
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRegStart() { return regStart; }
    public void setRegStart(String regStart) { this.regStart = regStart; }

    public String getRegEnd() { return regEnd; }
    public void setRegEnd(String regEnd) { this.regEnd = regEnd; }

    public int getWinnersToDraw() { return winnersToDraw; }
    public void setWinnersToDraw(int winnersToDraw) { this.winnersToDraw = winnersToDraw; }

    public int getWaitlistCap() { return waitlistCap; }
    public void setWaitlistCap(int waitlistCap) { this.waitlistCap = waitlistCap; }

    public boolean isGeolocationRequired() { return geolocationRequired; }
    public void setGeolocationRequired(boolean geolocationRequired) { this.geolocationRequired = geolocationRequired; }
}
