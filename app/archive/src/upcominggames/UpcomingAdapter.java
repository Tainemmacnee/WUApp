package com.example.wuapp.ui.upcominggames;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.R;
import com.example.wuapp.model.Game;
import com.squareup.picasso.Picasso;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    private Game[] games;

    public class UpcomingViewHolder extends RecyclerView.ViewHolder {
        public TextView homeTeamName;
        public TextView awayTeamName;
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
        }
    }

    public UpcomingAdapter(Game[] games) {
        this.games = games;
    }

    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_game_view, parent, false);

        return new UpcomingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        System.out.println(games.length);
        holder.homeTeamName.setText(games[position].getHomeTeamName());
        holder.awayTeamName.setText(games[position].getAwayTeamName());
        holder.date.setText(games[position].getDate());
        holder.time.setText(games[position].getTime());
        holder.league.setText(games[position].getLeague());
        holder.location.setText(games[position].getLocation());
        Picasso.get().load(games[position].getHomeTeamImg()).into(holder.homeTeamImage);
        Picasso.get().load(games[position].getAwayTeamImg()).into(holder.awayTeamImage);
    }


    @Override
    public int getItemCount() {
        return games.length;
    }


}
