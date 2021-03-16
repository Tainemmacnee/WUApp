package com.example.wuapp.ui.fragment.events.scores;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.wuapp.DisplayEventScoresActivity;
import com.example.wuapp.R;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class scoresFragment extends Fragment {



    public scoresFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scores, container, false);
        //loadTable(view);
        return view;
    }

//    private void loadTable(View view){
//        TableRow exampleTableRow = view.findViewById(R.id.scores_example_tablerow);
//        LinearLayout exampleTeamBox = view.findViewById(R.id.scores_example_teambox);
//        CardView exampleImageCard = view.findViewById(R.id.example_image_card);
//        ImageView exampleTeamImage = view.findViewById(R.id.scores_example_team_image);
//        TextView exampleTeamName = view.findViewById(R.id.scores_example_team_name);
//        TextView exampleTeamRecord = view.findViewById(R.id.scores_example_record);
//        TextView exampleTeamSpirit = view.findViewById(R.id.scores_example_spirit);
//        TextView exampleTeamPointDiff = view.findViewById(R.id.scores_example_pointdiff);
//        TableLayout table = view.findViewById(R.id.main_table);
//
//        DisplayEventScoresActivity activity = (DisplayEventScoresActivity) getActivity();
//        for(Map<String, String> info : activity.getStandings()){
//            TableRow tableRow = new TableRow(getContext());
//            LinearLayout teamBox = new LinearLayout(getContext());
//            CardView imageCard = new CardView(getContext());
//            ImageView teamImage = new ImageView(getContext());
//            TextView teamName = new TextView(getContext());
//            TextView teamRecord = new TextView(getContext());
//            TextView teamSpirit = new TextView(getContext());
//            TextView teamPointDiff = new TextView(getContext());
//
//            tableRow.setLayoutParams(exampleTableRow.getLayoutParams());
//            teamBox.setLayoutParams(exampleTeamBox.getLayoutParams());
//            imageCard.setLayoutParams(exampleImageCard.getLayoutParams());
//            imageCard.setRadius(exampleImageCard.getRadius());
//            teamImage.setLayoutParams(exampleTeamImage.getLayoutParams());
//            teamImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            teamName.setLayoutParams(exampleTeamName.getLayoutParams());
//            teamRecord.setLayoutParams(exampleTeamRecord.getLayoutParams());
//            teamSpirit.setLayoutParams(exampleTeamSpirit.getLayoutParams());
//            teamPointDiff.setLayoutParams(exampleTeamPointDiff.getLayoutParams());
//
//            Picasso.get().load(info.get("image")).into(teamImage);
//            teamName.setText(info.get("name"));
//            teamRecord.setText(info.get("record"));
//            teamSpirit.setText(info.get("spirit"));
//            teamPointDiff.setText(info.get("pointDiff"));
//
//            tableRow.setGravity(Gravity.CENTER);
//            teamName.setGravity(exampleTeamName.getGravity());
//            teamRecord.setGravity(exampleTeamRecord.getGravity());
//            teamSpirit.setGravity(exampleTeamSpirit.getGravity());
//            teamPointDiff.setGravity(exampleTeamPointDiff.getGravity());
//
//            teamName.setTextColor(exampleTeamName.getTextColors());
//            teamRecord.setTextColor(exampleTeamRecord.getTextColors());
//            teamSpirit.setTextColor(exampleTeamSpirit.getTextColors());
//            teamPointDiff.setTextColor(exampleTeamPointDiff.getTextColors());
//
//            teamName.setTextSize(18);
//            teamRecord.setTextSize(18);
//            teamSpirit.setTextSize(18);
//            teamPointDiff.setTextSize(18);
//
//            tableRow.setBackground(exampleTableRow.getBackground());
//
//            imageCard.addView(teamImage);
//            teamBox.addView(imageCard);
//            teamBox.addView(teamName);
//            tableRow.addView(teamBox);
//            tableRow.addView(getSpace(view));
//            tableRow.addView(teamRecord);
//            tableRow.addView(getSpace(view));
//            tableRow.addView(teamSpirit);
//            tableRow.addView(getSpace(view));
//            tableRow.addView(teamPointDiff);
//            tableRow.addView(getSpace(view));
//            table.addView(tableRow);
//        }
//
//    }

    private Space getSpace(View view){
        Space space = new Space(getContext());
        space.setLayoutParams(view.findViewById(R.id.spacer).getLayoutParams());
        return space;
    }
}