package com.example.wallet5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    Button generateBtn,scanBtn,lockBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference transactionRef = db.collection("wallet").document("transactions");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateBtn=findViewById(R.id.sealBtn);
        scanBtn=findViewById(R.id.scanBtn);
        lockBtn=findViewById(R.id.lockBtn);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {
                                    boolean sealed = documentSnapshot.getBoolean("sealed");
                                    if(sealed){
                                        Toast.makeText(MainActivity.this,"Wallet has been sealed",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        startActivity(new Intent(getApplicationContext(),Generator.class));
                                    }
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {
                                    boolean sealed = documentSnapshot.getBoolean("sealed");
                                    if(sealed){
                                        db.collection("wallet").document("transactions")
                                                .update(
                                                        "deposit", false,
                                                        "withdraw", false,
                                                        "amount", 0
                                                );
                                        Toast.makeText(MainActivity.this,"Wallet Locked!",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,"Generate/Print QR Codes and Seal Wallet first",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
                transactionRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {
                                    boolean sealed = documentSnapshot.getBoolean("sealed");
                                    if(sealed){
                                        startActivity(new Intent(getApplicationContext(),Scanner.class));
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,"Generate/Print QR Codes and Seal Wallet first",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void askCameraPermissions() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Camera Permission is Required to Use QR Scanner",Toast.LENGTH_SHORT).show();
            }
        }
    }
}