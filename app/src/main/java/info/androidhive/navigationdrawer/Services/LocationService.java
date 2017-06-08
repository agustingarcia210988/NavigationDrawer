package info.androidhive.navigationdrawer.Services;

/**
 * Created by aggarcia on 1/9/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import de.greenrobot.event.EventBus;

/**
 * Created by MyPC on 20/04/2016.
 */
public class LocationService extends Service {
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private FirebaseUser currenUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static double lattitude=0;
    public static double longitude=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION");
        Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION2");
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION3");
        this.myLocationListener = new MyLocationListener();
        Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION4");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION5");
        currenUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currenUser != null) {
            Log.d("lam", "ACTIVO" + currenUser.getUid());
        } else {
            Log.d("lam", "no Activo");
        }
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(FirebaseAuth authData) {
                setAuthenticatedUser(authData);
            }
        };
       // mAuth = FirebaseAuth.getInstance();
        Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION6");
       // mAuth.addAuthStateListener(mAuthStateListener);
       // Log.d("lam", "SE ESTA EJECUTANDO EL SERVICIO LOCATION7" + currenUser.getUid());
       // mDatabase.child("users").child(currenUser.getUid()).child("longitude").setValue("1212");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("lam", "SE CUMPLE");


            return;
        }
        Log.d("lam", "NO SE CUMPLE");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, myLocationListener);


    }


    private void setAuthenticatedUser(FirebaseAuth authData) {
        if (authData != null) {
            currenUser=authData.getCurrentUser();
        } else {
            currenUser=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mAuth.removeAuthStateListener(mAuthStateListener);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(myLocationListener);

        } catch (Exception e) {
        }
    }
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            try {
                Log.d("lam", "SE ESTA EJECUTANDO ON LOCATION CHANGE"+currenUser.getUid());
                mDatabase.child("users").child(currenUser.getUid()).child("latitude").setValue(location.getLatitude());
                mDatabase.child("users").child(currenUser.getUid()).child("longitude").setValue(location.getLongitude());
            }catch (Exception e){}
            lattitude=location.getLatitude();
            longitude=location.getLongitude();
            //EventBus.getDefault().post(location);
            Log.d("lam", "onLocationChanged:lam "+location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    }

}
