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

import official.com.savelife_blooddonor.Adapter.DonorAdapter;
import official.com.savelife_blooddonor.Adapter.RequestAdapter;
import official.com.savelife_blooddonor.Adapter.RequestAdapterAdmin;
import official.com.savelife_blooddonor.Model.Complain;
import official.com.savelife_blooddonor.Model.Donor;
import official.com.savelife_blooddonor.Model.Request;
import official.com.savelife_blooddonor.R;

public class AdminBReqeustActivity extends AppCompatActivity {
    RecyclerView doneeRecyclerView;
    List<Request> requestList = new ArrayList<>();
    RequestAdapterAdmin adapter;
    String TAG = "AdminDoneeActivity";
    String sMessage, sAddress, sName, sPhone,sBgroup;
    boolean isAdapterInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_breqeust);
        doneeRecyclerView = (RecyclerView) findViewById(R.id.brequest_recyclerView);
        doneeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance().getReference().child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String key = snap.getKey();
                    Map map = (HashMap) snap.getValue();
                    sAddress = map.get("address").toString();
                    sBgroup = map.get("bgroup").toString();
                    sName = map.get("name").toString();
                    sPhone = map.get("phone").toString();
                    sMessage = map.get("message").toString();
                    Request request = new Request(key,sName,sMessage,sBgroup,sAddress,sPhone,null);
                    requestList.add(request);
                }
                if (!isAdapterInit) {
                    adapter = new RequestAdapterAdmin(AdminBReqeustActivity.this, requestList);
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
