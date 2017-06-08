package info.androidhive.navigationdrawer.activity;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import info.androidhive.navigationdrawer.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.DirectionFrament;
import info.androidhive.navigationdrawer.fragment.HomeFragment;
import info.androidhive.navigationdrawer.other.User;


import info.androidhive.navigationdrawer.R;

/**
 * Created by skyrreasure on 12/5/16.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,RoutingListener {

    private GoogleMap mMap;
    private LatLng currenLocation;
    private LatLng friendLocation;
    private ArrayList<User> friendUser;
    private User currenUser;
    private DatabaseReference mDatabase;
    private LatLngBounds.Builder bounds;
    List<LatLng> locations;
    Button requestButton;
    private CardView directionView;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        requestButton = (Button) findViewById(R.id.requestButton);
        directionView = (CardView) findViewById(R.id.cardview);
        directionView.setVisibility(directionView.GONE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        friendUser = getIntent().getParcelableArrayListExtra(MainActivity.PAR_KEY);
        currenUser = getIntent().getParcelableExtra(MainActivity.CURRENT_KEY);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        locations = new ArrayList<>();
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // seVisible();
                finish();


            }

        });
    }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
   /* public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34.524303, -58.491549);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/

        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    bounds = new LatLngBounds.Builder();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(currenUser.getLatitude(), currenUser.getLongitude())));


                    bounds.include(new LatLng(currenUser.getLatitude(), currenUser.getLongitude()));
                    for (User users : friendUser) {
                        //builder.include(currenLocation);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
                        bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
                    }

                    try {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currenUser.getLatitude(), currenUser.getLongitude()));


                        //mMap.moveCamera(center);
                        mMap.animateCamera(zoom);
                    } catch (Exception e) {
                    }
                }
            }, 500);
            for (User users : friendUser) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(users.getUid());

                mDatabase.addValueEventListener(valueEventListenerFriendUser);
            }
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currenUser.getUid());
            mDatabase.addValueEventListener(valueEventListenerFriendUser);
        }

        private ValueEventListener valueEventListenerFriendUser = new ValueEventListener() {


            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (dataSnapshot.getKey() == currenUser.getUid()) {

                    currenUser.setLatitude(user.getLatitude());
                    currenUser.setLongitude(user.getLongitude());


                } else {
                    for (int i = 0; i < friendUser.size(); i++) {
                        if (dataSnapshot.getKey() == friendUser.get(i).getUid()) {
                            Log.d("lam", "ES IGUAL");
                            friendUser.get(i).setLatitude(user.getLatitude());
                            friendUser.get(i).setLongitude(user.getLongitude());


                        }


                    }
                }

                renderMap();


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        @Override
        public void onRoutingFailure () {

        }

        @Override
        public void onRoutingStart () {

        }

        @Override
    /*public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        try {
            mMap.clear();
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(Color.BLUE);
            polyoptions.width(10);
            polyoptions.addAll(polylineOptions.getPoints());

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
               // builder.include(currenLocation);

                builder.include(friendLocation);
                LatLngBounds bounds = builder.build();
                try {
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngBounds(bounds, 80));
                } catch (Exception e) {
                }
                mMap.addPolyline(polyoptions);

            String distance = route.getDistanceText();
           //mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MapsActivity.this));

           // Marker markerB = mMap.addMarker(new MarkerOptions().position(friendLocation)
           //         .title(friendUser.name + "-" + distance).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
         //   markerB.showInfoWindow();
        } catch (Exception e) {
        }
    }*/
        public void onRoutingSuccess (PolylineOptions polylineOptions, Route route){

            mMap.clear();


            bounds = new LatLngBounds.Builder();
            for (LatLng latLng : locations) {
                // builder.include(currenLocation);
                mMap.addMarker(new MarkerOptions().position(latLng));
                bounds.include(latLng);
            }

            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                // CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude));

                //mMap.moveCamera(center);
                mMap.animateCamera(zoom);
                // mMap.addPolyline(polyoptions);
            } catch (Exception e) {
            }


        }

        @Override
        public void onRoutingCancelled () {

        }

    public void renderMap() {

        mMap.clear();
        bounds = new LatLngBounds.Builder();
        mMap.addMarker(new MarkerOptions().position(new LatLng(currenUser.getLatitude(), currenUser.getLongitude())));
        bounds.include(new LatLng(currenUser.getLatitude(), currenUser.getLongitude()));
        for (User users : friendUser) {
            //builder.include(currenLocation);
            mMap.addMarker(new MarkerOptions().position(new LatLng(users.getLatitude(), users.getLongitude())));
            bounds.include(new LatLng(users.getLatitude(), users.getLongitude()));
        }

        try {
            // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
            //CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currenUser.getLatitude(), currenUser.getLongitude()));

            mMap.moveCamera(center);
            // mMap.animateCamera(zoom);
        } catch (Exception e) {
        }


    }
    public void seVisible()
    {
        directionView.setVisibility(View.VISIBLE);
    }
}



