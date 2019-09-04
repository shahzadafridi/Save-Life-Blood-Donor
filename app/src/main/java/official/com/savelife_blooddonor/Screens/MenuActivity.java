package official.com.savelife_blooddonor.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import official.com.savelife_blooddonor.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    Button nearByLoc,bloodGroup, nearByBBank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        nearByBBank = (Button) findViewById(R.id.menu_nearby_bloodbank);
        bloodGroup = (Button) findViewById(R.id.menu_blood_group);
        nearByLoc = (Button) findViewById(R.id.menu_nearby_location);
        nearByBBank.setOnClickListener(this);
        bloodGroup.setOnClickListener(this);
        nearByLoc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menu_nearby_bloodbank){
            Toast.makeText(this,"Not developed yet, is in progress",Toast.LENGTH_SHORT).show();
        }else if (view.getId() == R.id.menu_nearby_location){
            Toast.makeText(this,"Not developed yet, is in progress",Toast.LENGTH_SHORT).show();
        }else if (view.getId() == R.id.menu_blood_group){
            Toast.makeText(this,"Not developed yet, is in progress",Toast.LENGTH_SHORT).show();
        }
    }
}
