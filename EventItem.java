package com.example.iqmma.whathappensinthecity;

public class EventItem {
    private String eventId;
    private String eventDisplayName;
    private String eventType;
    private String eventPopularity;
    private String eventAgeRestriction;
    private String eventTime;
    private String eventDate;
    private String eventLocation;
    private String eventUrl;

    private String venueName;
    private String venueLat;
    private String venueLon;

    public EventItem(String eventId, String eventDisplayName, String eventType, String eventPopularity, String eventAgeRestriction, String eventTime, String eventDate, String city, String eventUrl, String venueName, String venueLat, String venueLon) {


        this.eventId = eventId;
        this.eventDisplayName = eventDisplayName;
        this.eventType = eventType;
        this.eventPopularity = eventPopularity;
        this.eventAgeRestriction = eventAgeRestriction;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
        this.eventLocation = city;
        this.eventUrl = eventUrl;
        this.venueName = venueName;
        this.venueLat = venueLat;
        this.venueLon = venueLon;

    }

    public void EventItem(){}


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventDisplayName() {
        return eventDisplayName;
    }

    public void setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventPopularity() {
        return eventPopularity;
    }

    public void setEventPopularity(String eventPopularity) {
        this.eventPopularity = eventPopularity;
    }

    public String getEventAgeRestriction() {
        return eventAgeRestriction;
    }

    public void setEventAgeRestriction(String eventAgeRestriction) {
        this.eventAgeRestriction = eventAgeRestriction;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueLat() {
        return venueLat;
    }

    public void setVenueLat(String venueLat) {
        this.venueLat = venueLat;
    }

    public String getVenueLon() {
        return venueLon;
    }

    public void setVenueLon(String venueLon) {
        this.venueLon = venueLon;
    }
}
