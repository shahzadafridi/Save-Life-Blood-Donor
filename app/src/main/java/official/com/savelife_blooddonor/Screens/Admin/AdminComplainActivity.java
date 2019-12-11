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

import official.com.savelife_blooddonor.Adapter.ComplainAdapter;
import official.com.savelife_blooddonor.Adapter.DoneeAdapter;
import official.com.savelife_blooddonor.Model.Complain;
import official.com.savelife_blooddonor.Model.Donee;
import official.com.savelife_blooddonor.R;

public class AdminComplainActivity extends AppCompatActivity {
    RecyclerView complainRecyclerView;
    List<Complain> complainList = new ArrayList<>();
    ComplainAdapter adapter;
    String TAG = "AdminDoneeActivity";
    boolean isAdapterInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complain);
        complainRecyclerView = (RecyclerView) findViewById(R.id.complain_recyclerView);
        complainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference().child("complains").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue() != null){
                        complainList.clear();
                        for (DataSnapshot snap: dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: Key"+snap.getKey());
                            Complain complain = (Complain) snap.getValue(Complain.class);
                            complainList.add(complain);
                        }

                        if (!isAdapterInit) {
                            adapter = new ComplainAdapter(AdminComplainActivity.this, complainList);
                            complainRecyclerView.setAdapter(adapter);
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
