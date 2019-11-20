package official.com.savelife_blooddonor.Screens;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import official.com.savelife_blooddonor.Network.IGoogleAPIService;
import official.com.savelife_blooddonor.Network.RetrofitConstant;
import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Util.AppConstants;

public class ProfileMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,GoogleMap.OnMapLongClickListener,View.OnClickListener {

    private GoogleMap mMap;
    double lat, lon;
    String phone;
    Marker user_marker;
    LatLng mLatlng,selectLatlng;
    BottomSheetDialog locationBottomSheet;
    TextView tvLat,tvLon,tvAddress;
    Button done;
    FrameLayout pinViewLine,pinViewCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View dialogView = getLayoutInflater().inflate(R.layout.seleted_location_bottom_sheet, null);
        locationBottomSheet = new BottomSheetDialog(this);
        locationBottomSheet.setContentView(dialogView);
        tvLat = (TextView) locationBottomSheet.findViewById(R.id.selected_location_bottom_sheet_lat);
        tvLon = (TextView) locationBottomSheet.findViewById(R.id.selected_location_bottom_sheet_lon);
        tvAddress = (TextView) locationBottomSheet.findViewById(R.id.selected_location_bottom_sheet_address);
        done = (Button) locationBottomSheet.findViewById(R.id.selected_location_bottom_sheet_done);
        done.setOnClickListener(this);

        pinViewLine = (FrameLayout) findViewById(R.id.pin_view_line);
        pinViewCircle = (FrameLayout) findViewById(R.id.pin_view_circle);
        pinViewCircle.setVisibility(View.GONE);
        pinViewLine.setVisibility(View.GONE);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        lat = Double.parseDouble(getIntent().getStringExtra("lat"));
        lon = Double.parseDouble(getIntent().getStringExtra("lon"));
        phone = getIntent().getStringExtra("phone");

        mLatlng = new LatLng(lat,lon);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        selectLatlng = latLng;
        //Bottom Sheet for select locaiton method
        tvLon.setText(String.valueOf(latLng.longitude));
        tvLat.setText(String.valueOf(latLng.latitude));
        tvAddress.setText(getAddress(latLng));
        locationBottomSheet.show();

    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address = "",city,state,country,postalCode,knownName;

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName(); //
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void updateLocation(double latitude, double longitude) {
        Map<String, Object> map = new HashMap<>();
        map.put("latitude", latitude);
        map.put("longtitude", longitude);
        FirebaseDatabase.getInstance().getReference("Donor").child(phone).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileMapActivity.this,"Location updated successfully",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileMapActivity.this,"Location update failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        locationBottomSheet.hide();
//     updateLocation(latLng.latitude,latLng.longitude);
        Intent intent = new Intent();
        intent.putExtra("lat",selectLatlng.latitude);
        intent.putExtra("lon",selectLatlng.longitude);
        setResult(RESULT_OK,intent);
        finish();
    }
}
