package com.example.wuapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.ReportResultActivity;
import com.example.wuapp.databinding.FragmentSettingsBinding;
import com.example.wuapp.model.ReportFormState;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.User;
import com.example.wuapp.model.UserLoginToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater);

        MainActivity activity = (MainActivity) getActivity();
        UserLoginToken loginToken = activity.getDataManager().getLoginToken();

        binding.username.setText(loginToken.getName());
        Picasso.get().load(loginToken.getProfileImage()).into(binding.userImage);

        binding.eventCacheSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                activity.getDataManager().setCacheEvents(isChecked);
            }
        });


        return binding.getRoot();
    }
}