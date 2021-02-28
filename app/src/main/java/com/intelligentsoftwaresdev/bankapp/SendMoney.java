package com.intelligentsoftwaresdev.bankapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelligentsoftwaresdev.bankapp.databinding.ActivityPayBillBinding;

import java.util.HashMap;
import java.util.Map;

public class SendMoney extends AppCompatActivity {
    ActivityPayBillBinding b;

    private FirebaseAuth mAuth;
    private String TAG = "";
    private String userId = "";
    private String bank = "";
    private  Integer balance;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        b = DataBindingUtil.setContentView(this,R.layout.activity_pay_bill);
        initComponent();
        getDataFirestore();
        b.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paybill();
            }
        });
    }
    private void initComponent() {


        (findViewById(R.id.bank)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankDialog(v);
            }
        });

    }
    private void BankDialog(final View v) {
        final String[] array = new String[]{
                "Maybank", "CIMB", "RHB", "RHB", "HSBC Bank", "Standard Chartered Bank."
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bank");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void paybill() {
        String amount = b.amount.getText().toString().trim();
         bank = b.bank.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            b.amount.setError("Account Number is required");
        } else if (TextUtils.isEmpty(bank)) {
            b.bank.setError("Bank  is required");
        } else {
            DocumentReference documentReference = db.collection("transactions").document(mAuth.getUid());
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("type", "sendmoney");
            transaction.put("amount", amount);
            transaction.put("bank", bank);
            documentReference.set(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    updateBalance();
                    Toast.makeText(SendMoney.this, "Bill Paid Succesfully", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    private void updateBalance() {

      String  amount = b.amount.getText().toString().trim();
        Log.e(TAG, "updateBalance: "+amount);
        Log.e(TAG, "updateBalance: "+balance);
        Toast.makeText(this, amount, Toast.LENGTH_SHORT).show();
        Integer newbalance = balance - (Integer.parseInt(amount));
//        register user details into firestore
        DocumentReference documentReference = db.collection("users").document(mAuth.getUid());
        Map<String, Object> user = new HashMap<>();
        user.put("balance", newbalance);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User created succesfully");
                Toast.makeText(SendMoney.this, "Bill Paid", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SendMoney.this,MainActivity.class));
            }
        });



    }

    private void getDataFirestore() {
        db.collection("users")
                .document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.e(TAG, "onComplete: ");
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
//                                Toast.makeText(MainActivity.this, "Firestore"+document.getData(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());//see log below
                                Map<String, Object> myMap = (Map<String, Object>) document.getData();
                                Log.e(TAG, "onComplete: "+myMap.toString() );
                                double sbalance = (double) myMap.get("balance");
                                balance =((int)Math.round(sbalance));
                                Log.e(TAG, "onComplete: " + balance);
                            } else {
                                Log.e(TAG, "No such document");
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendMoney.this, "Firestore"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toast.makeText(SendMoney.this, "cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}