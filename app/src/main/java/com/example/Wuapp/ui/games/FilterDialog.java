package com.example.Wuapp.ui.games;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.example.Wuapp.MainActivity;
import com.example.Wuapp.R;

public class FilterDialog extends DialogFragment {

    public static final String FILTERUPCOMING = "Upcoming Games";
    public static final String FILTERMISSINGRESULT = "Missing Result Games";
    public static final String FILTERALL = "All Games";

    String filter = FILTERALL;

    FilterDialogListner listener;

    public FilterDialog(FilterDialogListner listener){
        this.listener = listener;
    }

    public interface FilterDialogListner {
        public void onDialogPositiveClick(String filter);
    }

    private int getCheckedItem(){
        switch (filter){
            case FILTERALL:
                return 0;
            case FILTERUPCOMING:
                return 1;
            case FILTERMISSINGRESULT:
                return 2;
        }
        return 0;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.FilterMenu));
        String[] filters = new String[]{"All Games", "Upcoming Games", "Missing Result Games"};

        builder.setTitle("Filter games list")
            .setSingleChoiceItems(filters, getCheckedItem(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filter = filters[i];
                }
            })
            .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ListView lw = ((AlertDialog)dialogInterface).getListView();
                    String checkedItem = (String) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                    listener.onDialogPositiveClick(checkedItem);
                }
            });

        return builder.create();
    }
}
