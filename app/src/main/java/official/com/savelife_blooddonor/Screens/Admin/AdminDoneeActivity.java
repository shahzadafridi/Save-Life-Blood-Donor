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
import java.util.List;

import official.com.savelife_blooddonor.Adapter.DoneeAdapter;
import official.com.savelife_blooddonor.Model.Donee;
import official.com.savelife_blooddonor.R;

public class AdminDoneeActivity extends AppCompatActivity {

    RecyclerView doneeRecyclerView;
    List<Donee> doneeList = new ArrayList<>();
    DoneeAdapter adapter;
    String TAG = "AdminDoneeActivity";
    boolean isAdapterInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donee);
        doneeRecyclerView = (RecyclerView) findViewById(R.id.donee_recyclerView);
        doneeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference().child("Donee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue() != null){
                        doneeList.clear();
                        for (DataSnapshot snap: dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: Key"+snap.getKey());
                            Donee donee = (Donee) snap.getValue(Donee.class);
                            doneeList.add(donee);
                        }

                        if (!isAdapterInit) {
                            adapter = new DoneeAdapter(AdminDoneeActivity.this, doneeList);
                            doneeRecyclerView.setAdapter(adapter);
                            isAdapterInit = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }
}
