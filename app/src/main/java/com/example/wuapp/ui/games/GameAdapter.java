package com.example.wuapp.ui.games;

import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.ReportResultActivity;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.example.wuapp.ui.events.teams.EventTeamsAdapter;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> implements Filterable {

    private Game[] gamesFiltered;
    private Game[] games;
    private List<Event> events;

    public class GameViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout root;
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

        public ExpandableLayout expandableLayout;
        public RecyclerView gameTeamsRecyclerView;

        public View itemSeperator;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.game_view_root);
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
            this.expandableLayout = itemView.findViewById(R.id.expandable_layout);
            this.gameTeamsRecyclerView = itemView.findViewById(R.id.game_teams_recyclerview);
            this.itemSeperator = itemView.findViewById(R.id.item_seperator);
        }
    }

    public GameAdapter(List<Game> games, List<Event> events) {
        Game[] gamesArray = games.toArray(new Game[games.size()]);
        this.games = gamesArray;
        this.gamesFiltered = gamesArray;
        this.events = events;
    }

    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_view, parent, false);

        return new GameViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.expandableLayout.isExpanded()){
                    holder.expandableLayout.collapse();
                } else {
                    holder.expandableLayout.expand();
                }
            }
        });

        holder.homeTeamName.setText(gamesFiltered[position].getHomeTeamName());
        holder.awayTeamName.setText(gamesFiltered[position].getAwayTeamName());
        holder.date.setText(gamesFiltered[position].getDate());
        holder.time.setText(gamesFiltered[position].getTime());
        holder.league.setText(gamesFiltered[position].getLeague());
        holder.location.setText(gamesFiltered[position].getLocation());
        Picasso.get().load(gamesFiltered[position].getHomeTeamImg()).into(holder.homeTeamImage);
        Picasso.get().load(gamesFiltered[position].getAwayTeamImg()).into(holder.awayTeamImage);

        holder.homeTeamScore.setText(gamesFiltered[position].getHomeTeamScore());
        holder.awayTeamScore.setText(gamesFiltered[position].getAwayTeamScore());
        holder.awayTeamSpirit.setText(gamesFiltered[position].getAwayTeamSpirit());
        holder.homeTeamSpirit.setText(gamesFiltered[position].getHomeTeamSpirit());

        if(gamesFiltered[position].isReportable()) {
            holder.reportResultButton.setVisibility(View.VISIBLE);
            holder.reportResultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayUserActivity a = (DisplayUserActivity) view.getContext();
                    Event event = a.getUser().getEvent(gamesFiltered[position].getLeague());

                    Team homeTeam = event.getTeam(gamesFiltered[position].getHomeTeamName());
                    Team awayTeam = event.getTeam(gamesFiltered[position].getAwayTeamName());
                    Intent intent = new Intent(view.getContext(), ReportResultActivity.class);
                    intent.putExtra(DisplayUserActivity.MESSAGEHOMETEAM, homeTeam);
                    intent.putExtra(DisplayUserActivity.MESSAGEAWAYTEAM, awayTeam);
                    intent.putExtra(DisplayUserActivity.MESSAGEREPORTLINK, gamesFiltered[position].getReportLink());
                    intent.putExtra(DisplayUserActivity.MESSAGEUSERNAME, a.getUser().getName());
                    intent.putExtra(MainActivity.MESSAGE_COOKIES, a.getUser().getCookies());
                    view.getContext().startActivity(intent);
                }
            });
        } else {
            holder.reportResultButton.setVisibility(View.GONE);
        }

        int red = Color.rgb(255, 0, 0);
        int green = Color.rgb(0, 177, 64);

        if(gamesFiltered[position].getHomeTeamScore().contains("W")){
            holder.homeTeamScore.setTextColor(green);
            holder.awayTeamScore.setTextColor(red);
        } else if(gamesFiltered[position].getAwayTeamScore().contains("W")) {
            holder.awayTeamScore.setTextColor(green);
            holder.homeTeamScore.setTextColor(red);
        } else if(gamesFiltered[position].getHomeTeamScore().contains("?")){
            holder.homeTeamScore.setTextColor(Color.BLACK);
            holder.awayTeamScore.setTextColor(Color.BLACK);
        } else {
            int homeTeamScore = Integer.parseInt(gamesFiltered[position].getHomeTeamScore());
            int awayTeamScore = Integer.parseInt(gamesFiltered[position].getAwayTeamScore());
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

        //work out which 2 teams from the events need to be bound
        for(Event event : events){
            if(event.getName().equals(gamesFiltered[position].getLeague())){
                Team homeTeam = event.getTeam(gamesFiltered[position].getHomeTeamName());
                Team awayTeam = event.getTeam(gamesFiltered[position].getAwayTeamName());

                EventTeamsAdapter madapter = new EventTeamsAdapter(new Team[] {homeTeam, awayTeam});
                madapter.hideSeperators();
                holder.gameTeamsRecyclerView.setAdapter(madapter);
                holder.gameTeamsRecyclerView.setLayoutManager(new NoScrollLinearLayoutManager(holder.homeTeamName.getContext(), LinearLayoutManager.VERTICAL, false));
            }
        }

        if(position == getItemCount()-1){
            holder.itemSeperator.setVisibility(View.INVISIBLE);
        } else {
            holder.itemSeperator.setVisibility(View.VISIBLE);
        }
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
