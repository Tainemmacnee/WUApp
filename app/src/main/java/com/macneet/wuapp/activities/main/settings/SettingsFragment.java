package com.macneet.wuapp.activities.main.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.macneet.wuapp.BuildConfig;
import com.macneet.wuapp.datamanagers.ConfigManager;
import com.macneet.wuapp.datamanagers.EventsManager;
import com.macneet.wuapp.activities.main.MainActivity;
import com.macneet.wuapp.databinding.FragmentSettingsBinding;
import com.macneet.wuapp.model.UserLoginToken;
import com.macneet.wuapp.activities.main.DisplayFragment;
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

        binding.versionNumber.setText(BuildConfig.VERSION_NAME);

        return binding.getRoot();
    }

    @Override
    protected void refresh() {
    }
}