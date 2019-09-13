package official.com.savelife_blooddonor.Util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;
import official.com.savelife_blooddonor.Network.IGoogleAPIService;
import official.com.savelife_blooddonor.Network.Model.MyPlaces;
import official.com.savelife_blooddonor.Network.Model.Results;
import official.com.savelife_blooddonor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppConstants {

    public static int LOC_PERMISSION_CODE = 1001;
    public static String TAG = "AppConstants";

    /**
     * Changes the System Bar Theme.
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    public static void NearBy(Context context, final GoogleMap mMap, IGoogleAPIService mService, double latitude, double longitude, final String placeType) {
        String url = getUrl(context, latitude, longitude, placeType);
        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                Log.e("test", response.body().getResults()[i].toString());
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                if (placeType.equals("bloodbank")) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blood_bank));
                                } else {
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }
                                mMap.addMarker(markerOptions);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                    }
                });

    }

//    public static void NearByDonors(GoogleMap mMap, double latitude, double longitude) {
//        List<String> DonorKeys = new ArrayList<>();
//        List<Marker> DonorMarkerList = new ArrayList<>();
//        DatabaseReference DonorDatabaseRefrence = FirebaseDatabase.getInstance().getReference("DonorLocation");
//        GeoFire geoFire = new GeoFire(DonorDatabaseRefrence);
//        GeoQuery query = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), RADIUS);
//        query.removeAllListeners();
//        query.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(final String key, final GeoLocation location) {
//                Log.e(TAG, "key=" + key);
//                for (Marker markerIt : DonorMarkerList) {
//                    if (markerIt.getTag().equals(key)) {
//                        return;
//                    }
//                }
//                if (!DonorDuplicateKey.contentEquals(key)) {
//                    Log.e(TAG, key);
////                    Marker marker = mMap.addMarker(new MarkerOptions()
////                            .position(new LatLng(location.latitude, location.longitude))
////                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood_bank))
////                            .title("Donor"));
////                    marker.setTag(key);
////                    DonorMarkerList.add(marker);
//                    DonorDuplicateKey = key;
//                    DonorKeys.add(key);
//                }
//                Log.e(TAG, "On key called");
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//                for (Marker markerIt : DonorMarkerList) {
//                    if (markerIt.getTag().equals(key)) {
//                        //TODO: should key removed if driver not exists then send notification or not ?
//                        markerIt.remove();
//                        DonorMarkerList.remove(markerIt);
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//                for (Marker markerIt : DonorMarkerList) {
//                    if (markerIt.getTag().equals(key)) {
//                        animateMarker(mMap, markerIt, new LatLng(location.latitude, location.longitude), false);
////                            markerIt.setPosition(new LatLng(location.latitude, location.longitude));
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                RADIUS++;
//                if (RADIUS <= 200) {
//                    NearByDonors(mMap, latitude, longitude);
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//
//    }

    public static void LoadBloodDonorsByBloodGroup(GoogleMap mMap, String bgroup) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Donor");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map snap_info = (Map) snap.getValue();
                        String str_bgroup = snap_info.get("bgroup").toString();
                        if (bgroup.contentEquals(str_bgroup)) {
                            Log.e(TAG,snap.getKey());
                            Double latitude = Double.parseDouble(snap_info.get("latitude").toString());
                            Double longtitude = Double.parseDouble(snap_info.get("longtitude").toString());
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longtitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood_bank))
                                    .title("Donor"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private static String getUrl(Context context, double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + context.getResources().getString(R.string.google_maps_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();

    }

    public static boolean checkLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(((Activity) context), Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(((Activity) context), new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION

                }, LOC_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION

                }, LOC_PERMISSION_CODE);
            return false;

        } else
            return true;

    }

    public static boolean isValidEmailAddress(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidNumberAcceptPtcl(String number) {

        if (TextUtils.isEmpty(number)) {
            Log.e(TAG, "Enter number");
            return false;
        }
        if (!number.startsWith("0")) {
            Log.e(TAG, "Enter number which start with 0");
            return false;
        }
        if ((!number.startsWith("03")) && (number.length() < 10)) {
            Log.e(TAG, "Enter number which start with 03");
            return false;
        }
        if ((number.startsWith("03")) && (number.length() < 11)) {
            Log.e(TAG, "Enter full number");
            return false;
        }
        if (number.length() < 10) {
            Log.e(TAG, "Invalid number");
            return false;
        }
        return true;
    }

    public static void animateMarker(GoogleMap mMap, final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 10);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public static Dialog onCreateDialog(Context context, int layout, boolean cancelable) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height=metrics.heightPixels;
        Dialog dialog = new Dialog(context,android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout((6*width)/7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(cancelable);
        return dialog;
    }


    public static SharedPreferences getSharedPref(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getSharedPrefEditor(Context context, String name){
        SharedPreferences.Editor editor = getSharedPref(context,name).edit();
        return editor;
    }

    public static String getRole(Context context, String name){
        return getSharedPref(context,name).getString("role","");
    }

}
