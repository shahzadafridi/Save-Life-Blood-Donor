package official.com.savelife_blooddonor.Screens.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import official.com.savelife_blooddonor.Adapter.RequestAdapterAdmin;
import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.MainActivity;
import official.com.savelife_blooddonor.Screens.Registration.PhoneAuthentication;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    TextView donorTv, doneeTv, bloodRequestTv, complainTv;
    LinearLayout donorLayout, doneeLayout, bloodRequestLayout, complainLayout;
    String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        InitUI();
    }

    private void InitUI() {
        donorTv = (TextView) findViewById(R.id.admin_donor_tv);
        doneeTv = (TextView) findViewById(R.id.admin_donee_tv);
        bloodRequestTv = (TextView) findViewById(R.id.admin_bloodrequest_tv);
        complainTv = (TextView) findViewById(R.id.admin_complain_tv);

        donorLayout = (LinearLayout) findViewById(R.id.admin_donor_layout);
        doneeLayout = (LinearLayout) findViewById(R.id.admin_donee_layout);
        bloodRequestLayout = (LinearLayout) findViewById(R.id.admin_bloodrequest_layout);
        complainLayout = (LinearLayout) findViewById(R.id.admin_complain_layout);
        donorLayout.setOnClickListener(this);
        doneeLayout.setOnClickListener(this);
        bloodRequestLayout.setOnClickListener(this);
        complainLayout.setOnClickListener(this);
    }

    private void updateRequestUI() {
        FirebaseDatabase.getInstance().getReference().child("Request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bloodRequestTv.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void updateDoneeUI() {
        FirebaseDatabase.getInstance().getReference().child("Donee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doneeTv.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }

    private void updateComplainUI() {
        FirebaseDatabase.getInstance().getReference().child("complains").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                complainTv.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void updateDonorUI() {
        FirebaseDatabase.getInstance().getReference().child("Donor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                donorTv.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDonorUI();
        updateDoneeUI();
        updateRequestUI();
        updateComplainUI();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.admin_donee_layout) {
            startActivity(new Intent(AdminActivity.this, AdminDoneeActivity.class));
        } else if (v.getId() == R.id.admin_donor_layout) {
            startActivity(new Intent(AdminActivity.this, AdminDonorActivity.class));
        } else if (v.getId() == R.id.admin_bloodrequest_layout) {
            startActivity(new Intent(AdminActivity.this, AdminBReqeustActivity.class));
        } else if (v.getId() == R.id.admin_complain_layout) {
            startActivity(new Intent(AdminActivity.this, AdminComplainActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null){
//            FirebaseAuth.getInstance().signOut();
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
