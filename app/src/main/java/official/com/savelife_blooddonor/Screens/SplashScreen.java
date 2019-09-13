package official.com.savelife_blooddonor.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.R;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {

    RelativeLayout arcLayout;
    ImageView logo;
    Animation top2bottom, bottom2top;
    String TAG = "MainActivity";
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppConstants.setSystemBarTheme(this, false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.light_red));
        }
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();

        arcLayout = (RelativeLayout) findViewById(R.id.arc_layout);
        logo = (ImageView) findViewById(R.id.splash_logo);

        top2bottom = AnimationUtils.loadAnimation(this, R.anim.top2bottom);
        logo.setAnimation(top2bottom);
        top2bottom.setAnimationListener(this);
        top2bottom.start();

        bottom2top = AnimationUtils.loadAnimation(this, R.anim.bottom2top);
        arcLayout.setAnimation(bottom2top);
        bottom2top.start();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        Log.e(TAG, "onAnimationStart");
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.e(TAG, "onAnimationEnd");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
