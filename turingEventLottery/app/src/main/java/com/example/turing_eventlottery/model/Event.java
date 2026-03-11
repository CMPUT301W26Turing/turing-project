package com.example.turing_eventlottery.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private String id;
    private String organizerId;
    private String name;
    private String category;
    private String location;
    private String date;
    private String posterUrl;
    private String description;
    private String regStart;
    private String regEnd;
    private int winnersToDraw;
    private int waitlistCap;
    private boolean geolocationRequired;
    private ArrayList<String> waitlist;
    private ArrayList<String> participants;
    private Map<String, String> invitations; // userId -> status

    public Event() {}

    public Event(String id, String organizerId, String name, String category,
                 String location, String date, String time, String posterUrl,
                 String description, String regStart, String regEnd,
                 int winnersToDraw, int waitlistCap, boolean geolocationRequired,
                 ArrayList<String> waitlist, ArrayList<String> participants) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.category = category;
        this.location = location;
        this.date = date;
        this.posterUrl = posterUrl;
        this.description = description;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.winnersToDraw = winnersToDraw;
        this.waitlistCap = waitlistCap;
        this.geolocationRequired = geolocationRequired;
        this.waitlist = waitlist;
        this.participants = participants;
        this.invitations = new HashMap<>();
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

    public ArrayList<String> getWaitlist() { return waitlist; }
    public void setWaitlist(ArrayList<String> waitlist) { this.waitlist = waitlist; }
    public void addToWaitlist(String userId) { waitlist.add(userId); }
    public void removeFromWaitlist(String userId) { waitlist.remove(userId); }

    public ArrayList<String> getParticipants() { return participants; }
    public void setParticipants(ArrayList<String> participants) { this.participants = participants; }
    public void addToParticipants(String userId) { participants.add(userId); }
    public void removeFromParticipants(String userId) { participants.remove(userId); }

    public Map<String, String> getInvitations() {
        return invitations;
    }

    public void setInvitations(Map<String, String> invitations) {
        this.invitations = invitations;
    }

    // Helper method to update invitation status
    public void updateInvitationStatus(String userId, String status) {
        if (invitations == null) {
            invitations = new HashMap<>();
        }
        invitations.put(userId, status);
    }
}
