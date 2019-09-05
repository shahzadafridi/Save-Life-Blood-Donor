package official.com.savelife_blooddonor.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import official.com.savelife_blooddonor.AppConstants;
import official.com.savelife_blooddonor.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button nearByLoc, bloodGroup, nearByBBank;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        nearByBBank = (Button) findViewById(R.id.menu_nearby_bloodbank);
        bloodGroup = (Button) findViewById(R.id.menu_blood_group);
        nearByLoc = (Button) findViewById(R.id.menu_nearby_location);
        dialog =  AppConstants.onCreateDialog(MenuActivity.this,R.layout.blood_group_dialog,true);
        nearByBBank.setOnClickListener(this);
        bloodGroup.setOnClickListener(this);
        nearByLoc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menu_nearby_bloodbank) {

            if (AppConstants.checkLocationPermission(MenuActivity.this)) {
                Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                intent.putExtra("type", "nearby_bloodbank");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Permissions needed to shows nearby blood bank.", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.menu_nearby_location) {
            Toast.makeText(this, "Not developed yet, is in progress", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.menu_blood_group) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppConstants.LOC_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                    intent.putExtra("type", "nearby_bloodbank");
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
