package com.example.firstapp.ui.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.model.Event;
import com.squareup.picasso.Picasso;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private Event[] events;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.upcoming_game_home_team);
            this.imageView = (ImageView) itemView.findViewById(R.id.upcoming_game_home_image);
        }
    }

    public EventsAdapter(Event[] events) {
        this.events = events;
    }

    public EventsAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_view, parent, false);

        EventViewHolder vh = new EventViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.textView.setText(events[position].getName());
        Picasso.get().load(events[position].getEventImg()).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return events.length;
    }


}
