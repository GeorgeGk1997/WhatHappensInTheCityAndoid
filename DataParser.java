package com.example.iqmma.whathappensinthecity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// We will create a method to give place name, lanlon
// with a hashmap
public class DataParser {

    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJSON){
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longtitude = "";
        String reference = "";
        String totalRating = "";
        String totalUserRating = "";


        try {
            if(!googlePlaceJSON.isNull("name"))
                nameOfPlace = googlePlaceJSON.getString("name");

            if(!googlePlaceJSON.isNull("vicinity"))
                vicinity = googlePlaceJSON.getString("vicinity");

            latitude = googlePlaceJSON.getJSONObject("geometry")
                    .getJSONObject("location")
                    .getString("lat");

            longtitude = googlePlaceJSON.getJSONObject("geometry")
                    .getJSONObject("location")
                    .getString("lng");

            reference = googlePlaceJSON.getString("reference");

            totalRating = googlePlaceJSON.getString("rating");

            totalUserRating = googlePlaceJSON.getString("user_ratings_total");

            googlePlaceMap.put("place_name", nameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longtitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("totalRating", totalRating);
            googlePlaceMap.put("totalUserRating",totalUserRating);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
        // this it will return only on place cause of its latlng
        // so to get all the nearby places we need a list of hash maps
        // We will make another method for this reason
    }

    private List< HashMap<String, String> > getAllNearbyPlaces(JSONArray jsonArray){
        int counter = jsonArray.length();

        List< HashMap<String, String> > nearByPlacesList = new ArrayList<>();

        HashMap <String, String> nearByPlaceMap = null;

        for ( int i = 0; i < counter; i++){
            try {
                nearByPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                nearByPlacesList.add(nearByPlaceMap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return nearByPlacesList;
    }

    //This method it will parse all the data and it will feed it to getAllNearbyPlaces
    public List<HashMap<String, String>> parseString(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }

}
