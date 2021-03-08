package com.example.wuapp.ui.events.teams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.R;
import com.example.wuapp.model.Team;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventTeamsAdapter extends RecyclerView.Adapter<EventTeamsAdapter.EventTeamViewHolder> implements Filterable {

    private Team[] teams;
    private Team[] teamsFiltered;
    private boolean showSeperators = true;

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
        this.teamsFiltered = teams;
    }

    public EventTeamViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_team_view, parent, false);

        EventTeamViewHolder vh = new EventTeamViewHolder(v);
        return vh;
    }

    public void hideSeperators(){
        showSeperators = false;
    }

    public void showSeperators(){
        showSeperators = true;
    }

    @Override
    public void onBindViewHolder(@NonNull EventTeamViewHolder holder, int position) {

        Team team = teamsFiltered[position];
        holder.textView.setText(team.getName());
        Picasso.get().load(team.getImageUrl()).into(holder.imageView);
        if(team.getMaleMatchups().size() == 0){
            holder.maleMatchupContainer.setVisibility(View.GONE);
        } else {
            holder.maleMatchupContainer.setVisibility(View.VISIBLE);
            holder.maleMatchupDisplay.removeAllViews();
            for(String name : team.getMaleMatchups()){
                TextView example = holder.maleMatchupTextView;
                TextView newtextview = new TextView(holder.maleMatchupDisplay.getContext());
                newtextview.setText(name);
                newtextview.setLayoutParams(example.getLayoutParams());
                newtextview.setTextColor(example.getTextColors());
                newtextview.setVisibility(View.VISIBLE);
                holder.maleMatchupDisplay.addView(newtextview);
            }
        }
        if(team.getFemaleMatchups().size() == 0){
            holder.femaleMatchupContainer.setVisibility(View.GONE);
        } else {
            holder.femaleMatchupContainer.setVisibility(View.VISIBLE);
            holder.femaleMatchupDisplay.removeAllViews();
            for(String name : team.getFemaleMatchups()){
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
        return teamsFiltered.length;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    teamsFiltered = teams;
                } else {
                    ArrayList<Team> filteredList = new ArrayList<>();
                    for(Team team : teams){
                        if(team.getName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(team);
                        }
                    }
                    teamsFiltered = filteredList.toArray(new Team[filteredList.size()]);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = teamsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                teamsFiltered = (Team[]) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
