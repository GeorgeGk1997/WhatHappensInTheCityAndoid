package com.example.iqmma.whathappensinthecity;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<EventItem> eventList;
    private RequestQueue requestQueue, requestQueue1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        recyclerView = findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        requestQueue1 = Volley.newRequestQueue(this);
        parseJSON();
    }


    String urlEvents;
    Boolean locationFound;
    String metroAreaId;
    private void parseJSON(){
        String urlGetUserLocation = "https://api.songkick.com/api/3.0/search/locations.json?location=clientip&apikey=6Tz1tEjhyYjb1dRa";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlGetUserLocation, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    metroAreaId = response.getJSONObject("resultsPage")
                                                .getJSONObject("results")
                                                .getJSONArray("location")
                                                .getJSONObject(0)
                                                .getJSONObject("metroArea")
                                                .getString("id");


                    urlEvents = "https://api.songkick.com/api/3.0/metro_areas/" +
                                        metroAreaId + "/calendar.json?apikey=6Tz1tEjhyYjb1dRa";

                    eventResponse(urlEvents);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);

    }


    public void eventResponse(String url){
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                //Parsing a Json Object response
                try{
                    JSONArray jsonEventArray = response.getJSONObject("resultsPage")
                            .getJSONObject("results")
                            .getJSONArray("event");
                    for (int pointer = 0; pointer < jsonEventArray.length(); pointer++){
                        JSONObject object = jsonEventArray.getJSONObject(pointer);

                        //Get the important information from events
                        String eventId = object.getString("id");
                        String eventDisplayName = object.getString("displayName");
                        String eventType = object.getString("type");
                        String eventPopularity = object.getString("popularity");
                        String eventAgeRestriction = object.getString("ageRestriction");
                        String eventUrl = object.getString("uri");

                        //Get the event datetime
                        JSONObject objectDateTime = object.getJSONObject("start");
                        String eventDate = objectDateTime.getString("date");
                        String eventTime = objectDateTime.getString("time");

                        //Get the location info
                        //Location of the happening event place
                        JSONObject objectVenue = object.getJSONObject("venue");
                        String venueName = objectVenue.getString("displayName");
                        String venueLat = objectVenue.getString("lat");
                        String venueLon = objectVenue.getString("lng");

                        JSONObject objectLocation = object.getJSONObject("location");
                        String city = objectLocation.getString("city");

                        //Add all of them in an array list of EventItems
                        eventList.add(new EventItem(eventId, eventDisplayName, eventType, eventPopularity,
                                eventAgeRestriction, eventTime, eventDate, city,
                                eventUrl, venueName, venueLat, venueLon));
                    }
                    eventAdapter = new EventAdapter(EventsActivity.this, eventList);
                    recyclerView.setAdapter(eventAdapter);

                    //Performs Click Event when The user hit Going button
                    //Implements an interface
                    eventAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {

                        private DatabaseReference databaseReference;
                        private FirebaseAuth firebaseAuth;
                        @Override
                        public void onGoingClick(int position) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Events").child("Going").child(firebaseAuth.getInstance().getCurrentUser().getUid()
                                                                                                                                + eventList.get(position).getEventId());
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("eventId", eventList.get(position).getEventId());
                            hashMap.put("gone", "false");
                            hashMap.put("userId", firebaseAuth.getInstance().getCurrentUser().getUid());


                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful())
                                        Toast.makeText(EventsActivity.this, "Problem Occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onItemClick(int position, ImageView eventImage) {
                            goToDetailActivity(position, eventImage);
                        }


                    });

                }
                catch (JSONException e)
                {e.printStackTrace();}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(EventsActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue1.add(request2); // add the request to the volley queue

    }

    public void goToDetailActivity(int position, ImageView imgEvent){
        Intent detailIntent = new Intent(this, EventDetailsActivity.class);

        //Because some time the time and age restriction is null we have to deal with it
        String time = eventList.get(position).getEventTime();
        String age = eventList.get(position).getEventAgeRestriction();
        if (time.equals("null"))
            time = "All Day";
        if(age.equals("null"))
            age = "For all ages";

        imgEvent.buildDrawingCache();
        Bitmap bitmap = imgEvent.getDrawingCache();

        Bundle extrasBundle = new Bundle();
        extrasBundle.putParcelable("ImageEvent",bitmap);

        extrasBundle.putString("EventName", eventList.get(position).getEventDisplayName());
        extrasBundle.putString("Location", eventList.get(position).getEventLocation());
        extrasBundle.putString("DateTime", eventList.get(position).getEventDate() + " "+
                                                     time);

        extrasBundle.putString("Venue", eventList.get(position).getVenueName());
        extrasBundle.putString("Type", eventList.get(position).getEventType());
        extrasBundle.putString("Age", age);
        extrasBundle.putString("Url", eventList.get(position).getEventUrl());

        detailIntent.putExtras(extrasBundle);
        startActivity(detailIntent);
    }


}
