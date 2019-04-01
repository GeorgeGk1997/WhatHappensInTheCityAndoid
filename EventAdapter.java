package com.example.iqmma.whathappensinthecity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private ArrayList<EventItem> eventList;
    private OnItemClickListener listener;
    public Random random;

    private FirebaseUser userF;
    private DatabaseReference reference;


    public EventAdapter(Context context, ArrayList<EventItem> eventList){
        this.context = context;
        this.eventList = eventList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onGoingClick(int position);

        void onItemClick(int position, ImageView eventImage);

        //void onLoadRecycler(int position, TextView goingTxt);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_event_items, viewGroup, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder eventViewHolder, final int i) {

        random = new Random();
        EventItem currentItem = eventList.get(i);
        String eventDisplayName = currentItem.getEventDisplayName();
        final String eventDate = currentItem.getEventDate();
        String eventType = currentItem.getEventType();
        String eventLocation = currentItem.getEventLocation();

        eventViewHolder.displayName.setText(eventDisplayName);
        eventViewHolder.date.setText(eventDate);
        eventViewHolder.location.setText(eventLocation);
        eventViewHolder.type.setText(eventType);


        int randomNum = random.nextInt(7);
        switch (randomNum) {
            case 0:
                eventViewHolder.imageView.setImageResource(R.drawable.a);
                break;
            case 1:
                eventViewHolder.imageView.setImageResource(R.drawable.b);
                break;
            case 2:
                eventViewHolder.imageView.setImageResource(R.drawable.c);
                break;
            case 3:
                eventViewHolder.imageView.setImageResource(R.drawable.d );
                break;
            case 4:
                eventViewHolder.imageView.setImageResource(R.drawable.e);
                break;
            case 5:
                eventViewHolder.imageView.setImageResource(R.drawable.f);
                break;
            case 6:
                eventViewHolder.imageView.setImageResource(R.drawable.g);
                break;
        }

        userF = FirebaseAuth.getInstance().getCurrentUser();
        try {
            reference = FirebaseDatabase.getInstance().getReference("Events").child("Going").child(userF.getUid() +
                    eventList.get(i).getEventId());


            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EventFirebase eventFirebase = dataSnapshot.getValue(EventFirebase.class);
                    if (eventFirebase != null ) {
                        eventViewHolder.goingState.setBackgroundResource(R.drawable.capsule_going);
                        eventViewHolder.goingState.setTextColor(Color.WHITE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {}

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    public class EventViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView displayName;
        public TextView date;
        public TextView location;
        public TextView type;
        public TextView goingState;


        public EventViewHolder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.eventProf);
            displayName = itemView.findViewById(R.id.eventDisplayName);
            date = itemView.findViewById(R.id.eventDateTime);
            location = itemView.findViewById(R.id.eventLocation);
            type = itemView.findViewById(R.id.eventType);
            goingState = itemView.findViewById(R.id.eventGoingState);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null ){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position, imageView);
                        }
                    }
                }
            });

            goingState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION ) {
                            listener.onGoingClick(position);
                            goingState.setBackgroundResource(R.drawable.capsule_going);
                            goingState.setTextColor(Color.WHITE);
                        }
                    }
                }
            });





        }
    }
}
