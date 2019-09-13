package official.com.savelife_blooddonor.Screens;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.Network.IGoogleAPIService;
import official.com.savelife_blooddonor.Network.RetrofitConstant;
import official.com.savelife_blooddonor.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap mMap;
    FusedLocationProviderClient locationProviderClient, getLastLocationFromLocationProvider;
    LocationRequest locationRequest;
    Marker user_marker;
    Location mLocation;
    LatLng mLatlng;
    String TAG = "MapsActivity";
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 15000; /* 15 sec */
    IGoogleAPIService mService;
    boolean isNearByFired = false;
    public int RADIUS = 1;
    public String DonorDuplicateKey = "";
    List<String> DonorKeys = new ArrayList<>();
    List<Marker> DonorMarkerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mService = RetrofitConstant.getGoogleAPIService();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (AppConstants.checkLocationPermission(MapsActivity.this)) {
            mMap.setMyLocationEnabled(true);
            locationProviderClient.requestLocationUpdates(locationRequest, mLocationCallBack, Looper.myLooper());
            getLastLocationFromLocationProvider = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
            getLastLocationFromLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (user_marker != null)
                            user_marker.remove();
                        user_marker = mMap.addMarker(new MarkerOptions().position(mLatlng).title("My Location"));
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(mLatlng)
                                .zoom(16)
//                                .bearing(90)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                    }
                }
            });
        } else {
            Log.e(TAG, "Location permission not granted.");
        }

        if (getIntent() != null){
            String type = getIntent().getStringExtra("type");
            if (!TextUtils.isEmpty(type) && type.contentEquals("single_bloodgroup")){
                String bgroup = getIntent().getStringExtra("bgroup");
                AppConstants.LoadBloodDonorsByBloodGroup(mMap,bgroup);
            }
        }

    }


    LocationCallback mLocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLocation = locationResult.getLastLocation();
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    if (location.getAccuracy() <= 50.0f && System.currentTimeMillis() - location.getTime() <= 180000) {
                        if (user_marker != null)
                            user_marker.remove();
                        mLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                        user_marker = mMap.addMarker(new MarkerOptions().position(mLatlng).title("My Location"));
                        if (!isNearByFired){
                            String type = getIntent().getStringExtra("type");
                            if (!TextUtils.isEmpty(type)){
                                if (type.contentEquals("nearby_bloodbank")){
                                    if (mLocation != null) {
                                        AppConstants.NearBy(MapsActivity.this, mMap, mService,mLocation.getLatitude(),mLocation.getLongitude(),"bloodbank");
                                        isNearByFired = true;
                                    }
                                } else  if (type.contentEquals("nearby_donor")){
                                    if (mLocation != null) {
                                        NearByDonors(mMap,mLocation.getLatitude(),mLocation.getLongitude());
                                        isNearByFired = true;
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "location is not correct");
                    }
                    Log.e(TAG, "location updated.");
                }
            }

        }
    };

    @Override
    public void onClick(View view) {

    }

    public void NearByDonors(GoogleMap mMap, double latitude, double longitude) {
        DatabaseReference DonorDatabaseRefrence = FirebaseDatabase.getInstance().getReference("DonorLocation");
        GeoFire geoFire = new GeoFire(DonorDatabaseRefrence);
        GeoQuery query = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), RADIUS);
        query.removeAllListeners();
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                for (Marker markerIt : DonorMarkerList) {
                    if (markerIt.getTag().equals(key)) {
                        return;
                    }
                }
                if (!DonorDuplicateKey.contentEquals(key)) {
                    Log.e(TAG, key);
//                    Marker marker = mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(location.latitude, location.longitude))
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood_bank))
//                            .title("Donor"));
//                    marker.setTag(key);
//                    DonorMarkerList.add(marker);
                    DonorDuplicateKey = key;
                    DonorKeys.add(key);
                }
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : DonorMarkerList) {
                    if (markerIt.getTag().equals(key)) {
                        //TODO: should key removed if driver not exists then send notification or not ?
                        markerIt.remove();
                        DonorMarkerList.remove(markerIt);
                        return;
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : DonorMarkerList) {
                    if (markerIt.getTag().equals(key)) {
                        AppConstants.animateMarker(mMap, markerIt, new LatLng(location.latitude, location.longitude), false);
//                            markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                        return;
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
                RADIUS++;
                if (RADIUS <= 200) {
                    NearByDonors(mMap, latitude, longitude);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
