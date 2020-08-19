package com.example.firstapp.ui.events.teams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.model.Event;
import com.example.firstapp.model.Team;
import com.squareup.picasso.Picasso;

public class EventTeamsAdapter extends RecyclerView.Adapter<EventTeamsAdapter.EventTeamViewHolder> {

    private Team[] teams;

    public class EventTeamViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public EventTeamViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.event_team_name);
            this.imageView = (ImageView) itemView.findViewById(R.id.event_team_image);
        }
    }

    public EventTeamsAdapter(Team[] teams) {
        this.teams = this.teams;
    }

    public EventTeamViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_view, parent, false);

        EventTeamViewHolder vh = new EventTeamViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventTeamViewHolder holder, int position) {
        holder.textView.setText(teams[position].getName());
        Picasso.get().load(teams[position].getImageUrl()).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return teams.length;
    }


}
