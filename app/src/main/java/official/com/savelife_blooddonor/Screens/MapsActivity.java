package official.com.savelife_blooddonor.Screens;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.Network.IGoogleAPIService;
import official.com.savelife_blooddonor.Network.RetrofitConstant;
import official.com.savelife_blooddonor.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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
    Marker marker = null;
    String getPhone = "";
    BottomSheetDialog dialog;
    private FrameLayout frameLayout, circleFrameLayout, pinFrameLayout;
    private ProgressBar progress;
    private TextView textView;
    private int circleRadius;
    private boolean isFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initViews();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mService = RetrofitConstant.getGoogleAPIService();
        getPhone = AppConstants.getPhone(this, "SESSION");
    }

    /*
     * Circle Marker
     * */

    private void initViews() {
        frameLayout = (FrameLayout) findViewById(R.id.map_container);
        circleFrameLayout = (FrameLayout) findViewById(R.id.pin_view_circle);
        pinFrameLayout = (FrameLayout) findViewById(R.id.pin_view_line);
        textView = (TextView) findViewById(R.id.textView);
        progress = (ProgressBar) findViewById(R.id.profile_loader);
    }

    private void hideShowCircleMarker() {
        progress.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        Drawable mDrawable;
        if (Build.VERSION.SDK_INT >= 21)
            mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.circle_background, null);
        else
            mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.circle_background);
        circleFrameLayout.setBackground(mDrawable);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                circleFrameLayout.setVisibility(View.GONE);
                pinFrameLayout.setVisibility(View.GONE);
            }
        }, 5000);
    }

    private void showCircleMarker() {
        textView.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        Drawable mDrawable;
        if (Build.VERSION.SDK_INT >= 21)
            mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.circle_background_moving, null);
        else
            mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.circle_background_moving);

        circleFrameLayout.setBackground(mDrawable);
    }

    //resing circle pin
    private void resizeLayout(boolean backToNormalSize){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) circleFrameLayout.getLayoutParams();

        ViewTreeObserver vto = circleFrameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                circleFrameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                circleRadius = circleFrameLayout.getMeasuredWidth();
            }
        });

        if (backToNormalSize) {
            params.width = WRAP_CONTENT;
            params.height = WRAP_CONTENT;
            params.topMargin = 0;

        } else {
            params.topMargin = (int) (circleRadius * 0.3);
            params.height = circleRadius - circleRadius / 3;
            params.width = circleRadius - circleRadius / 3;
        }

        circleFrameLayout.setLayoutParams(params);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showCircleMarker();
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

        if (getIntent() != null) {
            String type = getIntent().getStringExtra("type");
            if (!TextUtils.isEmpty(type) && type.contentEquals("single_bloodgroup")) {
                String bgroup = getIntent().getStringExtra("bgroup");
                AppConstants.LoadBloodDonorsByBloodGroup(mMap, bgroup);
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
                        if (!isNearByFired) {
                            String type = getIntent().getStringExtra("type");
                            if (!TextUtils.isEmpty(type)) {
                                if (type.contentEquals("nearby_bloodbank")) {
                                    if (mLocation != null) {
                                        AppConstants.NearBy(MapsActivity.this, mMap, mService, mLocation.getLatitude(), mLocation.getLongitude(), "bloodbank");
                                        isNearByFired = true;
                                    }
                                } else if (type.contentEquals("nearby_donor")) {
                                    if (mLocation != null) {
                                        NearByDonors(mMap, mLocation.getLatitude(), mLocation.getLongitude());
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

    public void NearByDonors(GoogleMap mMap, double latitude, double longitude) {
        Log.e("NearBy", "NearByDonors called");
        DatabaseReference DonorDatabaseRefrence = FirebaseDatabase.getInstance().getReference("DonorLocation");
        GeoFire geoFire = new GeoFire(DonorDatabaseRefrence);
        GeoQuery query = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), RADIUS);
        query.removeAllListeners();
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                Log.e("NearBy", "onKeyEntered called");
//                for (Marker markerIt : DonorMarkerList) {
//                    if (markerIt.getTag().equals(key)) {
//                        Log.e("NearBy","======================> Marker key:"+key);
//                        return;
//                    }
//                }

                if (!DonorDuplicateKey.contentEquals(key)) {
                    Log.e("NearBy", "Marker location - Lat:" + location.latitude + " , Lon:" + location.longitude);
                    Log.e("NearBy", "Key:" + key);
                    if (!TextUtils.isEmpty(getPhone) && getPhone.contentEquals(key)) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.donor_icon))
                                    .title(key));

                            marker.setTag(key);
                            DonorMarkerList.add(marker);
                            DonorDuplicateKey = key;
                            DonorKeys.add(key);

                            if (!isFound){
                                hideShowCircleMarker();
                                isFound = true;
                            }
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {
                Log.e("NearBy", "onKeyExited:" + key);
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
                Log.e("NearBy", "onKeyMoved:" + key);
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
                if (RADIUS <= 2) {
                    Log.e("NearBy", "onGeoQueryReady - Radius:" + RADIUS);
                    NearByDonors(mMap, latitude, longitude);
                }else {
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("NearBy", "onGeoQueryError - error:" + error.getMessage());
            }
        });

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("NearBy", "onMarkerClick - Key =======================> " + marker.getTag());
        DatabaseReference DonorDatabaseRefrence = FirebaseDatabase.getInstance().getReference("Donor");
        DonorDatabaseRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            if (snap.getKey().contentEquals(marker.getTitle())) {
                                Map map = (HashMap) snap.getValue();
                                String age = map.get("age").toString();
                                String bgroup = map.get("bgroup").toString();
                                String email = map.get("email").toString();
                                String gender = map.get("gender").toString();
                                String lat = String.valueOf(map.get("latitude"));
                                String lon = String.valueOf(map.get("longtitude"));
                                String name = map.get("name").toString();
                                String phone = map.get("phone").toString();
                                showBottomSheet(name, phone, gender, bgroup, age, email, lat, lon);
                                Log.e("donor", marker.getTitle());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return false;
    }

    String phone = "";

    public void showBottomSheet(String str_name, String str_phone, String str_gender, String str_bgroup, String str_age, String str_email, String str_lat, String str_lon) {
        phone = str_phone;
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_item_layout, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        TextView name = (TextView) dialog.findViewById(R.id.profile_name);
        TextView age = (TextView) dialog.findViewById(R.id.profile_age);
        TextView bgroup = (TextView) dialog.findViewById(R.id.profile_bgroup);
        TextView gender = (TextView) dialog.findViewById(R.id.profile_gender);
        TextView email = (TextView) dialog.findViewById(R.id.profile_email);
        email.setText(str_email);
        name.setText(str_name);
        age.setText(str_age);
        bgroup.setText(str_bgroup);
        gender.setText(str_gender);
        Button sms = (Button) dialog.findViewById(R.id.sms);
        Button call = (Button) dialog.findViewById(R.id.call);
        Button back = (Button) dialog.findViewById(R.id.profile_back);
        sms.setOnClickListener(this);
        call.setOnClickListener(this);
        back.setOnClickListener(this);
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sms) {
            if (!TextUtils.isEmpty(phone)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
            } else {
                Toast.makeText(MapsActivity.this, "Mobile number not found.", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.call) {
            if (!TextUtils.isEmpty(phone)) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            } else {
                Toast.makeText(MapsActivity.this, "Mobile number not found.", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.profile_back) {
            dialog.hide();
        }
    }


}
