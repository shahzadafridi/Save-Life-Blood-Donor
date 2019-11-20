package official.com.savelife_blooddonor.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.rtp.AudioGroup;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.TestLooperManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.Registration.DonorRegisterActivity;
import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.Util.Locator;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference reference;
    String getRole,getPhone;
    TextView tvOnlineOffline;
    Switch aSwitch;
    TextView tvBack,tvEdit;
    TextView age,bgroup,email,gender,name,phone,showLocation,etShowLocation;
    EditText etAge,etBgroup,etEmail,etGender,etName,etPhone;
    String sAge,sBgroup,sEmail,sGender,sName,sPhone,sLat,sLon,sStatus="online";
    Button back,update,bottomsheetCurrent,bottomsheetShowonMap;
    ProgressBar progressBar,etProgressBar;
    LinearLayout displayLayout,editLayout,mainLayout;
    boolean isLocationClick = false, isOnline = true,isSwitchClick = false;
    String TAG = "ProfileActivity";
    BottomSheetDialog bottomSheetDialogLocation;
    Location location;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        InitUI();

        getRole = AppConstants.getRole(this,"SESSION");
        getPhone = AppConstants.getPhone(this,"SESSION");

        if (getRole.contentEquals("donee")){
            tvOnlineOffline.setVisibility(View.GONE);
            aSwitch.setVisibility(View.GONE);
            reference = FirebaseDatabase.getInstance().getReference().child("Donee");
        }else {
            reference = FirebaseDatabase.getInstance().getReference().child("Donor");
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String key = snap.getKey();
                    if (key.contentEquals(getPhone)){
                        Map map = (HashMap) snap.getValue();
                        Log.e("check",map.get("phone").toString());
                        sAge = map.get("age").toString();
                        sBgroup = map.get("bgroup").toString();
                        sEmail = map.get("email").toString();
                        if (map.get("status") != null) {
                            sStatus = map.get("status").toString();
                        }
                        sGender = map.get("gender").toString();
                        sLat = map.get("latitude").toString();
                        sLon = map.get("longtitude").toString();
                        sName = map.get("name").toString();
                        sPhone = map.get("phone").toString();
                        updateUI();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI() {

//        mainLayout.setVisibility(View.VISIBLE);

        etAge.setText(sAge);
        etBgroup.setText(sBgroup);
        etEmail.setText(sEmail);
        etGender.setText(sGender);
        etName.setText(sName);


        if (sStatus.contentEquals("offline")){
            aSwitch.setChecked(false);
            tvOnlineOffline.setText("Offline");
        }else {
            tvOnlineOffline.setText("Online");
            aSwitch.setChecked(true);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> map = new HashMap<>();
                if (isChecked){
                    isOnline = true;
                    tvOnlineOffline.setText("Online");
                    map.put("status","online");
                }else {
                    isOnline = false;
                    tvOnlineOffline.setText("Offline");
                    map.put("status","offline");
                }

                FirebaseDatabase.getInstance().getReference().child("Donor").child(getPhone).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            if (isSwitchClick) {
                                Toast.makeText(ProfileActivity.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (isSwitchClick) {
                            Toast.makeText(ProfileActivity.this, "Status update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        age.setText(sAge);
        bgroup.setText(sBgroup);
        email.setText(sEmail);
        gender.setText(sGender);
        name.setText(sName);
        phone.setText(sPhone);
        back.setText("BACK");
        back.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void InitUI() {

        mainLayout = (LinearLayout) findViewById(R.id.profile_main_layout);
//        mainLayout.setVisibility(View.INVISIBLE);

        // Status
        tvOnlineOffline = (TextView) findViewById(R.id.tvOnlineOffline);
        aSwitch = (Switch) findViewById(R.id.swtich_onlineOffline);

        //Bottom Sheet for select locaiton method
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_item_location_layout, null);
        bottomSheetDialogLocation = new BottomSheetDialog(this);
        bottomSheetDialogLocation.setContentView(dialogView);
        bottomsheetCurrent = (Button) bottomSheetDialogLocation.findViewById(R.id.bottom_item_location_current);
        bottomsheetShowonMap = (Button) bottomSheetDialogLocation.findViewById(R.id.bottom_item_location_chooseOnMap);
        bottomsheetCurrent.setOnClickListener(this);
        bottomsheetShowonMap.setOnClickListener(this);


        // Top back and edit icon
        tvBack = (TextView) findViewById(R.id.profile_back_top);
        tvEdit = (TextView) findViewById(R.id.profile_edit_top);
        tvBack.setOnClickListener(this);
        tvEdit.setOnClickListener(this);

        //Display and Edit Layout
        displayLayout = (LinearLayout) findViewById(R.id.profile_display_layout);
        editLayout = (LinearLayout) findViewById(R.id.profile_edit_layout);
        showLocation = (TextView) findViewById(R.id.profile_showsLocation);
        progressBar = (ProgressBar) findViewById(R.id.profile_prog);

        //Edit Layout EditText
        etAge = (EditText) findViewById(R.id.et_profile_age);
        etBgroup = (EditText) findViewById(R.id.et_profile_bgroup);
        etEmail = (EditText) findViewById(R.id.et_profile_email);
        etGender = (EditText) findViewById(R.id.et_profile_gender);
        etName = (EditText) findViewById(R.id.et_profile_name);
        etShowLocation = (TextView) findViewById(R.id.et_profile_showsLocation);
        update = (Button) findViewById(R.id.et_profile_Update);
        etProgressBar = (ProgressBar) findViewById(R.id.et_profile_prog);
        update.setOnClickListener(this);
        etShowLocation.setOnClickListener(this);

        //Display Layout TextView
        age = (TextView) findViewById(R.id.profile_age);
        bgroup = (TextView) findViewById(R.id.profile_bgroup);
        email = (TextView) findViewById(R.id.profile_email);
        gender = (TextView) findViewById(R.id.profile_gender);
        name = (TextView) findViewById(R.id.profile_name);
        phone = (TextView) findViewById(R.id.profile_phone);
        back = (Button) findViewById(R.id.profile_back);
        back.setOnClickListener(this);
        showLocation.setOnClickListener(this);
        back.setText("");
        back.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile_back){
            onBackPressed();
        }else if (view.getId() == R.id.profile_showsLocation){
            Intent intent = new Intent(ProfileActivity.this,ProfileMapActivity.class);
            intent.putExtra("lat",sLat);
            intent.putExtra("lon",sLon);
            intent.putExtra("phone",getPhone);
            startActivity(intent);
        }else if (view.getId() == R.id.et_profile_Update){
            etProgressBar.setVisibility(View.VISIBLE);
            update.setText("");
            update.setEnabled(false);
            if (validation()){
                String str_name = etName.getText().toString();
                String str_age = etAge.getText().toString();
                String str_bgroup = etBgroup.getText().toString();
                String str_email = etEmail.getText().toString();
                String str_gender = etGender.getText().toString();
                String str_latituted = String.valueOf(location.getLatitude());
                String str_longtited = String.valueOf(location.getLongitude());

                Map<String, Object> map = new HashMap<>();
                map.put("status",tvOnlineOffline.getText().toString().toLowerCase());
                map.put("name", str_name);
                map.put("email", str_email);
                map.put("gender", str_gender);
                map.put("age", str_age);
                map.put("phone",getPhone);
                map.put("bgroup", str_bgroup);
                map.put("latitude", str_latituted);
                map.put("longtitude", str_longtited);
                FirebaseDatabase.getInstance().getReference("Donor").child(getPhone).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                            updateDonorLocationNode();
                            hideProgressBar();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,"Profile update failed",Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                });

            }else {
                Toast.makeText(ProfileActivity.this,"Incomplete detail entered.",Toast.LENGTH_SHORT).show();
            }

        }else if (view.getId() == R.id.profile_edit_top) {
            displayLayout.setVisibility(View.INVISIBLE);
            editLayout.setVisibility(View.VISIBLE);
            tvBack.setVisibility(View.VISIBLE);

        }else if (view.getId() == R.id.profile_back_top) {
            editLayout.setVisibility(View.INVISIBLE);
            displayLayout.setVisibility(View.VISIBLE);
            tvBack.setVisibility(View.INVISIBLE);
        }else if (view.getId() == R.id.et_profile_showsLocation){
            bottomSheetDialogLocation.show();

        }else if (view.getId() == R.id.bottom_item_location_current){
            if (AppConstants.checkLocationPermission(ProfileActivity.this)) {
                location = Locator.getInstance(ProfileActivity.this, locationManager).getLastKnownLocation();
                if (location != null) {
                    isLocationClick = true;
                    bottomSheetDialogLocation.hide();
                    etShowLocation.setText("Latitude:" + location.getLatitude() + " , Longtitude:" + location.getLongitude());
                } else {
                    Toast.makeText(ProfileActivity.this, "Location can't be null.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Permission needed.", Toast.LENGTH_SHORT).show();
            }
        }else if (view.getId() == R.id.bottom_item_location_chooseOnMap){
            Intent intent = new Intent(ProfileActivity.this,ProfileMapActivity.class);
            intent.putExtra("lat",sLat);
            intent.putExtra("lon",sLon);
            intent.putExtra("phone",getPhone);
            startActivityForResult(intent,111);
        }
    }

    private void updateDonorLocationNode() {
        DatabaseReference donorLocRef = FirebaseDatabase.getInstance().getReference("DonorLocation");
        GeoFire geoFire = new GeoFire(donorLocRef);
        geoFire.setLocation(getPhone, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.e(TAG, "There was an error saving the location to GeoFire: " + error);
                } else {
                    Log.e(TAG, "Location saved on server successfully!");
                }
            }
        });
    }

    public boolean validation() {
        boolean isValid = true;

        etAge = (EditText) findViewById(R.id.et_profile_age);
        etBgroup = (EditText) findViewById(R.id.et_profile_bgroup);
        etEmail = (EditText) findViewById(R.id.et_profile_email);
        etGender = (EditText) findViewById(R.id.et_profile_gender);
        etName = (EditText) findViewById(R.id.et_profile_name);

        StringBuilder builder = new StringBuilder();

        String str_name = etName.getText().toString();
        String str_age = etAge.getText().toString();
        String str_bgroup = etBgroup.getText().toString();
        String str_email = etEmail.getText().toString();
        String str_gender = etGender.getText().toString();



        if (TextUtils.isEmpty(str_name)) {
            etName.setError("name can't be null");
            isValid = false;
            builder.append("name can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_age)) {
            etAge.setError("Age can't be null");
            isValid = false;
            builder.append("Age can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_bgroup)) {
            etBgroup.setError("Blood group can't be null");
            isValid = false;
            builder.append("Blood group can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_email)) {
            etEmail.setError("Email can't be null");
            isValid = false;
            builder.append("Email can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_gender)) {
            etGender.setError("Gender can't be null");
            isValid = false;
            builder.append("Gender can't be null");
            builder.append("\n");
        }

        if (!isLocationClick){
            isValid = false;
            Toast.makeText(ProfileActivity.this,"Select Location",Toast.LENGTH_SHORT).show();
        }

        Log.e(TAG, builder.toString());

        if (!isValid) {
            hideProgressBar();
        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111){
            if (resultCode == RESULT_OK){
                bottomSheetDialogLocation.hide();
                isLocationClick = true;
                location = new Location("MyLocation");
                location.setLatitude(data.getDoubleExtra("lat",0.00));
                location.setLongitude(data.getDoubleExtra("lon",0.00));
                etShowLocation.setText("Latitude:"+location.getLatitude()+" , Longtitude:"+location.getLongitude());
            }
        }
    }

    public void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                update.setText("Update");
                update.setEnabled(true);
                etProgressBar.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayLayout.setVisibility(View.VISIBLE);
                        editLayout.setVisibility(View.INVISIBLE);
                    }
                },2000);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
