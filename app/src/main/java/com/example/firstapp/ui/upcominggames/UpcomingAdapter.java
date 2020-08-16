package com.example.firstapp.ui.upcominggames;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.model.UpcomingGame;
import com.squareup.picasso.Picasso;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    private UpcomingGame[] upcomingGames;

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
            this.homeTeamName = (TextView) itemView.findViewById(R.id.upcoming_game_home_team);
            this.awayTeamName = (TextView) itemView.findViewById(R.id.upcoming_game_away_team);
            this.date = (TextView) itemView.findViewById(R.id.upcoming_game_date);
            this.time = (TextView) itemView.findViewById(R.id.upcoming_game_time);
            this.league = (TextView) itemView.findViewById(R.id.upcoming_game_league);
            this.location = (TextView) itemView.findViewById(R.id.upcoming_game_location);
            this.homeTeamImage = (ImageView) itemView.findViewById(R.id.upcoming_game_home_image);
            this.awayTeamImage = (ImageView) itemView.findViewById(R.id.upcoming_game_away_image);
        }
    }

    public UpcomingAdapter(UpcomingGame[] upcomingGames) {
        this.upcomingGames = upcomingGames;
    }

    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_game_view, parent, false);

        UpcomingViewHolder vh = new UpcomingViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        holder.homeTeamName.setText(upcomingGames[position].getHomeTeamName());
        holder.awayTeamName.setText(upcomingGames[position].getAwayTeamName());
        holder.date.setText(upcomingGames[position].getDate());
        holder.time.setText(upcomingGames[position].getTime());
        holder.league.setText(upcomingGames[position].getLeague());
        holder.location.setText(upcomingGames[position].getLocation());
        Picasso.get().load(upcomingGames[position].getHomeTeamImg()).into(holder.homeTeamImage);
        Picasso.get().load(upcomingGames[position].getAwayTeamImg()).into(holder.awayTeamImage);
    }


    @Override
    public int getItemCount() {
        return upcomingGames.length;
    }


}
