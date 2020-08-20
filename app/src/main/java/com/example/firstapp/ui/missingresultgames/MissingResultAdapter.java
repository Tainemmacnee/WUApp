package com.example.firstapp.ui.missingresultgames;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.model.Game;
import com.squareup.picasso.Picasso;

public class MissingResultAdapter extends RecyclerView.Adapter<MissingResultAdapter.UpcomingViewHolder> {

    private Game[] games;

    public class UpcomingViewHolder extends RecyclerView.ViewHolder {
        public TextView homeTeamName;
        public TextView awayTeamName;
        public TextView homeTeamScore;
        public TextView awayTeamSpirit;
        public TextView awayTeamScore;
        public TextView homeTeamSpirit;

        public TextView date;
        public TextView time;
        public TextView league;
        public TextView location;
        public ImageView homeTeamImage;
        public ImageView awayTeamImage;
        public UpcomingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.homeTeamName = itemView.findViewById(R.id.event_team_name);
            this.awayTeamName = itemView.findViewById(R.id.upcoming_game_away_team);
            this.date = itemView.findViewById(R.id.upcoming_game_date);
            this.time = itemView.findViewById(R.id.upcoming_game_time);
            this.league = itemView.findViewById(R.id.upcoming_game_league);
            this.location = itemView.findViewById(R.id.upcoming_game_location);
            this.homeTeamImage = itemView.findViewById(R.id.event_team_image);
            this.awayTeamImage = itemView.findViewById(R.id.upcoming_game_away_image);
            this.homeTeamScore = itemView.findViewById(R.id.home_team_score);
            this.awayTeamScore = itemView.findViewById(R.id.away_team_score);
            this.homeTeamSpirit = itemView.findViewById(R.id.home_team_spirit);
            this.awayTeamSpirit = itemView.findViewById(R.id.home_team_spirit);
        }
    }

    public MissingResultAdapter(Game[] games) {
        this.games = games;
    }

    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.missing_result_game_view, parent, false);

        return new UpcomingViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        holder.homeTeamName.setText(games[position].getHomeTeamName());
        holder.awayTeamName.setText(games[position].getAwayTeamName());
        holder.date.setText(games[position].getDate());
        holder.time.setText(games[position].getTime());
        holder.league.setText(games[position].getLeague());
        holder.location.setText(games[position].getLocation());
        Picasso.get().load(games[position].getHomeTeamImg()).into(holder.homeTeamImage);
        Picasso.get().load(games[position].getAwayTeamImg()).into(holder.awayTeamImage);

        holder.homeTeamScore.setText(games[position].homeTeamScore);
        holder.awayTeamScore.setText(games[position].awayTeamScore);
        holder.awayTeamSpirit.setText(games[position].awayTeamSpirit);
        holder.homeTeamSpirit.setText(games[position].homeTeamSpirit);

        int red = Color.rgb(255, 0, 0);
        int green = Color.rgb(0, 177, 64);

        if(games[position].homeTeamScore.contains("W")){
            holder.homeTeamScore.setTextColor(green);
            holder.awayTeamScore.setTextColor(red);
        } else if(games[position].awayTeamScore.contains("W")){
            holder.awayTeamScore.setTextColor(green);
            holder.homeTeamScore.setTextColor(red);
        } else {
            int homeTeamScore = Integer.parseInt(games[position].homeTeamScore);
            int awayTeamScore = Integer.parseInt(games[position].awayTeamScore);
            if(homeTeamScore == awayTeamScore){
                holder.homeTeamScore.setTextColor(Color.BLACK);
                holder.awayTeamScore.setTextColor(Color.BLACK);
            } else if(homeTeamScore > awayTeamScore){
                holder.homeTeamScore.setTextColor(green);
                holder.awayTeamScore.setTextColor(red);
            } else {
                holder.homeTeamScore.setTextColor(red);
                holder.awayTeamScore.setTextColor(green);
            }
        }
    }


    @Override
    public int getItemCount() {
        return games.length;
    }


}
