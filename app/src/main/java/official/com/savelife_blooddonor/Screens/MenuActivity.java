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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button nearByLoc, bloodGroup, nearByBBank,request;
    Dialog dialog;
    TextView Aplus, Aneg, Bplus, Bneg, Oplus, Oneg, ABplus, ABneg;
    String str_blood_group = "O+"; // default blood group.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        nearByBBank = (Button) findViewById(R.id.menu_nearby_bloodbank);
        bloodGroup = (Button) findViewById(R.id.menu_blood_group);
        nearByLoc = (Button) findViewById(R.id.menu_nearby_location);
        request = (Button) findViewById(R.id.menu_nearby_showRequest);
        nearByBBank.setOnClickListener(this);
        bloodGroup.setOnClickListener(this);
        nearByLoc.setOnClickListener(this);
        request.setOnClickListener(this);
        IntializeDialog();
        String getRole = AppConstants.getRole(this,"SESSION");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (getRole.contentEquals("donor")) {
                request.setVisibility(View.VISIBLE);
            }else {
                request.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void IntializeDialog(){
        dialog = AppConstants.onCreateDialog(MenuActivity.this, R.layout.blood_group_dialog, true);
        Aplus = (TextView) dialog.findViewById(R.id.dialog_aplus_tv);
        Aneg = (TextView) dialog.findViewById(R.id.dialog_anegtive_tv);
        Bplus = (TextView) dialog.findViewById(R.id.dialog_bplus_tv);
        Bneg = (TextView) dialog.findViewById(R.id.dialog_bnegtive_tv);
        Oplus = (TextView) dialog.findViewById(R.id.dialog_oplus_tv);
        Oneg = (TextView) dialog.findViewById(R.id.dialog_onegative_tv);
        ABplus = (TextView) dialog.findViewById(R.id.dialog_ABplus_tv);
        ABneg = (TextView) dialog.findViewById(R.id.dialog_ABnegtive_tv);
        Aplus.setOnClickListener(this);
        Aneg.setOnClickListener(this);
        Bplus.setOnClickListener(this);
        Bneg.setOnClickListener(this);
        Oplus.setOnClickListener(this);
        Oneg.setOnClickListener(this);
        ABplus.setOnClickListener(this);
        ABneg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menu_nearby_bloodbank) {
            goToNextActivity("nearby_bloodbank", "");
        } else if (view.getId() == R.id.menu_nearby_location) {
            goToNextActivity("nearby_donor", "");
        } else if (view.getId() == R.id.menu_blood_group) {
            if (dialog == null){
                IntializeDialog();
            }
            dialog.show();
        } else if (view.getId() == R.id.dialog_aplus_tv) {
            str_blood_group = "A+";
            goToNextActivity("single_bloodgroup", "A+");
            setSelectedBackground(str_blood_group);
        } else if (view.getId() == R.id.dialog_anegtive_tv) {
            str_blood_group = "A-";
            goToNextActivity("single_bloodgroup", "A-");
            setSelectedBackground(str_blood_group);
        } else if (view.getId() == R.id.dialog_bplus_tv) {
            str_blood_group = "B+";
            goToNextActivity("single_bloodgroup", "B+");
            setSelectedBackground(str_blood_group);
        } else if (view.getId() == R.id.dialog_bnegtive_tv) {
            str_blood_group = "B-";
            goToNextActivity("single_bloodgroup", "B-");
            setSelectedBackground(str_blood_group);
        } else if (view.getId() == R.id.dialog_oplus_tv) {
            str_blood_group = "O+";
            goToNextActivity("single_bloodgroup", "O+");
            setSelectedBackground(str_blood_group);
        } else if (view.getId() == R.id.dialog_onegative_tv) {
            str_blood_group = "O-";
            goToNextActivity("single_bloodgroup", "O-");
            setSelectedBackground(str_blood_group);
        } else if (view.getId() == R.id.dialog_ABplus_tv) {
            str_blood_group = "AB+";
            setSelectedBackground(str_blood_group);
            goToNextActivity("single_bloodgroup", "AB+");
        } else if (view.getId() == R.id.dialog_ABnegtive_tv) {
            str_blood_group = "AB-";
            goToNextActivity("single_bloodgroup", "AB-");
            setSelectedBackground(str_blood_group);
        }else if (view.getId() == R.id.menu_nearby_showRequest){
            Toast.makeText(this,"Comming Soon",Toast.LENGTH_SHORT).show();
        }
    }

    public void goToNextActivity(String type, String bgroup) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (AppConstants.checkLocationPermission(MenuActivity.this)) {
            Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("bgroup", bgroup);
            startActivity(intent);
        } else {
            Toast.makeText(MenuActivity.this, "Permissions needed to shows nearby blood bank.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSelectedBackground(String type) {
        if (type.contentEquals("A+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("A-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.color_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("B+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("B-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.color_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("O+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("O-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.color_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("AB+")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.color_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
        } else if (type.contentEquals("AB-")) {
            Aplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Aneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Bneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            Oneg.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABplus.setBackground(getResources().getDrawable(R.drawable.gray_round));
            ABneg.setBackground(getResources().getDrawable(R.drawable.color_round));
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
