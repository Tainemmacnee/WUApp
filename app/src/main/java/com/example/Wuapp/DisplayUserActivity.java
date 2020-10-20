package com.example.Wuapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Wuapp.model.Event;
import com.example.Wuapp.model.Game;
import com.example.Wuapp.model.User;
import com.example.Wuapp.model.UserLoginToken;
import com.example.Wuapp.ui.RefreshableFragment;
import com.example.Wuapp.ui.loading.LoadingScreen;
import com.example.Wuapp.model.WebLoader;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An Activity to display all of the main user related information
 * e.g. Games, Events, ect.
 */
public class DisplayUserActivity extends AppCompatActivity implements LoadingScreen.loadableActivity {

    public static final String MESSAGEEVENTNAME = "messageeventname";
    public static final String MESSAGEEVENTTEAMS = "messageeventteams";
    public static final String MESSAGEHOMETEAM = "messagehometeam";
    public static final String MESSAGEAWAYTEAM = "messageawayteam";
    public static final String MESSAGEREPORTLINK = "messagereportlink";
    public static final String MESSAGEUSERNAME = "messageusername";

    private AppBarConfiguration mAppBarConfiguration;
    private User user;
    private NavController navController;
    private UserLoginToken loginToken;

    private Future<List<Event>> futureEvents;
    private Future<List<Game>> futureGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load User
        Intent intent = getIntent();
        this.loginToken = (UserLoginToken) intent.getExtras().getSerializable(MainActivity.MESSAGE_LOGINTOKEN);

        futureEvents = WebLoader.LoadEvents(loginToken.getCookies());
        futureGames = WebLoader.LoadGames(loginToken.getCookies(), loginToken.getLinks().get(User.GAMESLINK));

        loadUser(loginToken);
    }

    /**
     * This function is used to load the users information from a login token
     * @param loginToken a token genetated when the user authenticates themselves
     */
    private void loadUser(UserLoginToken loginToken){
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Loading User", WebLoader.loadUser(loginToken), this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_view, loadingScreen);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * This function is used to setup the activity's display after it is finished loading the user.
     */
    private void lateOnCreate(){
        setContentView(R.layout.acivity_display_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder
                (R.id.nav_dashboard, R.id.nav_home, R.id.nav_events, R.id.nav_games, R.id.nav_maps)
                .setDrawerLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View hview = navigationView.getHeaderView(0);
        Picasso.get().load(this.user.getProfileImgUrl()).into((ImageView) hview.findViewById(R.id.nav_header_profile_image));
        TextView nav_user = (TextView)hview.findViewById(R.id.username);
        nav_user.setText(this.user.getNickName());
    }

    public User getUser() {
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

    /**
     * This function is called when the user wants to reload all of the data being displayed
     * on a given fragment and passes the call on if it is refreshable
     */
    private void refresh(){
        Fragment fragment =  getSupportFragmentManager().getFragments().get(1).getChildFragmentManager().getFragments().get(0);
        if(fragment instanceof RefreshableFragment){
            RefreshableFragment refreshableFragment = (RefreshableFragment) fragment;
            refreshableFragment.refresh();
        }
    }

    /**
     * This function is used to navigate the display to the feedback fragment
     * @param view
     */
    public void sendFeedback(View view){
        navController.navigate(R.id.nav_feedback);
        DrawerLayout drawerLayout = this.findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    /**
     * This function is used to log the user out
     * @param view
     */
    public void logout(View view){
        File file = new File(getApplicationContext().getFilesDir(), "login.txt");
        if (file.exists()) {
            file.delete();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    /**
     * This function is called when the activity finishes loading something
     * @param result the results returned when the loading finished
     * @param finished a boolean showing if there are more things to finish loading or not
     */
    public void processResult(Object Result, boolean finished) {
        if(finished){
            this.user = (User) Result;
            this.user.setData(futureEvents, futureGames);
            lateOnCreate();
        }
    }
}