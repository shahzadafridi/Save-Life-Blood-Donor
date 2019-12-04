package official.com.savelife_blooddonor.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Util.AppConstants;

public class ComplainScreen extends AppCompatActivity {

    EditText name,phone,message;
    ProgressBar progressBar;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_screen);

        name = (EditText) findViewById(R.id.complain_name);
        phone = (EditText) findViewById(R.id.complain_phone);
        message = (EditText) findViewById(R.id.complain_message);
        progressBar = (ProgressBar) findViewById(R.id.complain_progressBar);
        button = (Button) findViewById(R.id.complain_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String str_phone = phone.getText().toString();
                if (str_phone == null){
                    Toast.makeText(ComplainScreen.this,"Enter complete details",Toast.LENGTH_SHORT).show();
                }else {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    map.put("phone", str_phone);
                    map.put("message", message.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("complains").child(str_phone).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(ComplainScreen.this, MenuActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
}
