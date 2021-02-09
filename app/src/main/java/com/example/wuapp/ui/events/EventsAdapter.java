package com.example.wuapp.ui.events;

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

import com.example.wuapp.DisplayEventScoresActivity;
import com.example.wuapp.DisplayEventTeamsActivity;
import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
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
                ArrayList<Team> teams = (ArrayList<Team>) events[position].getTeams();
                Intent intent = new Intent(view.getContext(), DisplayEventTeamsActivity.class);
                intent.putExtra(DisplayUserActivity.MESSAGEEVENTNAME, events[position].getName());
                intent.putExtra(DisplayUserActivity.MESSAGEEVENTTEAMS, teams);
                view.getContext().startActivity(intent);

            }
        });
        holder.eventStandingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DisplayEventScoresActivity.class);
                intent.putExtra(MainActivity.MESSAGE_COOKIES, events[position].getCookies());
                intent.putExtra(DisplayEventScoresActivity.MESSAGESCORESLINK, events[position].getStandingsLink());
                view.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return events.length;
    }


}
