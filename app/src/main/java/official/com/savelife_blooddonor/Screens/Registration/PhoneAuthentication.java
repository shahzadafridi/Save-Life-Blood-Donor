package official.com.savelife_blooddonor.Screens.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Util.AppConstants;
import official.com.savelife_blooddonor.Util.PhoneAuthenticator;

public class PhoneAuthentication extends AppCompatActivity implements View.OnClickListener {

    EditText phone;
    Button verify;
    ProgressBar progressBar;
    OtpTextView otpTextView;
    TextView message, timer, resend_otp;
    RelativeLayout optSendLayout;
    LinearLayout optVerificationLayout;
    String role;
    PhoneAuthenticator phoneAuthenticator;
    String TAG = "PhoneAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);
        role = getIntent().getStringExtra("role");
        message = (TextView) findViewById(R.id.verifcation_message);
        phone = (EditText) findViewById(R.id.verify_phone_num);
        verify = (Button) findViewById(R.id.verify_done);
        progressBar = (ProgressBar) findViewById(R.id.verify_progressBar);
        optSendLayout = (RelativeLayout) findViewById(R.id.otp_send_layout);
        timer = (TextView) findViewById(R.id.timer);
        resend_otp = (TextView) findViewById(R.id.resend_otp);
        resend_otp.setEnabled(false);
        resend_otp.setOnClickListener(this);
        optVerificationLayout = (LinearLayout) findViewById(R.id.otp_verification_layout);
        verify.setOnClickListener(this);
        otpTextView = findViewById(R.id.otp_view);
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                phoneAuthenticator.verifyVerificationCode(otp);
            }
        });
//        otpTextView.getOtpListener();  // retrieves the current OTPListener (null if nothing is set)
//        otpTextView.requestFocusOTP();	//sets the focus to OTP box (does not open the keyboard)
//        otpTextView.setOTP(otpString);	// sets the entered otpString in the Otp box (for case when otp is retreived from SMS)
//        otpTextView.getOTP();	// retrieves the OTP entered by user (works for partial otp input too)
//        otpTextView.showSuccess();	// shows the success state to the user (can be set a bar color or drawable)
//        otpTextView.showError();	// shows the success state to the user (can be set a bar color or drawable)
//        otpTextView.resetState();	// brings the views back to default state (the state it was at input)
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.verify_done) {
            String str_phone = phone.getText().toString();
            if (TextUtils.isEmpty(str_phone)) {
                phone.setError("Enter phone number");
            } else {
                if (AppConstants.isValidNumberAcceptPtcl(str_phone)) { // remove ! when testing finished.
                    phoneAuthenticator = new PhoneAuthenticator(PhoneAuthentication.this, otpTextView, message, progressBar, optSendLayout, optVerificationLayout,str_phone, role);
                    startTimer();
                    optSendLayout.setVisibility(View.GONE);
                    optVerificationLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    sendVerificationCode(str_phone);
                } else {
                    phone.setError("Invalid phone number.");
                }
            }
        } else if (view.getId() == R.id.resend_otp) {
            String str_phone = phone.getText().toString();
            onRsendVerifyVerificationCode(str_phone);
        }
    }

    private void startTimer() {
        new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                timer.setText("" + String.format("00 : %d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                timer.setText("00:00");
                resend_otp.setTextColor(getResources().getColor(R.color.black));
                resend_otp.setEnabled(true);

            }
        }.start();
    }

    private void sendVerificationCode(String mobile) {
        startTimer();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD, phoneAuthenticator);
    }


    private void onRsendVerifyVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                phoneAuthenticator,         // OnVerificationStateChangedCallbacks
                phoneAuthenticator.getmResendToken());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
