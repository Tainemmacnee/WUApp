package com.macneet.wuapp.activities.displaymap;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.macneet.wuapp.R;
import com.macneet.wuapp.model.Game;

/**
 * An Activity to load and display the standings for an event
 * @author Taine Macnee
 */
public class DisplayMapActivity extends AppCompatActivity {

    private WebView webView;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup activity display
        setContentView(R.layout.acvivity_display_map);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);

        //retrieve required info from intent
        Intent intent = getIntent();
        game = intent.getParcelableExtra(getString(R.string.MESSAGE_GAME));

        Button button7s = findViewById(R.id.button_7s);
        button7s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.button_wrapper).setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                displayImage(7);
            }
        });

        Button button4s = findViewById(R.id.button_4s);
        button4s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.button_wrapper).setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                displayImage(4);
            }
        });
    }

    public void exit(View view){
        finish();
    }

    private void displayImage(int numPlayers){
        //The first word in the location is always the field name
        String fieldName = game.getLocation().split(" ")[0].toLowerCase();
        //The field number is always the last character (there are < 10 fields so always a single digit)
        String fieldNumber = ""+game.getLocation().charAt(game.getLocation().length()-1);
        //mimic file name
        String imagePath = fieldName+"_field_"+fieldNumber+"_"+numPlayers+".png";
        //Fake html for webview to consume
        String html = "<style>body {background-color: #121212;}</style><html><head></head><body> <img src=\""+ imagePath + "\" onerror=\"this.onerror=null; this.src='fail.png'\"> </body></html>";
        //Load the image
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }


}