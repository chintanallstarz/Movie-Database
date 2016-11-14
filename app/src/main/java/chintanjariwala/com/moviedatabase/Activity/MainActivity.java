package chintanjariwala.com.moviedatabase.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import chintanjariwala.com.moviedatabase.Fragment.HomeFragment;
import chintanjariwala.com.moviedatabase.Fragment.TrendingFragment;
import chintanjariwala.com.moviedatabase.R;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, TrendingFragment.OnFragmentInteractionListener
{
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView tvNavName;
    private TextView tvNavEmail = null;
    private Toolbar toolbar;
    //Base URL to make calls
    final String baseURL = "https://api.themoviedb.org/3";

    //index for current nav_menu item
    private static int navItemIndex = 0;


    private static final String TAG = MainActivity.class.getSimpleName();

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_TRENDING = "photos";
    private static final String TAG_TOP = "movies";
    public static String CURRENT_TAG = TAG_HOME;

    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the contents
        init();

        setSupportActionBar(toolbar);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //Setup the navigation bar
        setupNavBar();
        
        getTheTrendingData();

        setupNavigationView();

        if (savedInstanceState == null){
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_trending:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_TRENDING;
                        break;
                    default:
                        navItemIndex = 0;
                }

                if (item.isChecked()){
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                }
                item.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    private void loadHomeFragment() {
        selectNavMenu();
        
        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                TrendingFragment trendingFragment= new TrendingFragment();
                return trendingFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void getTheTrendingData() {
        Uri builtURI = Uri.parse(baseURL).buildUpon()
                .appendPath("movie").appendPath("now_playing").appendQueryParameter("api_key",getString(R.string.TMDB_api_key))
                .appendQueryParameter("language","en-US").build();
        Log.d(TAG, "getTheTrendingData: "+ builtURI.toString() );
    }

    private void setupNavBar() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string .myPrefs), Context.MODE_PRIVATE);
        String name  = sharedPreferences.getString("name","Test");
        String email = sharedPreferences.getString("email","Test");
        Log.d(TAG, "setupNavBar: "+ name + " " + email);
//        if(name != null && email != null){
//            tvNavEmail.setText(email);
//            tvNavName.setText(name);
//        }

    }



    private void init() {
        //basic items
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //header items
        navHeader = navigationView.getHeaderView(0);
        tvNavName = (TextView) navHeader.findViewById(R.id.drawer_layout);
        tvNavEmail = (TextView) navHeader.findViewById(R.id.tvNavEmail);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}