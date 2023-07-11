package com.scan.qrgenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.scan.qrgenerator.generator.QrBatchGeneratorActivity;
import com.scan.qrgenerator.scanner.QrScannerActivity;


public class MainActivity extends AppCompatActivity {

    private int REQUEST_CAMERA_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        findViewById(R.id.btn_qr_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QrScannerActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_qr_generate_batch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QrBatchGeneratorActivity.class);
                startActivity(intent);
            }
        });


    }
}