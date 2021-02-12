package com.example.halloworld.DesignV1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.halloworld.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Karte2 extends NavigationMenu implements OnMapReadyCallback {

    private static final String TAG = "Karte2";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private EditText mSearchText;
//    private ImageView mGps;
    Button mapStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte2);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("karte");
        super.setDrawerLayout(this, toolbar, R.id.nav_karte);
        mapStyle =findViewById(R.id.mapstyle);
        //mSearchText= findViewById(R.id.input_search);
//        mGps = findViewById(R.id.ic_gps);
        if (isServiceOK()) {
            getLocationPermission();
        }

        mapStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap.getMapType()==GoogleMap.MAP_TYPE_SATELLITE){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }

            }
        });
    }

    private void init(){
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId== EditorInfo.IME_ACTION_SEARCH
//                || actionId == EditorInfo.IME_ACTION_DONE
//                || event.getAction()== KeyEvent.ACTION_DOWN
//                || event.getAction()== KeyEvent.KEYCODE_ENTER){
//                    // execute our methode for searching
//                    geoLocate();
//                }
//                return false;
//            }
//        });

//        mGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getDeviceLocation();
//            }
//        });
//        hideSoftKeyboard();
    }

//    private void geoLocate(){
//        String searchString = mSearchText.getText().toString();
//
//        Geocoder geocoder = new Geocoder(this);
//        List<Address> list = new ArrayList<>();
//
//        try {
//            list= geocoder.getFromLocationName(searchString,1);
//
//        }catch(IOException e){
//            Log.d(TAG, "geoLocate: IOExeption:m "+ e.getMessage());
//        }
//
//        if(list.size()>0){
//            Address address = list.get(0);
//            Log.d(TAG, "geoLocate: Adress: "+address.toString());
//            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
//        }
//        hideSoftKeyboard();
//    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the current device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation: onComplete found location");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,"My Location");
                        } else {
                            Log.d(TAG, "getDeviceLocation: onComplete location is null");
                            Toast.makeText(Karte2.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException:" + e.getMessage());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            init();
        }
    }
    private void moveCamera(LatLng latLng,float zoom,String titel){
       Log.d(TAG,"moveCamera: moving the camera to lat: "+latLng.latitude+ " lng: "+latLng.longitude);
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(titel);
        mMap.addMarker(options);
        hideSoftKeyboard();
    }
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleKarte);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions= {FINE_LOCATION,COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted=true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: called");
        mLocationPermissionsGranted= false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length>0 ){
                    for( int i = 0; i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted=false;
                            Log.d(TAG,"onRequestPermissionsResult: permisson failed");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult: permisson granted");
                   mLocationPermissionsGranted=true;
                   //initialize our map
                    initMap();
                }
            }
        }
    }

    private boolean isServiceOK() {
        Log.d(TAG,"isserviceOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can male map requests
            Log.d(TAG,"isServiceOK: Google Play Services is working");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
            return false;
        }

    private void  hideSoftKeyboard(){

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}