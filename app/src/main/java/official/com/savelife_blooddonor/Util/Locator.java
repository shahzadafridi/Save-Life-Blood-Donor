package official.com.savelife_blooddonor.Util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class Locator implements LocationListener {

    static Context context;
    static LocationManager locationManager;
    String TAG = "Locator";
    static Locator locator;


    public static Locator getInstance(Context mContext, LocationManager mLocationManager) {
        context = mContext;
        locationManager = mLocationManager;
        if (locator == null) {
            locator = new Locator();
        }
        return locator;
    }

    public Location getLastKnownLocation() {
        final long MIN_DISTANCE_FOR_UPDATE = 10, MIN_TIME_FOR_UPDATE = 1000 * 60;
        Location bestLocation = null;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            if (AppConstants.checkLocationPermission(context)) {
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                List<String> providers = locationManager.getProviders(true);
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                    }
                }
            }
        } else {
            Log.e(TAG, "Gps is not enable");
        }
        return bestLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
