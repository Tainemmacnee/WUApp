package com.example.Wuapp.ui.feedback;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Wuapp.DisplayUserActivity;
import com.example.Wuapp.R;
import com.example.Wuapp.model.Event;
import com.example.Wuapp.model.User;
import com.example.Wuapp.ui.RefreshableFragment;
import com.example.Wuapp.ui.events.EventsAdapter;

public class FeedbackFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        return v;
    }
}