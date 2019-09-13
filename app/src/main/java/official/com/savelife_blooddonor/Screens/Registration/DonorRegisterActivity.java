package official.com.savelife_blooddonor.Screens.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import official.com.savelife_blooddonor.Screens.MainActivity;
import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.R;

public class DonorRegisterActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    TextView back, Aplus, Aneg, Bplus, Bneg, Oplus, Oneg, ABplus, ABneg, male_lable, female_label, location_tv, click_location;
    EditText name, email, phone, age, password;
    ImageView male, female;
    Button done;
    String TAG = "DonorRegisterActivity";
    String str_blood_group = "O+"; // default blood group.
    ProgressBar progressBar;
    boolean isGenderSelected = false;
    String gender = "male";
    Location location;
    protected LocationManager locationManager;
    boolean isEmailExists = false;
    String str_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_register);
        InitUI();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        str_phone = getIntent().getStringExtra("phone");
    }

    private void InitUI() {

        back = (TextView) findViewById(R.id.reg_back);
        Aplus = (TextView) findViewById(R.id.reg_aplus_tv);
        Aneg = (TextView) findViewById(R.id.reg_anegtive_tv);
        Bplus = (TextView) findViewById(R.id.reg_bplus_tv);
        Bneg = (TextView) findViewById(R.id.reg_bnegtive_tv);
        Oplus = (TextView) findViewById(R.id.reg_oplus_tv);
        Oneg = (TextView) findViewById(R.id.reg_onegative_tv);
        ABplus = (TextView) findViewById(R.id.reg_ABplus_tv);
        ABneg = (TextView) findViewById(R.id.reg_ABnegtive_tv);
        name = (EditText) findViewById(R.id.reg_name);
        email = (EditText) findViewById(R.id.reg_email);
        phone = (EditText) findViewById(R.id.reg_phone);
        age = (EditText) findViewById(R.id.reg_age);
//        password = (EditText) findViewById(R.id.reg_password);
        male = (ImageView) findViewById(R.id.reg_male_iv);
        female = (ImageView) findViewById(R.id.reg_female_iv);
        done = (Button) findViewById(R.id.reg_done_btn);
        progressBar = (ProgressBar) findViewById(R.id.reg_progressBar);
        male_lable = (TextView) findViewById(R.id.reg_male_label);
        female_label = (TextView) findViewById(R.id.reg_female_label);
        location_tv = (TextView) findViewById(R.id.reg_location);
        click_location = (TextView) findViewById(R.id.reg_click_location);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        Aplus.setOnClickListener(this);
        Aneg.setOnClickListener(this);
        Bplus.setOnClickListener(this);
        Bneg.setOnClickListener(this);
        back.setOnClickListener(this);
        Oplus.setOnClickListener(this);
        Oneg.setOnClickListener(this);
        ABplus.setOnClickListener(this);
        ABneg.setOnClickListener(this);
        done.setOnClickListener(this);
        click_location.setOnClickListener(this);
        Oplus.setBackground(getResources().getDrawable(R.drawable.color_round));
//        phone.setText(str_phone);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.reg_male_iv) {
            isGenderSelected = true;
            gender = "male";
            female_label.setTextColor(getResources().getColor(R.color.black));
            male_lable.setTextColor(getResources().getColor(R.color.light_red));
        } else if (id == R.id.reg_female_iv) {
            isGenderSelected = true;
            gender = "female";
            male_lable.setTextColor(getResources().getColor(R.color.black));
            female_label.setTextColor(getResources().getColor(R.color.light_red));
        } else if (id == R.id.reg_aplus_tv) {
            str_blood_group = "A+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_anegtive_tv) {
            str_blood_group = "A-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_bplus_tv) {
            str_blood_group = "B+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_bnegtive_tv) {
            str_blood_group = "B-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_oplus_tv) {
            str_blood_group = "O+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_onegative_tv) {
            str_blood_group = "O-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_ABplus_tv) {
            str_blood_group = "AB+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_ABnegtive_tv) {
            str_blood_group = "AB-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.reg_click_location) {
            location = getLastKnownLocation();
            if (location != null) {
                location_tv.setText("Latitude:" + location.getLatitude() + " , Longtitude:" + location.getLongitude());
            } else {
                Toast.makeText(DonorRegisterActivity.this, "Location can't be null.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.reg_done_btn) {
            done.setText("");
            done.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            if (validation()) {
                String str_email = email.getText().toString();
                String str_phone = phone.getText().toString();
                checPhoneNumber(str_email, str_phone);
            } else {
                done.setText("Done");
                done.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(DonorRegisterActivity.this, "Incomplete details, failed to register", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checPhoneNumber(String str_email, String str_phone) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Donor");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    if (snapshot.hasChild(str_phone)) {
                        Toast.makeText(DonorRegisterActivity.this, "Phone number exists in database", Toast.LENGTH_SHORT).show();
                        done.setText("Done");
                        done.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        checkEmail(str_email);
                    }
                } else {
                    RegisterUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void checkEmail(String str_email) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Donor");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map snap_info = (Map) snap.getValue();
                        if (snap_info.get("email") != null) {
                            String email = snap_info.get("email").toString();
                            if (email.contentEquals(str_email)) {
                                isEmailExists = true;
                            } else {
                                isEmailExists = false;
                            }
                        } else {
                            isEmailExists = false;
                        }
                    }

                    if (!isEmailExists) {
                        RegisterUser();
                    } else {
                        Toast.makeText(DonorRegisterActivity.this, "Email exists in database", Toast.LENGTH_SHORT).show();
                        done.setText("Done");
                        done.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void RegisterUser() {

        String str_name = name.getText().toString();
        String str_email = email.getText().toString();
        String str_phone = phone.getText().toString();
        String str_age = age.getText().toString();
//        String str_password = password.getText().toString();
        String str_latituted = String.valueOf(location.getLatitude());
        String str_longtited = String.valueOf(location.getLongitude());

        Map<String, String> map = new HashMap<>();
        map.put("name", str_name);
        map.put("email", str_email);
//        map.put("password", str_password);
        map.put("phone", str_phone);
        map.put("gender", gender);
        map.put("age", str_age);
        map.put("bgroup", str_blood_group);
        map.put("latitude", str_latituted);
        map.put("longtitude", str_longtited);

        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Donor").child(str_phone);
        DatabaseReference donorLocRef = FirebaseDatabase.getInstance().getReference("DonorLocation");
        // Register Data..
        donorRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    GeoFire geoFire = new GeoFire(donorLocRef);
                    Double mLatitude = Double.parseDouble(str_latituted);
                    Double mLongtitude = Double.parseDouble(str_longtited);
                    geoFire.setLocation(str_phone, new GeoLocation(mLatitude, mLongtitude), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Log.e(TAG, "There was an error saving the location to GeoFire: " + error);
                            } else {
                                Log.e(TAG, "Location saved on server successfully!");
                            }
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(DonorRegisterActivity.this, "Donor successfully register.", Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            done.setText("Done");
                            done.setEnabled(true);
                            Intent intent = new Intent(DonorRegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 2000);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }


    public void setSelectedBackground(String type) {
        if (type.contentEquals("A+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("A-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.color_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("B+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("B-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.color_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("O+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("O-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.color_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("AB+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("AB-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.color_round));
        }
    }

    public boolean validation() {
        boolean isValid = true;
        StringBuilder builder = new StringBuilder();
        String str_name = name.getText().toString();
        String str_email = email.getText().toString();
        String str_phone = phone.getText().toString();
        String str_age = age.getText().toString();
//        String str_password = password.getText().toString();
        String str_location = location_tv.getText().toString();

        if (str_email.contains("@")) {

            if (TextUtils.isEmpty(str_email)) {
                email.setError("Email can't be null");
                isValid = false;
                builder.append("Email can't be null");
                builder.append("\n");
            } else {
                if (!AppConstants.isValidEmailAddress(str_email)) {
                    email.setError("Invalid email");
                    isValid = false;
                    builder.append("Invalid email");
                    builder.append("\n");
                }
            }
        }
        if (TextUtils.isEmpty(str_phone)) {
            phone.setError("Phone can't be null");
            isValid = false;
            builder.append("Phone can't be null");
            builder.append("\n");
        } else {
            if (!AppConstants.isValidNumberAcceptPtcl(str_phone)) {
                phone.setError("Invalid phone number entered");
                isValid = false;
                builder.append("Invalid phone number entered");
                builder.append("\n");
            }
        }

        if (TextUtils.isEmpty(str_age)) {
            age.setError("Age can't be null");
            isValid = false;
            builder.append("Age can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_name)) {
            name.setError("Name can't be null");
            isValid = false;
            builder.append("Name can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_location)) {
            isValid = false;
            Toast.makeText(DonorRegisterActivity.this, "Location can't be null", Toast.LENGTH_SHORT).show();
            builder.append("Location can't be null");
            builder.append("\n");
        }

//        if (TextUtils.isEmpty(str_password)) {
//            password.setError("Password can't be null");
//            isValid = false;
//            builder.append("Password can't be null");
//            builder.append("\n");
//        } else {
//            if (str_password.length() < 6) {
//                password.setError("Password must be at least 6 characters");
//                isValid = false;
//                builder.append("Password must be at least 6 characters");
//                builder.append("\n");
//            }
//        }

        if (!isGenderSelected) {
            isValid = false;
            Toast.makeText(DonorRegisterActivity.this, "Select gender", Toast.LENGTH_SHORT).show();
        }

        Log.e(TAG, builder.toString());

        if (!isValid) {
            hideProgressBar();
        }

        return isValid;
    }

    public void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public Location getLastKnownLocation() {
        final long MIN_DISTANCE_FOR_UPDATE = 10, MIN_TIME_FOR_UPDATE = 1000 * 60;
        Location bestLocation = null;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            if (AppConstants.checkLocationPermission(DonorRegisterActivity.this)) {
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
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
            Log.e("check", "Gps is not enable");
        }
        return bestLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

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
