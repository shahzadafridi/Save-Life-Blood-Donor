package official.com.savelife_blooddonor.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.rtp.AudioGroup;
import android.os.Build;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import official.com.savelife_blooddonor.Util.AppConstants;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference reference;
    String getRole,getPhone;
    TextView age,bgroup,email,gender,name,phone,showLocation;
    String sAge,sBgroup,sEmail,sGender,sName,sPhone,sLat,sLon;
    Button back;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        InitUI();

        getRole = AppConstants.getRole(this,"SESSION");
        getPhone = AppConstants.getPhone(this,"SESSION");

        if (getRole.contentEquals("donee")){
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
        showLocation = (TextView) findViewById(R.id.profile_showsLocation);
        progressBar = (ProgressBar) findViewById(R.id.profile_prog);
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

        }else if (view.getId() == R.id.profile_showsLocation){
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_item_layout, null);
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(dialogView);
            dialog.show();
        }
    }
}
