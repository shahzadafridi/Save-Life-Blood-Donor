package official.com.savelife_blooddonor.Screens.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.MainActivity;
import official.com.savelife_blooddonor.Screens.SplashScreen;

public class AdminLoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView back;
    EditText email, password;
    Button login;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        back = (TextView) findViewById(R.id.admin_back);
        email = (EditText) findViewById(R.id.admin_email);
        password = (EditText) findViewById(R.id.admin_pass);
        login = (Button) findViewById(R.id.admin_btn);
        progressBar = (ProgressBar) findViewById(R.id.admin_progressBar);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        login.setText("");
        login.setEnabled(false);
        String str_email = email.getText().toString();
        String str_pass = password.getText().toString();
        if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_pass)) {
            login.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            login.setText("Login");
            Toast.makeText(AdminLoginActivity.this, "Enter complete details", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(str_email, str_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminLoginActivity.this, "Admin Successfully login", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        login.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        login.setText("Login");
                        Toast.makeText(AdminLoginActivity.this, "Admin failed to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
