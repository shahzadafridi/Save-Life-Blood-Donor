package official.com.savelife_blooddonor.Screens.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import official.com.savelife_blooddonor.R;

public class RegistrationMenu extends AppCompatActivity implements View.OnClickListener{

    Button donor, donee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_menu);
        donor = (Button) findViewById(R.id.reg_menu_donor);
        donee = (Button) findViewById(R.id.reg_menu_donee);
        donor.setOnClickListener(this);
        donee.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reg_menu_donor){
            Intent intent = new Intent(RegistrationMenu.this,PhoneAuthentication.class);
            intent.putExtra("role","donor");
            startActivity(intent);
        }else if (view.getId() == R.id.reg_menu_donee){
            Intent intent = new Intent(RegistrationMenu.this,PhoneAuthentication.class);
            intent.putExtra("role","donee");
            startActivity(intent);
        }
    }

}
