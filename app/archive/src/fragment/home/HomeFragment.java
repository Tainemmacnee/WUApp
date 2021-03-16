package com.example.wuapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.R;
import com.example.wuapp.databinding.FragmentHomeBinding;
import com.example.wuapp.databinding.ProfileInfoRowBinding;
import com.example.wuapp.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       binding = FragmentHomeBinding.inflate(inflater, container, false);
       View view = binding.getRoot();

       DisplayUserActivity activity = (DisplayUserActivity)getActivity();
       User user = activity.getUser();

       binding.usernameDisplay.setText(user.getName());
       binding.homeAboutText.setText(user.getAboutText());
       Picasso.get().load(user.getProfileImgUrl()).into(binding.profileImageMain);

       for(String key : user.getProfileInfo().keySet()){

           System.out.println("K: "+key);

           ProfileInfoRowBinding rowBinding = ProfileInfoRowBinding.inflate(inflater);
           rowBinding.profileInfoTitle.setText(key);
           rowBinding.profileInfoValue.setText(user.getProfileInfo().get(key));

           binding.homeInfoDisplay.addView(rowBinding.getRoot());
       }

       return view;
    }
}