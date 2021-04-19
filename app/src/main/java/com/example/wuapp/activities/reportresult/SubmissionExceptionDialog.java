package com.example.wuapp.activities.reportresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.wuapp.R;

public class SubmissionExceptionDialog extends DialogFragment {

    ReportResultActivity activity;

    public static SubmissionExceptionDialog newInstance(String message) {
        SubmissionExceptionDialog frag = new SubmissionExceptionDialog();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exception_display, container);
    }
//
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.activity = (ReportResultActivity) getActivity();

        // Get field from view
        Button backButton = view.findViewById(R.id.reload_button);
        backButton.setText("close");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        Button exitButton = view.findViewById(R.id.back_button);
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setText("exit");
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.exit(null);
                dismiss();
            }
        });

        // Fetch arguments from bundle and set message
        String message = getArguments().getString("message", "An error has occurred");

        TextView errorMessageView = view.findViewById(R.id.error_message);
        errorMessageView.setText(message);
    }
}
