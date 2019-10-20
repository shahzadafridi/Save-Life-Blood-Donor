package official.com.savelife_blooddonor.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.aabhasjindal.otptextview.OtpTextView;
import official.com.savelife_blooddonor.Model.Request;
import official.com.savelife_blooddonor.Screens.BloodRequest.RequestActivity;
import official.com.savelife_blooddonor.Screens.MainActivity;
import official.com.savelife_blooddonor.Screens.Registration.DoneeRegisterActivity;
import official.com.savelife_blooddonor.Screens.Registration.DonorRegisterActivity;

public class PhoneAuthenticator extends PhoneAuthProvider.OnVerificationStateChangedCallbacks implements OnCompleteListener {

    Context context;
    OtpTextView otpTextView;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    TextView message;
    ProgressBar progressBar;
    RelativeLayout optSendLayout;
    LinearLayout optVerificationLayout;
    String role, phone, requester_status;
    String TAG = "PhoneAuthenticator";
    boolean isDoneeExists = false, isDonorExists = false;

    public PhoneAuthenticator(Context context, OtpTextView otpTextView, TextView message, ProgressBar progressBar, RelativeLayout optSendLayout, LinearLayout optVerificationLayout, String str_phone, String role, String requester_status) {
        this.context = context;
        this.otpTextView = otpTextView;
        this.message = message;
        this.progressBar = progressBar;
        this.optSendLayout = optSendLayout;
        this.optVerificationLayout = optVerificationLayout;
        this.role = role;
        this.phone = str_phone;
        this.requester_status = requester_status;
    }

    @Override
    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        String code = phoneAuthCredential.getSmsCode();
        if (code != null) {
            progressBar.setVisibility(View.GONE);
//            otpTextView.setOTP(code);
//            verifyVerificationCode(code);
        }
    }

    @Override
    public void onVerificationFailed(@NonNull FirebaseException e) {
        Log.e(TAG, "onVerificationFailed" + e);
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "Invalid Credential Exception");
        } else if (e instanceof FirebaseTooManyRequestsException) {
            Log.e(TAG, "Too Many Request Exception");
        }

    }

    @Override
    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(s, forceResendingToken);
        Log.e(TAG, "Verification id: " + s + " - Resend token:" + forceResendingToken.toString());
        mVerificationId = s;
        setmResendToken(forceResendingToken);
        mResendToken = forceResendingToken;
    }

    public void verifyVerificationCode(String code) {
        Log.e(TAG, "Verification code:" + code + " - Verification id:" + mVerificationId);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(((Activity) context), this);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()) {
            Log.e(TAG, "Verification successful");
            //verification successful we will start the profile activity
            otpTextView.showSuccess();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (role.contentEquals("donor")) {
                        checkDonorExists(phone);
                    } else if (role.contentEquals("donee")) {
                        checkDoneeExists(phone);
                    }
                }
            }, 3000);

        } else {
            Log.e(TAG, "Verification unsuccessful");
            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                message.setText("Invalid code entered.");
            }
        }
    }


    private void checkDoneeExists(String str_phone) {
        AppConstants.getSharedPrefEditor(context, "SESSION")
                .putBoolean("isLogin", true)
                .putString("role", "donee")
                .putString("phone", phone)
                .commit();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Donee");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    if (snapshot.hasChild(str_phone)) {
                        Log.e(TAG, "Donee exists");
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        Log.e(TAG, "Donee not exists");
                        Intent intent = new Intent(context, DoneeRegisterActivity.class);
                        intent.putExtra("phone", "+92" + phone);
                        context.startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "Donee not exists");
                    Intent intent = new Intent(context, DoneeRegisterActivity.class);
                    intent.putExtra("phone", "+92" + phone);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void checkDonorExists(String str_phone) {
        AppConstants.getSharedPrefEditor(context, "SESSION")
                .putBoolean("isLogin", true)
                .putString("role", "donor")
                .putString("phone", phone)
                .commit();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Donor");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    if (snapshot.hasChild(str_phone)) {
                        Log.e(TAG, "Donor exists");
                        Intent intent;
                        intent = new Intent(context, MainActivity.class);
                        if (requester_status != null) {
                            intent = new Intent(context, RequestActivity.class);
                        }
                        context.startActivity(intent);
                    } else {
                        Log.e(TAG, "Donor not exists");
                        Intent intent = new Intent(context, DonorRegisterActivity.class);
                        intent.putExtra("contact", requester_status);
                        intent.putExtra("phone", "+92" + phone);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                    }
                } else {
                    Log.e(TAG, "Donor not exists");
                    Intent intent = new Intent(context, DonorRegisterActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("phone", "+92" + phone);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    public PhoneAuthProvider.ForceResendingToken getmResendToken() {
        return mResendToken;
    }

    public void setmResendToken(PhoneAuthProvider.ForceResendingToken mResendToken) {
        this.mResendToken = mResendToken;
    }

}
