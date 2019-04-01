package com.example.iqmma.whathappensinthecity;

public class EventFirebase {

    private String eventId;
    private String gone;
    private String userId;

    public EventFirebase(String eventId, String gone, String userId){
        this.eventId = eventId;
        this.gone = gone;
        this.userId = userId;
    }

    public EventFirebase(){}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getGone() {
        return gone;
    }

    public void setGone(String gone) {
        this.gone = gone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
