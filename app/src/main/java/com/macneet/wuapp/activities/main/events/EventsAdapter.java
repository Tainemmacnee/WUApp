package com.macneet.wuapp.activities.main.events;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.macneet.wuapp.activities.displaystandings.DisplayEventStandingsActivity;
import com.macneet.wuapp.activities.displayteams.DisplayEventTeamsActivity;
import com.macneet.wuapp.activities.main.MainActivity;
import com.macneet.wuapp.R;
import com.macneet.wuapp.model.Event;
import com.macneet.wuapp.model.Team;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private Event[] events;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public Button eventTeamsButton;
        public Button eventStandingsButton;
        private final Context context;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.textView = (TextView) itemView.findViewById(R.id.event_name);
            this.imageView = (ImageView) itemView.findViewById(R.id.event_team_image);
            this.eventTeamsButton = (Button) itemView.findViewById(R.id.event_teams_button);
            this.eventStandingsButton = (Button) itemView.findViewById(R.id.event_standings_button);
        }
    }

    public EventsAdapter(List<Event> events) {
        Event[] eventsArray = events.toArray(new Event[events.size()]);
        this.events = eventsArray;
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

        holder.eventTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Team> teams = new ArrayList<>();
                teams.addAll(events[position].getTeams());
                Intent intent = new Intent(view.getContext(), DisplayEventTeamsActivity.class);
                intent.putExtra(MainActivity.MESSAGE_EVENTTEAMS, teams);
                view.getContext().startActivity(intent);

            }
        });
        holder.eventStandingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DisplayEventStandingsActivity.class);
                intent.putExtra(DisplayEventStandingsActivity.MESSAGESCORESLINK, events[position].getEventLink()+"/standings");
                view.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return events.length;
    }


}
