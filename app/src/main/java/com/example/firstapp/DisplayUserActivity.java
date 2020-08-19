package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstapp.model.User;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;

public class DisplayUserActivity extends AppCompatActivity {

    public static final String MESSAGEEVENTNAME = "messageeventname";
    private AppBarConfiguration mAppBarConfiguration;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_display_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_events, R.id.nav_upcoming_games)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Intent intent = getIntent();
        HashMap<String, String> cookies = (HashMap<String, String>) intent.getExtras().getSerializable(MainActivity.MESSAGE_COOKIES);
        HashMap<String, String> links = (HashMap<String, String>) intent.getExtras().getSerializable(MainActivity.MESSAGE_LINKS);
        this.user = new User(cookies, links);

        View hview = navigationView.getHeaderView(0);
        Picasso.get().load(this.user.getProfileImgUrl()).into((ImageView) hview.findViewById(R.id.nav_header_profile_image));
        TextView nav_user = (TextView)hview.findViewById(R.id.username);
        nav_user.setText(this.user.getName());
    }

    public User getUserData() {
        return this.user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_user_activity, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.action_refresh){
            refresh();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(){
        Intent intent = new Intent(this, DisplayUserActivity.class);//DisplayUserActivity.class);
        intent.putExtra(MainActivity.MESSAGE_COOKIES, user.getCookies());
        intent.putExtra(MainActivity.MESSAGE_LINKS, user.getLinks());
        startActivity(intent);
        finish();
    }

    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);//DisplayUserActivity.class);
        startActivity(intent);
        finish();
    }


}