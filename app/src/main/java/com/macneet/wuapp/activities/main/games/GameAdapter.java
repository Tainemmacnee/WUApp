package com.macneet.wuapp.activities.main.games;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.macneet.wuapp.activities.main.MainActivity;
import com.macneet.wuapp.R;
import com.macneet.wuapp.model.Game;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder>{

    private Game[] games;

    public class GameViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout root;
        public TextView homeTeamName;
        public TextView awayTeamName;
        public ImageView homeTeamImage;
        public ImageView awayTeamImage;

        public CardView homeTeamScoresCard;
        public CardView awayTeamScoresCard;
        public ConstraintLayout homeTeamScoreBackground;
        public ConstraintLayout awayTeamScoreBackground;
        public TextView homeTeamScore;
        public TextView homeTeamSpirit;
        public TextView awayTeamScore;
        public TextView awayTeamSpirit;


        public TextView datetime;
        public TextView location;
        public TextView league;


        public Button reportButton;
        public Button mapsButton;
        public Button gameButton;

        public ConstraintLayout gameInfoDisplay;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.game_view_root);
            gameInfoDisplay = itemView.findViewById(R.id.game_info_display);
            this.homeTeamName = gameInfoDisplay.findViewById(R.id.team1_name);
            this.awayTeamName = gameInfoDisplay.findViewById(R.id.team2_name);
            this.homeTeamImage = gameInfoDisplay.findViewById(R.id.team1_image);
            this.awayTeamImage = gameInfoDisplay.findViewById(R.id.team2_image);
            this.homeTeamScoresCard = gameInfoDisplay.findViewById(R.id.team1_scores_card);
            this.awayTeamScoresCard = gameInfoDisplay.findViewById(R.id.team2_scores_card);
            this.homeTeamScoreBackground = gameInfoDisplay.findViewById(R.id.team1_score_background);
            this.awayTeamScoreBackground = gameInfoDisplay.findViewById(R.id.team2_score_background);
            this.homeTeamScore = gameInfoDisplay.findViewById(R.id.team1_score);
            this.homeTeamSpirit = gameInfoDisplay.findViewById(R.id.team1_spirit);
            this.awayTeamScore = gameInfoDisplay.findViewById(R.id.team2_score);
            this.awayTeamSpirit = gameInfoDisplay.findViewById(R.id.team2_spirit);
            this.datetime = itemView.findViewById(R.id.game_datetime);
            this.location = itemView.findViewById(R.id.game_location);
            this.league = itemView.findViewById(R.id.game_league);

            this.reportButton = itemView.findViewById(R.id.report_button);
            this.mapsButton = itemView.findViewById(R.id.map_button);
            this.gameButton = itemView.findViewById(R.id.game_button);

        }
    }

    public GameAdapter(List<Game> games) {
        Game[] gamesArray = games.toArray(new Game[games.size()]);
        this.games = gamesArray;
    }

    private ViewGroup parent;

    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        this.parent = parent;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_view, parent, false);
        return new GameViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {

        //setup views
        holder.homeTeamName.setText(games[position].getHomeTeamName());
        holder.awayTeamName.setText(games[position].getAwayTeamName());
        holder.datetime.setText(games[position].getTime() + "   " + games[position].getDate());
        holder.location.setText(games[position].getLocation());
        holder.league.setText(games[position].getLeague());
        Picasso.get().load(games[position].getHomeTeamImg()).into(holder.homeTeamImage);
        Picasso.get().load(games[position].getAwayTeamImg()).into(holder.awayTeamImage);

        if(games[position].hasScores()){
            holder.homeTeamScoresCard.setVisibility(View.VISIBLE);
            holder.awayTeamScoresCard.setVisibility(View.VISIBLE);
            holder.homeTeamScore.setText(games[position].getHomeTeamScore());
            holder.homeTeamSpirit.setText(games[position].getHomeTeamSpirit());
            holder.awayTeamScore.setText(games[position].getAwayTeamScore());
            holder.awayTeamSpirit.setText(games[position].getAwayTeamSpirit());

            int homeTeamScore = Integer.parseInt(games[position].getHomeTeamScore());
            int awayTeamScore = Integer.parseInt(games[position].getAwayTeamScore());
            if(homeTeamScore < awayTeamScore){
                holder.homeTeamScoreBackground.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.logout_red));
                holder.awayTeamScoreBackground.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.victory_green));
            }

        }

        //setup buttons
        if(games[position].isReportable()){
            holder.reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) view.getContext();
                    activity.reportGame(view, games[position]); //pass a reference of the game to be reported.
                }
            });
        } else {
            holder.reportButton.setEnabled(false);
        }

        if(!games[position].getLocation().contains("Liardet") &&
            !games[position].getLocation().contains("MacAlister")){
            holder.mapsButton.setEnabled(false);
            holder.mapsButton.setVisibility(View.GONE);
        } else {
            holder.mapsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) view.getContext();
                    activity.viewGameMap(view, games[position]);
                }
            });
        }

        holder.gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) view.getContext();
                activity.viewGame(games[position]);
            }
        });

    }

    @Override
    public int getItemCount() {
        return games.length;
    }


}
