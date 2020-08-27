package com.example.firstapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView userName = root.findViewById(R.id.username_display_main);
        final TextView aboutText = root.findViewById(R.id.home_about_text);
        final ImageView profileImg = root.findViewById(R.id.profile_image_main);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        User user = activity.getUserData();

        userName.setText(user.getName());
        aboutText.setText((user.getAboutText()));
        Picasso.get().load(user.getProfileImgUrl()).into(profileImg);

        final LinearLayout exampleInfoBox = root.findViewById(R.id.home_example_info);
        final TextView exampleValueTextView = root.findViewById(R.id.home_example_value);
        final TextView exampleTitleTextView = root.findViewById(R.id.home_example_title);

        LinearLayout infoDisplay = root.findViewById(R.id.home_info_display);

        for(String key : user.getProfileInfo().keySet()){
            System.out.println(key);
            LinearLayout infoBox = new LinearLayout(getContext());
            TextView infoTitle = new TextView(getContext());
            Space spacer = new Space(getContext());
            spacer.setLayoutParams(root.findViewById(R.id.space).getLayoutParams());
            TextView infoValue = new TextView(getContext());

            infoBox.setOrientation(LinearLayout.HORIZONTAL);
            infoBox.addView(infoTitle);
            infoBox.addView(spacer);
            infoBox.addView(infoValue);

            infoTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            infoValue.setTextColor(getResources().getColor(R.color.colorPrimary));

            infoTitle.setLayoutParams(exampleTitleTextView.getLayoutParams());
            infoValue.setLayoutParams(exampleValueTextView.getLayoutParams());

            infoTitle.setText(key);
            infoValue.setText(user.getProfileInfo().get(key));

            infoBox.setLayoutParams(exampleInfoBox.getLayoutParams());

            infoDisplay.addView(infoBox);
        }

        return root;
    }
}