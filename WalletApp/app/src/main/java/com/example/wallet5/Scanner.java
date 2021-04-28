package com.example.wallet5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference transactionRef = db.collection("wallet").document("transactions");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this,scannView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String submittedCode = result.getText();
                        transactionRef.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()) {
                                            String depositqr = documentSnapshot.getString("depositqr");
                                            String withdrawqr = documentSnapshot.getString("withdrawqr");
                                            if(submittedCode.equals(depositqr)){
                                                openDepositDialog();
                                            }
                                            if(submittedCode.equals(withdrawqr)){
                                                openWithdrawDialog();
                                            }
                                            if(!submittedCode.equals(withdrawqr) && !submittedCode.equals(depositqr)){
                                                Toast.makeText(Scanner.this,"QR Code did not match the wallet, please try again",Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            }
                                        }
                                        else{
                                            Toast.makeText(Scanner.this,"Could not connect to Firebase",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    public void openDepositDialog() {
        DepositDialog depositDialog = new DepositDialog();
        depositDialog.show(getSupportFragmentManager(), "deposit dialog");
    }

    public void openWithdrawDialog() {
        WithdrawDialog withdrawDialog = new WithdrawDialog();
        withdrawDialog.show(getSupportFragmentManager(), "withdraw dialog");
    }

    @Override
    protected void onResume(){
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(Scanner.this,"Camera Permission Required to Scan",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}