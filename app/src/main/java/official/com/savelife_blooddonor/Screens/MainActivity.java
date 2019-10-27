package official.com.savelife_blooddonor.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.BloodRequest.RequestActivity;
import official.com.savelife_blooddonor.Screens.Registration.RegistrationMenu;
import official.com.savelife_blooddonor.Util.AppConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button search, request;
    TextView bottom_label, logout_label, profile_label;
    ImageButton logout, profile;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (Button) findViewById(R.id.main_search);
        request = (Button) findViewById(R.id.main_request);
        bottom_label = (TextView) findViewById(R.id.main_bottom_label);
        progressBar = (ProgressBar) findViewById(R.id.main_progressBar);
        profile = (ImageButton) findViewById(R.id.profile);
        profile_label = (TextView) findViewById(R.id.profile_label);
        logout = (ImageButton) findViewById(R.id.logout);
        logout_label = (TextView) findViewById(R.id.logout_label);
        logout.setOnClickListener(this);
        profile.setOnClickListener(this);
        search.setOnClickListener(this);
        request.setOnClickListener(this);
        bottom_label.setOnClickListener(this);

        String getRole = AppConstants.getRole(this, "SESSION");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (getRole.contentEquals("donee")) {
                request.setVisibility(View.VISIBLE);
                profile.setVisibility(View.INVISIBLE);
            }
        } else {
            logout_label.setVisibility(View.INVISIBLE);
            logout.setVisibility(View.INVISIBLE);
            profile.setVisibility(View.INVISIBLE);
            profile_label.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.main_search) {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.main_bottom_label) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toast.makeText(MainActivity.this, "You are already login", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, RegistrationMenu.class);
                startActivity(intent);
            }
        } else if (view.getId() == R.id.main_request) {
            Intent intent = new Intent(MainActivity.this, RequestActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.logout) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signOut();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                    startActivity(intent);
                }
            }, 2000);

        } else if (view.getId() == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
