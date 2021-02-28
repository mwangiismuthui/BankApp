package com.intelligentsoftwaresdev.bankapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelligentsoftwaresdev.bankapp.databinding.ActivitySendMoneyBinding;
import com.intelligentsoftwaresdev.bankapp.databinding.ActivitySignUPBinding;

import java.util.HashMap;
import java.util.Map;

public class SendMoney extends AppCompatActivity {

    ActivitySendMoneyBinding b;
    private FirebaseAuth mAuth;
    private String TAG = "";
    private String userId = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        b = DataBindingUtil.setContentView(this, R.layout.activity_send_money);
        b.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoney();
            }
        });
    }

    private void sendMoney() {
        String bank = b.bank.getText().toString().trim();
        String recieverAccountNumber = b.recieverNumber.getText().toString().trim();
        String mobileNumber = b.mobilenumber.getText().toString().trim();
        String amount = b.amount.getText().toString().trim();
        if (TextUtils.isEmpty(recieverAccountNumber)) {
            b.recieverNumber.setError("Account Number is required");
        } else if (TextUtils.isEmpty(amount)) {
            b.amount.setError("Amount is required");
        } else if (TextUtils.isEmpty(mobileNumber)) {
            b.mobilenumber.setError("Phone is Required");
        } else if (TextUtils.isEmpty(bank)) {
            b.bank.setError("Bank is Required");
        } else {
            DocumentReference documentReference = db.collection("transactions").document(mAuth.getUid());
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("type", "sendMoney");
            transaction.put("amount", amount);
            transaction.put("bank", bank);
            transaction.put("recieverAccountNumber", recieverAccountNumber);
            transaction.put("mobileNumber", mobileNumber);
            documentReference.set(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(SendMoney.this, "Money Send Succesfully", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }
}