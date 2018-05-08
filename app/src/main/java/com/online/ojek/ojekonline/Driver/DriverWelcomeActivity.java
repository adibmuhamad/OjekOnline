package com.online.ojek.ojekonline.Driver;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.online.ojek.ojekonline.Common.Common;
import com.online.ojek.ojekonline.Model.Token;
import com.online.ojek.ojekonline.R;
import com.online.ojek.ojekonline.remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DriverWelcomeActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    Marker mCurrent;
    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;

    private List<LatLng> polyLineList;
    private Marker ojekMarker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition, currentPosition;
    private int index, next;
    //private Button btnGo;
    private PlaceAutocompleteFragment places;
    private String destination;
    private PolylineOptions polylineOptions, blackPolyLineOptions;
    private Polyline blackPolyLine, greyPolyLine;

    private IGoogleAPI mServices;

    DatabaseReference onlineRef, currentUserRef;

    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < polyLineList.size() - 1) {
                startPosition = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v*endPosition.longitude+(1-v)*startPosition.longitude;
                    lat = v*endPosition.latitude+(1-v)*startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    ojekMarker.setPosition(newPos);
                    ojekMarker.setAnchor(0.5f, 0.5f);
                    ojekMarker.setRotation(getBearing(startPosition, newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build()
                    ));
                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 3000);
        }
    };

            private float getBearing(LatLng startPosition, LatLng endPosition) {
                double lat = Math.abs(startPosition.latitude - endPosition.latitude);
                double lng = Math.abs(startPosition.longitude - endPosition.longitude);

                if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
                    return (float) (Math.toDegrees(Math.atan(lng/lat)));
                else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
                    return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+90);
                else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
                    return (float) (Math.toDegrees(Math.atan(lng/lat))+180);
                else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
                    return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+270);
                return -1;
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_driver_welcome);
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapDriver);
                mapFragment.getMapAsync(this);

                onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
                currentUserRef = FirebaseDatabase.getInstance().getReference(Common.driver_tbl)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                onlineRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserRef.onDisconnect().removeValue();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                location_switch = (MaterialAnimatedSwitch)findViewById(R.id.location_switch);
                location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isOnline) {
                        if(isOnline){
                            FirebaseDatabase.getInstance().goOnline();
                            startLocationUpdate();
                            displayLocation();
                            Snackbar.make(mapFragment.getView(), "You are online", Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        else
                        {
                            FirebaseDatabase.getInstance().goOffline();
                            stopLocationUpdate();
                            mCurrent.remove();
                            mMap.clear();
                            if(destination != null){
                                handler.removeCallbacks(drawPathRunnable);
                            }
                            Snackbar.make(mapFragment.getView(), "You are offline", Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

                polyLineList = new ArrayList<>();

                places =(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        if(location_switch.isChecked()){
                            destination = place.getAddress().toString();
                            destination = destination.replace(" ","+");

                            getDirection();
                        }
                        else {
                            Toast.makeText(DriverWelcomeActivity.this,"Please change your status to ONLINE", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Status status) {
                        Toast.makeText(DriverWelcomeActivity.this, ""+status.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                drivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
                geoFire = new GeoFire(drivers);
                setUpLocation();

                mServices = Common.getGoogleAPI();

                updateFirebaseToken();
            }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
    }

    private void getDirection() {
                currentPosition = new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude());
                String requestAPI = null;
                try{
                    requestAPI = "https://maps.googleapis.com/maps/api/directions/json?"+
                            "mode=driving&"+
                            "transit_routing_preference=less_driving&"+
                            "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                            "destination="+destination+"&"+
                            "key="+getResources().getString(R.string.google_direction_api);
                    Log.d("OjekOnline",requestAPI);
                    mServices.getPath(requestAPI)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                        for(int i = 0; i<jsonArray.length(); i++){
                                            JSONObject route = jsonArray.getJSONObject(i);
                                            JSONObject poly = route.getJSONObject("overview_polyline");
                                            String polyline = poly.getString("points");
                                            polyLineList = decodePoly(polyline);
                                        }
                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        for(LatLng latLng:polyLineList)
                                            builder.include(latLng);
                                        LatLngBounds bounds = builder.build();
                                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                        mMap.animateCamera(mCameraUpdate);

                                        polylineOptions = new PolylineOptions();
                                        polylineOptions.color(Color.GRAY);
                                        polylineOptions.width(5);
                                        polylineOptions.startCap(new SquareCap());
                                        polylineOptions.endCap(new SquareCap());
                                        polylineOptions.jointType(JointType.ROUND);
                                        polylineOptions.addAll(polyLineList);
                                        greyPolyLine = mMap.addPolyline(polylineOptions);

                                        blackPolyLineOptions= new PolylineOptions();
                                        blackPolyLineOptions.color(Color.BLACK);
                                        blackPolyLineOptions.width(5);
                                        blackPolyLineOptions.startCap(new SquareCap());
                                        blackPolyLineOptions.endCap(new SquareCap());
                                        blackPolyLineOptions.jointType(JointType.ROUND);
                                        blackPolyLine = mMap.addPolyline(blackPolyLineOptions);

                                        mMap.addMarker(new MarkerOptions()
                                                .position(polyLineList.get(polyLineList.size()-1))
                                                .title("Pickup Location"));

                                        ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0,100);
                                        polyLineAnimator.setDuration(2000);
                                        polyLineAnimator.setInterpolator(new LinearInterpolator());
                                        polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                List<LatLng> points = greyPolyLine.getPoints();
                                                int percentValue = (int)valueAnimator.getAnimatedValue();
                                                int size = points.size();
                                                int newPoints = (int)(size * (percentValue/100.0f));
                                                List<LatLng> p = points.subList(0, newPoints);
                                                blackPolyLine.setPoints(p);
                                            }
                                        });
                                        polyLineAnimator.start();

                                        ojekMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                                .flat(true)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.motor)));

                                        handler = new Handler();
                                        index=-1;
                                        next=1;
                                        handler.postDelayed(drawPathRunnable, 3000);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(DriverWelcomeActivity.this, ""+t.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            private List decodePoly(String encoded) {

                List poly = new ArrayList();
                int index = 0, len = encoded.length();
                int lat = 0, lng = 0;

                while (index < len) {
                    int b, shift = 0, result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lat += dlat;

                    shift = 0;
                    result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

                    LatLng p = new LatLng((((double) lat / 1E5)),
                            (((double) lng / 1E5)));
                    poly.add(p);
                }

                return poly;
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                switch (requestCode){
                    case MY_PERMISSION_REQUEST_CODE:
                        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            if(checkPlayServices()){
                                buildGoogleApiClient();
                                createLocationRequest();
                                if(location_switch.isChecked())
                                    displayLocation();
                            }
                        }
                }

            }

            private void setUpLocation() {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, MY_PERMISSION_REQUEST_CODE);
                }
                else {
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        if(location_switch.isChecked())
                            displayLocation();
                    }
                }
            }

            private void createLocationRequest() {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setFastestInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FATEST_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
            }

            private void buildGoogleApiClient() {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }

            private boolean checkPlayServices() {
                int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if(resultCode != ConnectionResult.SUCCESS){
                    if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                        GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
                    else{
                        Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return false;
                }
                return true;
            }

            private void stopLocationUpdate() {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            }

            private void displayLocation() {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
                Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if(Common.mLastLocation != null){
                    if(location_switch.isChecked()){
                        final double latitude = Common.mLastLocation.getLatitude();
                        final double longitude = Common.mLastLocation.getLongitude();

                        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if(mCurrent != null){
                                    mCurrent.remove();
                                }
                                mCurrent = mMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(latitude,longitude))
                                                                .title("Your Location"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15.0f));
                                //rotateMaker(mCurrent, -360, mMap);

                            }
                        });
                    }
                }
                else{
                    Log.d("ERROR", "Cannot get your location ");
                }

            }

            private void rotateMaker(final Marker mCurrent, final float i, GoogleMap mMap) {
                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                final float startRotation = mCurrent.getRotation();
                final long duration = 1500;

                final Interpolator interpolator = new LinearInterpolator();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = interpolator.getInterpolation((float)elapsed/duration);
                        float rot = t*i+(1-t)*startRotation;
                        mCurrent.setRotation(-rot > 180?rot/2:rot);
                        if(t<1.0){
                            handler.postDelayed(this, 16);
                        }
                    }
                });
            }

            private void startLocationUpdate() {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            }


            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setTrafficEnabled(false);
                mMap.setIndoorEnabled(false);
                mMap.setBuildingsEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            }


            @Override
            public void onConnected(@Nullable Bundle bundle) {
                displayLocation();
                startLocationUpdate();
            }

            @Override
            public void onConnectionSuspended(int i) {
                mGoogleApiClient.connect();
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            public void onLocationChanged(Location location) {
                Common.mLastLocation = location;
                displayLocation();
            }
        }
