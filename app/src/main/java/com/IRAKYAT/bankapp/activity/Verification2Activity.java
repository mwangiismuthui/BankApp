package com.IRAKYAT.bankapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.IRAKYAT.bankapp.R;
import com.IRAKYAT.bankapp.databinding.ActivityVerification2Binding;
import com.IRAKYAT.bankapp.fragments.FingerPrintVerificationFragment;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Verification2Activity extends AppCompatActivity implements FingerPrintVerificationFragment.OnFragmentInteractionListener {
    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private String password;
    private String phoneNumber;
    private String cpassword;
    private String phone;
    private String dailyLimit;
    private String email;
    private String type;
    private String amount;
    private String bank;
    private String accountNumber;
    private String company;
    private String referenceNote;
    private String beneficiary;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth getmAuth = FirebaseAuth.getInstance();
    // [START declare_auth]
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FrameLayout fragmentContainer;
    private ActivityVerification2Binding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_verification2);
        initButtons();
        getphoneFirestore();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            password = (String) extras.getSerializable("password");
            cpassword = (String) extras.getSerializable("cpassword");
            phone = (String) extras.getSerializable("phone");
            dailyLimit = (String) extras.getSerializable("dailyLimit");
            beneficiary = (String) extras.getSerializable("beneficiary");
            referenceNote = (String) extras.getSerializable("referenceNote");
            company = (String) extras.getSerializable("company");
            accountNumber = (String) extras.getSerializable("accountNumber");
            bank = (String) extras.getSerializable("bank");
            amount = (String) extras.getSerializable("amount");
            type = (String) extras.getSerializable("type");
            email = (String) extras.getSerializable("email");
        }

//        Setting up the fragment
        fragmentContainer = findViewById(R.id.fragment_container);


        getmAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [END_EXCLUDE]
                Toast.makeText(Verification2Activity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(Verification2Activity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
//                    b.fieldPhoneNumber.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(Verification2Activity.this, "Sms Quota has been exceeded!", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                Toast.makeText(Verification2Activity.this, "Code has been sent", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
//        startPhoneNumberVerification("+254721257308");

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        // [END phone_auth_callbacks]
    }

    private void initButtons() {
        b.fingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Verification2Activity.this, FingerPrintActivity.class));
//                finish();
                Log.e(TAG, "onClick: Clicked");
                openFragment("Hello Fragment");
            }
        });
        b.requestOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPhoneNumberVerification(phoneNumber);

            }
        });
//        b.resend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resendVerificationCode(phoneNumber, mResendToken);
//            }
//        });
        b.verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = b.otpField.getText().toString().trim();
                if (otp.isEmpty()) {
                    b.otpField.setError("Kindly Enter the OTP");
                    b.otpField.requestFocus();
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, otp);
                }

            }
        });

    }

    private void openFragment(String text) {

        FingerPrintVerificationFragment fingerPrintVerificationFragment = FingerPrintVerificationFragment.newInstance(text);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragment_container, fingerPrintVerificationFragment, "FingerPrint Verification Fragment").commit();


    }


    private void getphoneFirestore() {
        Log.e(TAG, "getphoneFirestore: Getting Phone number");

        DocumentReference docRef = db.collection("users").document(getmAuth.getUid());
        Log.e(TAG, "UserID" + getmAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> userMap = (Map<String, Object>) document.getData();
                        phoneNumber = (String) userMap.get("phone");
                        Log.e(TAG, "Balance: " + phoneNumber);

//                        startPhoneNumberVerification(phoneNumber);
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });


    }


//    // [START on_start_check_user]
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = getmAuth.getCurrentUser();
//        getphoneFirestore();
//
//        // [START_EXCLUDE]
//        if (mVerificationInProgress && validatePhoneNumber()) {
////            startPhoneNumberVerification("+254721257308");
//        }
//        // [END_EXCLUDE]
//    }
//    // [END on_start_check_user]
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
//    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(getmAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(getmAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]


    private boolean validatePhoneNumber() {
        if (phoneNumber == "") {
            return false;
        }

        return true;
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.e(TAG, "signInWithPhoneAuthCredential: Credential" + credential);
        getmAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
//                Toast.makeText(Verification2Activity.this, "Linking Successfull", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: Linking Succesfull");

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("password", password);
//                resultIntent.putExtra("cpassword", cpassword);
//                resultIntent.putExtra("phone", phone);
//                resultIntent.putExtra("dailyLimit", dailyLimit);
//                resultIntent.putExtra("email", email);
//                resultIntent.putExtra("company", company);
//                resultIntent.putExtra("beneficiary", beneficiary);
//                resultIntent.putExtra("referenceNote", referenceNote);
//                resultIntent.putExtra("accountNumber", accountNumber);
//                resultIntent.putExtra("bank", bank);
//                resultIntent.putExtra("type", type);
//                resultIntent.putExtra("amount", amount);
//////                startActivity(new Intent(Verification2Activity.this, MainActivity.class));
//
////                Toast.makeText(Verification2Activity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: Linking not Succesfull");
//                setResult(RESULT_CANCELED, resultIntent);
//                finish();
            }
        });
        getmAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            // [START_EXCLUDE]
                            Toast.makeText(Verification2Activity.this, "Verification Successfull", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("password", password);
                            resultIntent.putExtra("cpassword", cpassword);
                            resultIntent.putExtra("phone", phone);
                            resultIntent.putExtra("dailyLimit", dailyLimit);
                            resultIntent.putExtra("email", email);
                            resultIntent.putExtra("company", company);
                            resultIntent.putExtra("beneficiary", beneficiary);
                            resultIntent.putExtra("referenceNote", referenceNote);
                            resultIntent.putExtra("accountNumber", accountNumber);
                            resultIntent.putExtra("bank", bank);
                            resultIntent.putExtra("type", type);
                            resultIntent.putExtra("amount", amount);
//                startActivity(new Intent(Verification2Activity.this, MainActivity.class));
                            setResult(RESULT_OK, resultIntent);
                            finish();
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(Verification2Activity.this, "Invalid Code", Toast.LENGTH_SHORT).show();

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("password", password);
                                resultIntent.putExtra("cpassword", cpassword);
                                resultIntent.putExtra("phone", phone);
                                resultIntent.putExtra("dailyLimit", dailyLimit);
                                resultIntent.putExtra("email", email);
                                resultIntent.putExtra("company", company);
                                resultIntent.putExtra("beneficiary", beneficiary);
                                resultIntent.putExtra("referenceNote", referenceNote);
                                resultIntent.putExtra("accountNumber", accountNumber);
                                resultIntent.putExtra("bank", bank);
                                resultIntent.putExtra("type", type);
                                resultIntent.putExtra("amount", amount);
//                startActivity(new Intent(Verification2Activity.this, MainActivity.class));
                                setResult(RESULT_CANCELED, resultIntent);
                                finish();
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            // [END_EXCLUDE]
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("password", password);
                resultIntent.putExtra("cpassword", cpassword);
                resultIntent.putExtra("phone", phone);
                resultIntent.putExtra("dailyLimit", dailyLimit);
                resultIntent.putExtra("email", email);
                resultIntent.putExtra("company", company);
                resultIntent.putExtra("beneficiary", beneficiary);
                resultIntent.putExtra("referenceNote", referenceNote);
                resultIntent.putExtra("accountNumber", accountNumber);
                resultIntent.putExtra("bank", bank);
                resultIntent.putExtra("type", type);
                resultIntent.putExtra("amount", amount);
//                startActivity(new Intent(Verification2Activity.this, MainActivity.class));
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                Log.e(TAG, "onFailure:Sign In failure " + e.getMessage());
            }
        });
    }


    @Override
    public void onFragmentInteraction(String sendBackText) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("password", password);
        resultIntent.putExtra("cpassword", cpassword);
        resultIntent.putExtra("phone", phone);
        resultIntent.putExtra("dailyLimit", dailyLimit);
        resultIntent.putExtra("email", email);
        resultIntent.putExtra("company", company);
        resultIntent.putExtra("beneficiary", beneficiary);
        resultIntent.putExtra("referenceNote", referenceNote);
        resultIntent.putExtra("accountNumber", accountNumber);
        resultIntent.putExtra("bank", bank);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("amount", amount);
//                startActivity(new Intent(Verification2Activity.this, MainActivity.class));
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
