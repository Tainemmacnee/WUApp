package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstapp.model.User;
import com.example.firstapp.model.UserLoginToken;
import com.example.firstapp.ui.RefreshableFragment;
import com.example.firstapp.ui.loading.LoadingScreen;
import com.example.firstapp.model.WebLoader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load User
        Intent intent = getIntent();
        this.loginToken = (UserLoginToken) intent.getExtras().getSerializable(MainActivity.MESSAGE_LOGINTOKEN);

        loadUser(loginToken);
    }

    private void loadUser(UserLoginToken loginToken){
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Loading User", WebLoader.loadUser(loginToken), this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_view, loadingScreen);
        transaction.addToBackStack(null);
        transaction.commit();
    }

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
                (R.id.nav_dashboard, R.id.nav_home, R.id.nav_events, R.id.nav_upcoming_games, R.id.nav_missing_result_games)
                .setDrawerLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View hview = navigationView.getHeaderView(0);
        Picasso.get().load(this.user.getProfileImgUrl()).into((ImageView) hview.findViewById(R.id.nav_header_profile_image));
        TextView nav_user = (TextView)hview.findViewById(R.id.username);
        nav_user.setText(this.user.getNickName());
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
        Fragment fragment =  getSupportFragmentManager().getFragments().get(1).getChildFragmentManager().getFragments().get(0);
        if(fragment instanceof RefreshableFragment){
            RefreshableFragment refreshableFragment = (RefreshableFragment) fragment;
            refreshableFragment.refresh();
        }
    }

    public void logout(View view){
        File file = new File(getApplicationContext().getFilesDir(), "login.txt");
        if (file.exists()) {
            file.delete();
        }
        Intent intent = new Intent(this, MainActivity.class);//DisplayUserActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void processResult(Object Result, boolean finished) {
        if(finished){
            this.user = (User) Result;
            lateOnCreate();
        }
    }
}