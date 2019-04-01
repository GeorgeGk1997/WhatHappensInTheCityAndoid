package com.example.iqmma.whathappensinthecity;


public class RatingFirebase {
    private String datetime;
    private String ratingValue;

    public RatingFirebase(String datetime, String ratingValue){
        this.datetime = datetime;
        this.ratingValue = ratingValue;
    }

    public RatingFirebase(){}


    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }
}
