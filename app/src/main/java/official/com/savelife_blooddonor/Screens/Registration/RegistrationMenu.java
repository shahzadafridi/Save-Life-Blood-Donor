package official.com.savelife_blooddonor.Screens.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import javax.security.auth.login.LoginException;

import official.com.savelife_blooddonor.R;

public class RegistrationMenu extends AppCompatActivity implements View.OnClickListener {

    Button loginDonor,loginDonee;
    TextView loginBack;
    Button donor, donee, login;
    String loginRole = "";
    BottomSheetDialog sheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_menu);
        donor = (Button) findViewById(R.id.reg_menu_donor);
        donee = (Button) findViewById(R.id.reg_menu_donee);
        login = (Button) findViewById(R.id.reg_menu_login);
        donor.setOnClickListener(this);
        donee.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reg_menu_donor) {
            Intent intent = new Intent(RegistrationMenu.this, PhoneAuthentication.class);
            intent.putExtra("role", "donor");
            if (getIntent().getStringArrayExtra("contact") != null) {
                intent.putExtra("contact", getIntent().getStringExtra("contact"));
            }
            startActivity(intent);
        } else if (view.getId() == R.id.reg_menu_donee) {
            Intent intent = new Intent(RegistrationMenu.this, PhoneAuthentication.class);
            intent.putExtra("role", "donee");
            startActivity(intent);
        } else if (view.getId() == R.id.reg_menu_login) {
            showBottomDialog();
        }else if (view.getId() == R.id.login_type_donee_back){
            sheetDialog.dismiss();
        }else if (view.getId() == R.id.login_type_menu_donor){
            loginRole = "donor";
            sheetDialog.dismiss();
            Intent intent = new Intent(RegistrationMenu.this, PhoneAuthentication.class);
            intent.putExtra("login_role", loginRole);
            startActivity(intent);
        }else if (view.getId() == R.id.login_type_menu_donee){
            loginRole = "donee";
            sheetDialog.dismiss();
            Intent intent = new Intent(RegistrationMenu.this, PhoneAuthentication.class);
            intent.putExtra("login_role", loginRole);
            startActivity(intent);
        }
    }

    public void showBottomDialog() {
        sheetDialog = new BottomSheetDialog(RegistrationMenu.this);
        sheetDialog.setContentView(R.layout.login_type_item);
        loginBack = (TextView) sheetDialog.findViewById(R.id.login_type_donee_back);
        loginDonee = (Button) sheetDialog.findViewById(R.id.login_type_menu_donee);
        loginDonor = (Button) sheetDialog.findViewById(R.id.login_type_menu_donor);
        loginBack.setOnClickListener(this);
        loginDonor.setOnClickListener(this);
        loginDonee.setOnClickListener(this);
        sheetDialog.show();

    }

}
