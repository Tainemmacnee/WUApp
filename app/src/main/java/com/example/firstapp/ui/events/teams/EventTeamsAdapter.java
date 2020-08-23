package com.example.firstapp.ui.events.teams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.model.Event;
import com.example.firstapp.model.Team;
import com.squareup.picasso.Picasso;

public class EventTeamsAdapter extends RecyclerView.Adapter<EventTeamsAdapter.EventTeamViewHolder> {

    private Team[] teams;

    public class EventTeamViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout femaleMatchupDisplay;
        public LinearLayout maleMatchupDisplay;
        public TextView textView;
        public ImageView imageView;
        public LinearLayout maleMatchupContainer;
        public LinearLayout femaleMatchupContainer;
        public TextView maleMatchupTextView;
        public TextView femaleMatchupTextView;

        public EventTeamViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.event_team_name);
            this.imageView = itemView.findViewById(R.id.event_team_image);
            this.femaleMatchupContainer = itemView.findViewById(R.id.female_matchup_container);
            this.femaleMatchupDisplay = itemView.findViewById(R.id.female_matchup_display);
            this.maleMatchupDisplay = itemView.findViewById(R.id.male_matchup_display);
            this.maleMatchupContainer = itemView.findViewById(R.id.male_matchup_container);
            this.maleMatchupTextView = itemView.findViewById(R.id.male_matchup_textview);
            this.femaleMatchupTextView = itemView.findViewById(R.id.female_matchup_textview);

        }
    }

    public EventTeamsAdapter(Team[] teams) {
        this.teams = teams;
    }

    public EventTeamViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_team_view, parent, false);

        EventTeamViewHolder vh = new EventTeamViewHolder(v);
        return vh;
    }

    //Team romeo, doesnt hide men, doesnt have men?

    @Override
    public void onBindViewHolder(@NonNull EventTeamViewHolder holder, int position) {
        holder.textView.setText(teams[position].getName());
        Picasso.get().load(teams[position].getImageUrl()).into(holder.imageView);
        if(teams[position].getMaleMatchups().size() == 0){
            holder.maleMatchupContainer.setVisibility(View.GONE);
            System.out.println("Team "+teams[position].getName()+" Hides Men "+teams[position].getMaleMatchups());
        } else {
            holder.maleMatchupContainer.setVisibility(View.VISIBLE);
            holder.maleMatchupDisplay.removeAllViews();
            for(String name : teams[position].getMaleMatchups()){
                TextView example = holder.maleMatchupTextView;
                TextView newtextview = new TextView(holder.maleMatchupDisplay.getContext());
                newtextview.setText(name);
                newtextview.setLayoutParams(example.getLayoutParams());
                newtextview.setTextColor(example.getTextColors());
                newtextview.setVisibility(View.VISIBLE);
                holder.maleMatchupDisplay.addView(newtextview);
            }
        }
        if(teams[position].getFemaleMatchups().size() == 0){
            holder.femaleMatchupContainer.setVisibility(View.GONE);
            System.out.println("Team "+teams[position].getName()+" Hides Women "+teams[position].getFemaleMatchups());
        } else {
            holder.femaleMatchupContainer.setVisibility(View.VISIBLE);
            holder.femaleMatchupDisplay.removeAllViews();
            for(String name : teams[position].getFemaleMatchups()){
                System.out.println("ADDING DISPLAY NAME: "+name);
                TextView example = holder.femaleMatchupTextView;
                TextView newtextview = new TextView(holder.femaleMatchupDisplay.getContext());
                newtextview.setText(name);
                newtextview.setLayoutParams(example.getLayoutParams());
                newtextview.setTextColor(example.getTextColors());
                holder.femaleMatchupDisplay.addView(newtextview);

            }
        }
    }


    @Override
    public int getItemCount() {
        return teams.length;
    }


}
