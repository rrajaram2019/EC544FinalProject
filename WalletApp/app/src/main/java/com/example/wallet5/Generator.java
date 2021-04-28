package com.example.wallet5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.UUID;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Generator extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    Button sealBtn;
    ImageView deposit;
    ImageView withdraw;
    String dep = generateString();
    String with = generateString();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference transactionRef = db.collection("wallet").document("transactions");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        sealBtn = findViewById(R.id.sealBtn);
        deposit = findViewById(R.id.qrPlaceHolder);
        withdraw = findViewById(R.id.qrPlaceHolder3);


        QRGEncoder qrgEncoderdep = new QRGEncoder(dep,null, QRGContents.Type.TEXT,500);
        try {
            Bitmap qrBits = qrgEncoderdep.encodeAsBitmap();
            deposit.setImageBitmap(qrBits);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        QRGEncoder qrgEncoderwith = new QRGEncoder(with,null, QRGContents.Type.TEXT,500);
        try {
            Bitmap qrBits = qrgEncoderwith.encodeAsBitmap();
            withdraw.setImageBitmap(qrBits);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        sealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("wallet").document("transactions")
                        .update(
                                "sealed", true,
                                "depositqr", dep,
                                "withdrawqr", with
                            );
                Toast.makeText(Generator.this,"Wallet Sealed! You will be redirected to main page in a moment.",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
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