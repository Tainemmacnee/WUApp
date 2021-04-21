package com.example.wuapp.activities.main.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.wuapp.datamanagers.ConfigManager;
import com.example.wuapp.datamanagers.EventsManager;
import com.example.wuapp.activities.main.MainActivity;
import com.example.wuapp.databinding.FragmentSettingsBinding;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.activities.main.DisplayFragment;
import com.squareup.picasso.Picasso;


public class SettingsFragment extends DisplayFragment {

    FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater);

        MainActivity activity = (MainActivity) getActivity();
        UserLoginToken loginToken = EventsManager.getInstance().getLoginToken();

        binding.username.setText(loginToken.getName());
        Picasso.get().load(loginToken.getProfileImage()).into(binding.userImage);

        binding.eventCacheSwitch.setChecked(ConfigManager.getInstance().getCacheEvents());
        binding.eventCacheSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ConfigManager.getInstance().setCacheEvents(isChecked, getContext());
            }
        });

        binding.loginCacheSwitch.setChecked(ConfigManager.getInstance().getCacheLogin());
        binding.loginCacheSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ConfigManager.getInstance().setCacheLogin(isChecked, getContext());
            }
        });


        return binding.getRoot();
    }

    @Override
    protected void refresh() {
    }
}