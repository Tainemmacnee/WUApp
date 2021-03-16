package com.example.wuapp.ui.fragment.games;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.example.wuapp.ui.activity.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.model.Game;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder>{

    private Game[] games;

    public class GameViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout root;
        public TextView homeTeamName;
        public TextView awayTeamName;

        public TextView datetime;
        public TextView location;
        public TextView league;
        public ImageView homeTeamImage;
        public ImageView awayTeamImage;

        public Button reportButton;
        public Button mapsButton;
        public Button gameButton;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.game_view_root);
            this.homeTeamName = itemView.findViewById(R.id.team1_name);
            this.awayTeamName = itemView.findViewById(R.id.team2_name);
            this.datetime = itemView.findViewById(R.id.game_datetime);
            this.location = itemView.findViewById(R.id.game_location);
            this.league = itemView.findViewById(R.id.game_league);
            this.homeTeamImage = itemView.findViewById(R.id.team1_image);
            this.awayTeamImage = itemView.findViewById(R.id.team2_image);
            this.reportButton = itemView.findViewById(R.id.report_button);
            this.mapsButton = itemView.findViewById(R.id.map_button);
            this.gameButton = itemView.findViewById(R.id.game_button);
        }
    }

    public GameAdapter(List<Game> games) {
        Game[] gamesArray = games.toArray(new Game[games.size()]);
        this.games = gamesArray;
    }

    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_view2, parent, false);

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

        //setup buttons
        if(games[position].isReportable()){
            holder.reportButton.setBackgroundResource(R.drawable.rounded_background_grey);
            holder.reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) view.getContext();
                    activity.reportGame(view, games[position]); //pass a reference of the game to be reported.
                }
            });
        } else {
            holder.reportButton.setBackgroundResource(R.drawable.rounded_background_red);
        }

        if(!games[position].getLocation().contains("Liardet") &&
            !games[position].getLocation().contains("MacAlister")){
            holder.mapsButton.setBackgroundResource(R.drawable.rounded_background_red);
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
