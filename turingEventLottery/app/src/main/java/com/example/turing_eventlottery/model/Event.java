package com.example.turing_eventlottery.model;

import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.google.firebase.firestore.Exclude;

/**
 * Represents an event in the system.
 *  <p>
 *      An event contains all the information needed for display and registration.
 *      Registration times are stored as a String in the format "MM/dd/yyyy, HH:mm"
 *  </p>
 *
 * @author Matthew Adams
 * @author Miro
 * @version 1.1
 * @since 03-07-2026
 * @see EventViewModel
 */
public class Event {
    private String id;
    private String organizerId;
    private String coOrganizerId;
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

    /**
     * A default constructor required for Firebase Firestore.
     * Firestone uses this constructor when reconstruction Event objects
     * from the database.
     */
    public Event() {}

    /**
     * Constructs an Event with all fields.
     *
     * @param id unique event ID
     * @param organizerId ID of the organizer
     * @param coOrganizerId ID of the co-organizer (optional)
     * @param name event name
     * @param category event category
     * @param location event location
     * @param date event date
     * @param posterUrl events poster image
     * @param description event description
     * @param regStart event registration start
     * @param regEnd event registration end
     * @param winnersToDraw event winners
     * @param waitlistCap event waitlist limit
     * @param geolocationRequired event region where to accept entrants
     */
    public Event(String id, String organizerId, String coOrganizerId, String name, String category,
                 String location, String date, String posterUrl,
                 String description, String regStart, String regEnd,
                 int winnersToDraw, int waitlistCap, boolean geolocationRequired) {
        this.id = id;
        this.organizerId = organizerId;
        this.coOrganizerId = coOrganizerId;
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
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getCoOrganizerId() { return coOrganizerId; }
    public void setCoOrganizerId(String coOrganizerId) { this.coOrganizerId = coOrganizerId; }

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
}
