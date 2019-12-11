package official.com.savelife_blooddonor.Screens.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import official.com.savelife_blooddonor.Adapter.DoneeAdapter;
import official.com.savelife_blooddonor.Adapter.DonorAdapter;
import official.com.savelife_blooddonor.Model.Donee;
import official.com.savelife_blooddonor.Model.Donor;
import official.com.savelife_blooddonor.R;

public class AdminDonorActivity extends AppCompatActivity {

    RecyclerView doneeRecyclerView;
    List<Donor> donorList = new ArrayList<>();
    DonorAdapter adapter;
    String TAG = "AdminDoneeActivity";
    String sAge, sBgroup, sEmail, sGender, sName, sPhone, sLat, sLon, sStatus = "online";
    boolean isAdapterInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donor);
        doneeRecyclerView = (RecyclerView) findViewById(R.id.donor_recyclerView);
        doneeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance().getReference().child("Donor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donorList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String key = snap.getKey();
                    Map map = (HashMap) snap.getValue();
                    Donor donor = new Donor();
                    Log.e("check", map.get("phone").toString());
                    sAge = map.get("age").toString();
                    sBgroup = map.get("bgroup").toString();
                    sEmail = map.get("email").toString();
                    if (map.get("status") != null) {
                        sStatus = map.get("status").toString();
                    }
                    sGender = map.get("gender").toString();
                    sName = map.get("name").toString();
                    sPhone = map.get("phone").toString();
                    donor.setAge(sAge);
                    donor.setName(sName);
                    donor.setBgroup(sBgroup);
                    donor.setEmail(sEmail);
                    donor.setGender(sGender);
                    donor.setPhone(sPhone);
                    donor.setStatus(sStatus);
                    donorList.add(donor);
                }
                if (!isAdapterInit) {
                    adapter = new DonorAdapter(AdminDonorActivity.this, donorList);
                    doneeRecyclerView.setAdapter(adapter);
                    isAdapterInit = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
