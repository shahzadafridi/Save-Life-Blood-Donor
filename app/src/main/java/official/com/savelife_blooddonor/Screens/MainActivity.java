package official.com.savelife_blooddonor.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import official.com.savelife_blooddonor.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button search;
    TextView bottom_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search = (Button) findViewById(R.id.main_search);
        bottom_label = (TextView) findViewById(R.id.main_bottom_label);
        search.setOnClickListener(this);
        bottom_label.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.main_search){
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.main_bottom_label){

        }
    }
}
