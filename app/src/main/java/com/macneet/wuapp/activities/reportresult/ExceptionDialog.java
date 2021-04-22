package com.macneet.wuapp.activities.reportresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.macneet.wuapp.R;

public class ExceptionDialog extends DialogFragment {

    ReportResultActivity activity;

    public static ExceptionDialog newInstance(String message) {
        ExceptionDialog frag = new ExceptionDialog();
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
        Button reloadButton = view.findViewById(R.id.reload_button);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.reload();
                dismiss();
            }
        });

        Button backButton = view.findViewById(R.id.back_button);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
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
