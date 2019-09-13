package official.com.savelife_blooddonor.Screens.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.MainActivity;
import official.com.savelife_blooddonor.Screens.MenuActivity;
import official.com.savelife_blooddonor.Util.AppConstants;

public class DoneeRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name, fname, phone;
    Button done;
    ProgressBar progressBar;
    String TAG = "DoneeRegisterActivity";
    String str_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donee_register);
        name = (EditText) findViewById(R.id.reg_donee_name);
        fname = (EditText) findViewById(R.id.reg_donee_fname);
        phone = (EditText) findViewById(R.id.reg_donee_phone);
        done = (Button) findViewById(R.id.reg_donee_btn);
        progressBar = (ProgressBar) findViewById(R.id.reg_donee_progressBar);
//        str_phone = getIntent().getStringExtra("phone");
//        phone.setText(str_phone);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (validation()) {
            progressBar.setVisibility(View.VISIBLE);
            done.setText("");
            done.setEnabled(false);
            RegisterUser();
        } else {
            Toast.makeText(DoneeRegisterActivity.this, "Enter complete detail.", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        }
    }

    private void RegisterUser() {

        String str_name = name.getText().toString();
        String str_fname = fname.getText().toString();
        String str_phone = phone.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("name", str_name);
        map.put("fname", str_fname);
        map.put("phone", str_phone);

        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Donee").child(str_phone);
        // Register Data..
        donorRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    hideProgressBar();
                    Toast.makeText(DoneeRegisterActivity.this, "Donee successfully register.", Toast.LENGTH_SHORT).show();
                    done.setText("Done");
                    done.setEnabled(true);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DoneeRegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }, 2000);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                done.setText("Done");
                done.setEnabled(true);
                Log.e(TAG, e.getMessage());
            }
        });
    }

    public boolean validation() {
        boolean isValid = true;
        StringBuilder builder = new StringBuilder();
        String str_name = name.getText().toString();
        String str_fname = fname.getText().toString();
        String str_phone = phone.getText().toString();


        if (TextUtils.isEmpty(str_fname)) {
            fname.setError("Father name can't be null");
            isValid = false;
            builder.append("Father name can't be null");
            builder.append("\n");
        }

        if (TextUtils.isEmpty(str_phone)) {
            phone.setError("Phone can't be null");
            isValid = false;
            builder.append("Phone can't be null");
            builder.append("\n");
        } else {
            if (!AppConstants.isValidNumberAcceptPtcl(str_phone)) {
                phone.setError("Invalid phone number entered");
                isValid = false;
                builder.append("Invalid phone number entered");
                builder.append("\n");
            }
        }

        if (TextUtils.isEmpty(str_name)) {
            name.setError("Name can't be null");
            isValid = false;
            builder.append("Name can't be null");
            builder.append("\n");
        }

        Log.e(TAG, builder.toString());

        if (!isValid) {
            hideProgressBar();
        }

        return isValid;
    }

    public void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
