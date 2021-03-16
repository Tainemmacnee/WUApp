package com.example.wuapp.ui.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;

import com.example.wuapp.ui.activity.MainActivity;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.databinding.FragmentSettingsBinding;
import com.example.wuapp.model.UserLoginToken;
import com.squareup.picasso.Picasso;


public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    DataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater);

        MainActivity activity = (MainActivity) getActivity();
        UserLoginToken loginToken = activity.getDataManager().getLoginToken();
        dataManager = activity.getDataManager();

        binding.username.setText(loginToken.getName());
        Picasso.get().load(loginToken.getProfileImage()).into(binding.userImage);

        binding.eventCacheSwitch.setChecked(dataManager.getConfig().getCacheEvents());
        binding.eventCacheSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                dataManager.getConfig().setCacheEvents(isChecked, getContext());
            }
        });

        binding.loginCacheSwitch.setChecked(dataManager.getConfig().getCacheLogin());
        binding.loginCacheSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                dataManager.getConfig().setCacheLogin(isChecked, getContext());
            }
        });


        return binding.getRoot();
    }
}