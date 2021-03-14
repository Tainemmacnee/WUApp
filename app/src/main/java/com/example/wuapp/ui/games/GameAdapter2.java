package com.example.wuapp.ui.games;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.ReportResultActivity;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameAdapter2 extends RecyclerView.Adapter<GameAdapter2.GameViewHolder> implements Filterable {

    private Game[] gamesFiltered;
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

    public GameAdapter2(List<Game> games) {
        Game[] gamesArray = games.toArray(new Game[games.size()]);
        this.games = gamesArray;
        this.gamesFiltered = gamesArray;
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
        holder.homeTeamName.setText(gamesFiltered[position].getHomeTeamName());
        holder.awayTeamName.setText(gamesFiltered[position].getAwayTeamName());
        holder.datetime.setText(gamesFiltered[position].getTime() + "   " + gamesFiltered[position].getDate());
        holder.location.setText(gamesFiltered[position].getLocation());
        holder.league.setText(gamesFiltered[position].getLeague());
        Picasso.get().load(gamesFiltered[position].getHomeTeamImg()).into(holder.homeTeamImage);
        Picasso.get().load(gamesFiltered[position].getAwayTeamImg()).into(holder.awayTeamImage);

        //setup buttons
        if(gamesFiltered[position].isReportable()){
            holder.reportButton.setBackgroundResource(R.drawable.rounded_background_grey);
            holder.reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) view.getContext();
                    activity.reportGame(view, gamesFiltered[position]); //pass a reference of the game to be reported.
                }
            });
        } else {
            holder.reportButton.setBackgroundResource(R.drawable.rounded_background_red);
        }

        if(!gamesFiltered[position].getLocation().contains("Liardet") &&
            !gamesFiltered[position].getLocation().contains("MacAlister")){
            holder.mapsButton.setBackgroundResource(R.drawable.rounded_background_red);
        } else {
            holder.mapsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) view.getContext();
                    activity.viewGameMap(view, gamesFiltered[position]);
                }
            });
        }

        holder.gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) view.getContext();
                activity.viewGame(gamesFiltered[position]);
            }
        });

    }

    @Override
    public int getItemCount() {
        return gamesFiltered.length;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterString = charSequence.toString();
                ArrayList<Game> filteredList = new ArrayList<>();

                switch (filterString) {
                    case FilterDialog.FILTERALL:
                        filteredList.addAll(Arrays.asList(games));
                        break;

                    case FilterDialog.FILTERUPCOMING:
                        for (Game g : games) {
                            if (g.isUpcoming()) {
                                filteredList.add(g);
                            }
                        }
                        break;

                    case FilterDialog.FILTERMISSINGRESULT:
                        for (Game g : games) {
                            if (g.isReportable()) {
                                filteredList.add(g);
                            }
                        }
                        break;

                    default:
                        for (Game g : games) {
                            if (g.getHomeTeamName().toLowerCase().contains(filterString.toLowerCase())) {
                                filteredList.add(g);
                            }
                            if (g.getAwayTeamName().toLowerCase().contains(filterString.toLowerCase())) {
                                filteredList.add(g);
                            }
                        }
                        break;
                }

                gamesFiltered = filteredList.toArray(new Game[filteredList.size()]);
                FilterResults filterResults = new FilterResults();
                filterResults.values = gamesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                gamesFiltered = (Game[]) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
