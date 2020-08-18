package com.example.firstapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView userName = root.findViewById(R.id.username_display_main);
        final TextView gId = root.findViewById(R.id.home_gender_id);
        final TextView age = root.findViewById(R.id.home_age);
        final TextView dHand = root.findViewById(R.id.home_dominant_hand);
        final TextView aboutText = root.findViewById(R.id.home_about_text);
        final ImageView profileImg = root.findViewById(R.id.profile_image_main);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        User user = activity.getUserData();

        userName.setText(user.getName());
        gId.setText(user.getgId());
        age.setText(user.getAge());
        dHand.setText(user.getdHand());
        aboutText.setText((user.getAboutText()));
        Picasso.get().load(user.getProfileImgUrl()).into(profileImg);

        return root;
    }
}