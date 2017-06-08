package info.androidhive.navigationdrawer.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.navigationdrawer.Manifest;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.HomeFragment;
import info.androidhive.navigationdrawer.fragment.NotificationsFragment;
import info.androidhive.navigationdrawer.fragment.PhotosFragment;
import info.androidhive.navigationdrawer.fragment.SettingsFragment;
import info.androidhive.navigationdrawer.other.CircleTransform;
import info.androidhive.navigationdrawer.other.User;
import info.androidhive.navigationdrawer.Services.LocationService;
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private NavigationView navigationView;

    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private String currenUserId;
    public DatabaseReference mDatabase;
    private FloatingActionButton fab;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private ArrayList<User> arrUser;
    private FirebaseAuth mAuthData;
    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    private User currenUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public final static String PAR_KEY = "info.androidhive.navigationdrawer.activity.par";
    public final static String CURRENT_KEY = "info.androidhive.navigationdrawer.activity.current";

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private GoogleMap mMap;
    private LatLngBounds.Builder bounds;
    String downloadTaskType = "";

    public class DownloadTask extends AsyncTask<String, Void, Void> {
        private ChildEventListener childEventListenerAllUser;
        boolean resultGood = false;

        @Override
        protected Void doInBackground(String... params) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("lam", "addListenerForSingleValueEvent");
                    if (!dataSnapshot.getKey().equals(currenUserId)) {
                        Log.d("lam", "user " + user.getUsername());
                        user.setUid(dataSnapshot.getKey());
                        arrUser.add(user);
                    } else {
                        user.setUid(dataSnapshot.getKey());
                        currenUser = user;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//            mDatabase.addChildEventListener(childEventListenerAllUser);
//
//           childEventListenerAllUser = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    User user = dataSnapshot.getValue(User.class);
//                    Log.d("lam","se ejecuta childEventListenerAllUser");
//                    if (!dataSnapshot.getKey().equals(currenUserId)) {
//                        Log.d("lam","user "+ user.getUsername());
//                        user.setUid(dataSnapshot.getKey());
//                        arrUser.add(user);
//                    } else {
//                        user.setUid(dataSnapshot.getKey());
//                        currenUser = user;
//                    }
//
//
//
//
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//
//
//            };
            resultGood = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (resultGood) {
                mMap.clear();
                bounds = new LatLngBounds.Builder();

                Log.d("lam", "SE EJECUTA ON POST");
                for (User users : arrUser) {
                    //builder.include(currenLocation);
                    Log.d("lam", "user " + users.getUsername());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
                    bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
                }

                try {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);


                    //mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                } catch (Exception e) {
                }
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.startService(new Intent(MainActivity.this, LocationService.class));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        arrUser = new ArrayList<>();

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                setAuthenticatedUser(firebaseAuth);
            }

        };


        // load nav menu header data

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        }

        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    Log.d("lam", "addListenerForSingleValueEvent");
                    if (!child.getKey().equals(currenUserId)) {
                        Log.d("lam", "user " + user.getUsername());
                        user.setUid(child.getKey());
                        arrUser.add(user);
                    } else {
                        user.setUid(dataSnapshot.getKey());
                        currenUser = user;


                    }

                }

                mMap.clear();
                bounds = new LatLngBounds.Builder();

                Log.d("lam", "SE EJECUTA ON POST");
                for (User users : arrUser) {
                    //builder.include(currenLocation);
                    Log.d("lam", "user " + users.getUsername());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
                    bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
                }

                try {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);


                    //mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    if (child.getKey().equals(currenUserId)) {



                    } else {
                        for (int i = 0; i < arrUser.size(); i++) {
                            if (child.getKey() == arrUser.get(i).getUid()) {
                                User user = child.getValue(User.class);
                                Log.d("lam", "ES IGUAL");
                                Log.d("lam", "usuario"+arrUser.get(i).getUsername()+"lat "+user.getLatitude());
                                arrUser.get(i).setLatitude(user.getLatitude());
                                arrUser.get(i).setLongitude(user.getLongitude());


                            }


                        }
                    }
                }

                mMap.clear();
                bounds = new LatLngBounds.Builder();

                for (User users : arrUser) {
                    //builder.include(currenLocation);
                    Log.d("lam", "usuario"+users.getUsername()+"lat "+ users.getLatitude());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
                    bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
                }


                try {
                   // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                    //CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currenUser.getLatitude(), currenUser.getLongitude()));

                   mMap.moveCamera(center);

                    //mMap.moveCamera(center);
                    // mMap.animateCamera(zoom);
                } catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setAuthenticatedUser(FirebaseAuth authData) {
        mAuthData = authData;
        if (authData != null) {
            FirebaseUser user = authData.getCurrentUser();
            currenUserId = user.getUid();
            currenUser = new User(user);
            Log.d("lam", "SE ESTA EJECUTANDO GETALLUSERS" + currenUser.getUid());
            // load nav menu header data
            loadNavHeader(currenUser);

            // initializing navigation menu
            setUpNavigationView();
        } else

        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }


    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader(User user) {
        // name, website
        txtName.setText(user.getUsername());
        txtWebsite.setText(user.getEmail());

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(user.getPhotoUrl())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
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

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:


            case 1:
                // photos
                PhotosFragment photosFragment = new PhotosFragment();
                return photosFragment;
            case 2:
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                // Gson gson = new Gson();
                // User puser = arrUser.get(0);
                // String jsonUsers = gson.toJson(arrUser);
//            intent.putExtra("key_send_user", gson.toJson(arrUser).toString() + "---" + gson.toJson(currenUser).toString());
                //  intent.putExtra("key_send_user", jsonUsers );
                intent.putParcelableArrayListExtra(PAR_KEY, arrUser);
                intent.putExtra(CURRENT_KEY, currenUser);
                startActivity(intent);

            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
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

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
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

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            //------------------------------------------------------------------------------
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                            "permission was granted, :)",
                            Toast.LENGTH_LONG).show();
                    getMyLocation();

                } else {
                    Toast.makeText(MainActivity.this,
                            "permission denied, ...:(",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("lam", "se ejecuta onMapReady");
        mMap = googleMap;
        Handler handler = new Handler();


        bounds = new LatLngBounds.Builder();

        for (User users : arrUser) {
            //builder.include(currenLocation);
            Log.d("lam", "user " + users.getUsername());
            mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
            bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
        }

        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);


            //mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        } catch (Exception e) {
        }
    }







    public void renderMap() {

        mMap.clear();
        bounds = new LatLngBounds.Builder();
        for (User users : arrUser) {
            //builder.include(currenLocation);
            mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
            bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
        }

        try {
            // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
            //CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

            // mMap.animateCamera(zoom);
        } catch (Exception e) {
        }


    }

}

