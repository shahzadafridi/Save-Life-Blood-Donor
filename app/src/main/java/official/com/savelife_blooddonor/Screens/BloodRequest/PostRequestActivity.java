package official.com.savelife_blooddonor.Screens.BloodRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.Util.Locator;

public class PostRequestActivity extends AppCompatActivity implements View.OnClickListener{

    TextView Aplus, Aneg, Bplus, Bneg, Oplus, Oneg, ABplus, ABneg, locationTv;
    EditText name,message, address;
    Button done;
    String TAG = "PostRequestActivity";
    ProgressBar progressBar;
    String str_blood_group = "O+";
    protected LocationManager locationManager;
    Location location;
    boolean isUpdate = false;
    String str_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_request);
        InitUI();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void InitUI(){
        Aplus = (TextView) findViewById(R.id.post_aplus_tv);
        Aneg = (TextView) findViewById(R.id.post_anegtive_tv);
        Bplus = (TextView) findViewById(R.id.post_bplus_tv);
        Bneg = (TextView) findViewById(R.id.post_bnegtive_tv);
        Oplus = (TextView) findViewById(R.id.post_oplus_tv);
        Oneg = (TextView) findViewById(R.id.post_onegative_tv);
        ABplus = (TextView) findViewById(R.id.post_ABplus_tv);
        ABneg = (TextView) findViewById(R.id.post_ABnegtive_tv);
        name = (EditText) findViewById(R.id.post_name);
        address = (EditText) findViewById(R.id.post_address);
        message = (EditText) findViewById(R.id.post_message);
        locationTv = (TextView) findViewById(R.id.post_click_location);
        done = (Button) findViewById(R.id.post_btn);
        progressBar = (ProgressBar) findViewById(R.id.post_progressBar);
        Aplus.setOnClickListener(this);
        Aneg.setOnClickListener(this);
        Bplus.setOnClickListener(this);
        Bneg.setOnClickListener(this);
        Oplus.setOnClickListener(this);
        Oneg.setOnClickListener(this);
        ABplus.setOnClickListener(this);
        ABneg.setOnClickListener(this);
        done.setOnClickListener(this);
        locationTv.setOnClickListener(this);
        String type = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(type)){
            if (type.contentEquals("update")) {
                isUpdate = true;
                done.setText("Update");
                updateUI();
            }
        }
    }

    private void updateUI() {
        str_id = getIntent().getStringExtra("id");
        String str_name = getIntent().getStringExtra("name");
        String str_address = getIntent().getStringExtra("address");
        String str_bgroup = getIntent().getStringExtra("bgroup");
        String str_message = getIntent().getStringExtra("message");
        name.setText(str_name);
        address.setText(str_address);
        setSelectedBackground(str_bgroup);
        message.setText(str_message);
    }

    public boolean validation() {
        boolean isValid = true;
        StringBuilder builder = new StringBuilder();
        String str_name = name.getText().toString();
        String str_address = address.getText().toString();
        String str_message = message.getText().toString();

        if (TextUtils.isEmpty(str_address)) {
            address.setError("Address can't be null");
            isValid = false;
            builder.append("Address can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_name)) {
            name.setError("Name can't be null");
            isValid = false;
            builder.append("Name can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_message)) {
            message.setError("Message can't be null");
            isValid = false;
            builder.append("Message can't be null");
            builder.append("\n");
        }

        if (location == null){
            isValid = false;
            Toast.makeText(PostRequestActivity.this,"Location needed to post request",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.post_click_location){
            if (AppConstants.checkLocationPermission(PostRequestActivity.this)) {
                location = Locator.getInstance(PostRequestActivity.this, locationManager).getLastKnownLocation();
                if (location != null) {
                    locationTv.setText("Latitude:" + location.getLatitude() + " , Longtitude:" + location.getLongitude());
                } else {
                    Toast.makeText(PostRequestActivity.this, "Location can't be null.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PostRequestActivity.this, "Permission needed.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.post_aplus_tv) {
            str_blood_group = "A+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_anegtive_tv) {
            str_blood_group = "A-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_bplus_tv) {
            str_blood_group = "B+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_bnegtive_tv) {
            str_blood_group = "B-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_oplus_tv) {
            str_blood_group = "O+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_onegative_tv) {
            str_blood_group = "O-";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_ABplus_tv) {
            str_blood_group = "AB+";
            setSelectedBackground(str_blood_group);
        } else if (id == R.id.post_ABnegtive_tv) {
            str_blood_group = "AB-";
            setSelectedBackground(str_blood_group);
        }else if (id == R.id.post_btn){
            if (validation()){
                progressBar.setVisibility(View.VISIBLE);
                done.setText("");
                PostRequest();
            } else {
                Toast.makeText(PostRequestActivity.this, "Incomplete detail.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void PostRequest() {
        DatabaseReference requestRef;

        String str_name = name.getText().toString();
        String str_address = address.getText().toString();
        String str_message = message.getText().toString();
        String str_latituted = String.valueOf(location.getLatitude());
        String str_longtited = String.valueOf(location.getLongitude());

        String str_phone = AppConstants.getSharedPref(PostRequestActivity.this,"SESSION").getString("phone","");
        Map<String, String> map = new HashMap<>();
        map.put("name", str_name);
        map.put("address", str_address);
        map.put("message",str_message);
        map.put("phone",str_phone);
        map.put("bgroup", str_blood_group);
        map.put("latitude", str_latituted);
        map.put("longtitude", str_longtited);

        if (isUpdate && !TextUtils.isEmpty(str_id)){
            requestRef = FirebaseDatabase.getInstance().getReference("Request").child(str_id);
        }else {
            requestRef = FirebaseDatabase.getInstance().getReference("Request").push();
        }

        DatabaseReference requestLocRef = FirebaseDatabase.getInstance().getReference("RequestLocation");
        // Register Data..
        requestRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    GeoFire geoFire = new GeoFire(requestLocRef);
                    Double mLatitude = Double.parseDouble(str_latituted);
                    Double mLongtitude = Double.parseDouble(str_longtited);
                    geoFire.setLocation("", new GeoLocation(mLatitude, mLongtitude), new GeoFire.CompletionListener() {
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
                    Toast.makeText(PostRequestActivity.this, "Your request has been posted.", Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            done.setText("Done");
                            done.setEnabled(true);
                            Intent intent = new Intent(PostRequestActivity.this, RequestActivity.class);
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
}
