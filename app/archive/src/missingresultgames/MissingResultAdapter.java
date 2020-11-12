package com.example.wuapp.ui.missingresultgames;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.ReportResultActivity;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
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
        public Button reportResultButton;

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
            this.awayTeamSpirit = itemView.findViewById(R.id.away_team_spirit);
            this.reportResultButton = itemView.findViewById(R.id.report_results);
        }
    }

    public MissingResultAdapter(Game[] games) {
        this.games = games;
    }

    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_games, parent, false);

        return new UpcomingViewHolder(v);
    }

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

        holder.homeTeamScore.setText(games[position].getHomeTeamScore());
        holder.awayTeamScore.setText(games[position].getAwayTeamScore());
        holder.awayTeamSpirit.setText(games[position].getAwayTeamSpirit());
        holder.homeTeamSpirit.setText(games[position].getHomeTeamSpirit());

        holder.reportResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayUserActivity a = (DisplayUserActivity)view.getContext();
                Event event = a.getUserData().getEvent(games[position].getLeague());
                Team homeTeam = event.getTeam(games[position].getHomeTeamName());
                Team awayTeam = event.getTeam(games[position].getAwayTeamName());
                Intent intent = new Intent(view.getContext(), ReportResultActivity.class);
                intent.putExtra(DisplayUserActivity.MESSAGEHOMETEAM, homeTeam);
                intent.putExtra(DisplayUserActivity.MESSAGEAWAYTEAM, awayTeam);
                intent.putExtra(DisplayUserActivity.MESSAGEREPORTLINK, games[position].getReportLink());
                intent.putExtra(DisplayUserActivity.MESSAGEUSERNAME, a.getUserData().getName());
                intent.putExtra(MainActivity.MESSAGE_COOKIES, a.getUserData().getCookies());
                view.getContext().startActivity(intent);
            }
        });

        int red = Color.rgb(255, 0, 0);
        int green = Color.rgb(0, 177, 64);

        if(games[position].getHomeTeamScore().contains("W")){
            holder.homeTeamScore.setTextColor(green);
            holder.awayTeamScore.setTextColor(red);
        } else if(games[position].getAwayTeamScore().contains("W")) {
            holder.awayTeamScore.setTextColor(green);
            holder.homeTeamScore.setTextColor(red);
        } else if(games[position].getHomeTeamScore().contains("?")){
            holder.homeTeamScore.setTextColor(Color.BLACK);
            holder.awayTeamScore.setTextColor(Color.BLACK);
        } else {
            int homeTeamScore = Integer.parseInt(games[position].getHomeTeamScore());
            int awayTeamScore = Integer.parseInt(games[position].getAwayTeamScore());
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
