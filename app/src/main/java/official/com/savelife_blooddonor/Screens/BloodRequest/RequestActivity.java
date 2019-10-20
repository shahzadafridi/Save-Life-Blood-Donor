package official.com.savelife_blooddonor.Screens.BloodRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import official.com.savelife_blooddonor.Adapter.RequestAdapter;
import official.com.savelife_blooddonor.Model.Request;
import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Util.AppConstants;

public class RequestActivity extends AppCompatActivity {

    String str_phone, str_role;
    String TAG = "RequestActivity";
    List<Request> requests = new ArrayList<>();
    RecyclerView recyclerView;
    RequestAdapter requestAdapter;
    ProgressBar progressBar;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        str_phone = AppConstants.getSharedPref(RequestActivity.this, "SESSION").getString("phone", "");
        str_role = AppConstants.getSharedPref(RequestActivity.this, "SESSION").getString("role", "");
        recyclerView = (RecyclerView) findViewById(R.id.request_recyclerview);
        progressBar = (ProgressBar) findViewById(R.id.request_progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(this, requests, str_role);
        recyclerView.setAdapter(requestAdapter);
        add = (FloatingActionButton) findViewById(R.id.request_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestActivity.this, PostRequestActivity.class);
                startActivity(intent);
            }
        });
        if (str_role.contentEquals("donee")) {
            getDoneeRequest(str_phone);
            add.setVisibility(View.VISIBLE);
        } else {
            getRequest(str_phone);
            add.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @param str_phone
     * @Firebase sotred in below strucutre
     * FirebaseDatabase -> Request -> SFDSA320930u@# (Child id)
     * "address" : "complex"
     * "bgroup" : "O+"
     * "latitude" : "33.977819757257265"
     * "longtitude" : "71.4328616438901"
     * "message" : "I need O+ blood urgently"
     * "name" : "shahzad"
     * "phone" : "033392180000"
     */

    private void getDoneeRequest(String str_phone) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Request");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map snap_info = (Map) snap.getValue();
                        if (snap_info.get("phone") != null) {
                            if (snap_info.get("phone").toString().contentEquals(str_phone)) {
                                String fphone = snap_info.get("phone").toString();
                                if (fphone.contentEquals(str_phone)) {
                                    String name = snap_info.get("name").toString();
                                    String message = snap_info.get("message").toString();
                                    String phone = snap_info.get("phone").toString();
                                    String address = snap_info.get("address").toString();
                                    String bgroup = snap_info.get("bgroup").toString();
                                    String latitude = snap_info.get("latitude").toString();
                                    String longtitude = snap_info.get("longtitude").toString();
                                    Double lat = Double.parseDouble(latitude);
                                    Double lon = Double.parseDouble(longtitude);
                                    Location location = new Location(name);
                                    location.setLatitude(lat);
                                    location.setLongitude(lon);
                                    requests.add(new Request(snap.getKey(), name, message, bgroup, address, phone, location));
                                    Log.e(TAG, "name: " + name + " , message: " + message + " , phone: " + phone + " , address: " + address + " , bgroup: " + bgroup + " , latitude: " + latitude + " , longtitude: " + longtitude);
                                }
                            }
                        } else {
                            Toast.makeText(RequestActivity.this, "No Request posted yet", Toast.LENGTH_SHORT).show();
                        }
                    }

                    updateAdapter();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RequestActivity.this, "No Request posted yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void getRequest(String str_phone) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Request");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map snap_info = (Map) snap.getValue();
                        if (snap_info.get("phone") != null) {
                            String fphone = snap_info.get("phone").toString();
                            if (fphone.contentEquals(str_phone)) {
                                String name = snap_info.get("name").toString();
                                String message = snap_info.get("message").toString();
                                String phone = snap_info.get("phone").toString();
                                String address = snap_info.get("address").toString();
                                String bgroup = snap_info.get("bgroup").toString();
                                String latitude = snap_info.get("latitude").toString();
                                String longtitude = snap_info.get("longtitude").toString();
                                Double lat = Double.parseDouble(latitude);
                                Double lon = Double.parseDouble(longtitude);
                                Location location = new Location(name);
                                location.setLatitude(lat);
                                location.setLongitude(lon);
                                requests.add(new Request(snap.getKey(), name, message, bgroup, address, phone, location));
                                Log.e(TAG, "name: " + name + " , message: " + message + " , phone: " + phone + " , address: " + address + " , bgroup: " + bgroup + " , latitude: " + latitude + " , longtitude: " + longtitude);
                            }

                        } else {
                            Toast.makeText(RequestActivity.this, "No Request posted yet", Toast.LENGTH_SHORT).show();
                        }
                    }

                    updateAdapter();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RequestActivity.this, "No Request posted yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void updateAdapter() {
        progressBar.setVisibility(View.INVISIBLE);
        requestAdapter.setRequestList(requests);
    }
}
